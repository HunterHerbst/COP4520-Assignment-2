/*
My understanding gathered from questions asked in class, is that I've completely tackled this the wrong way.
Please find better wording for the assignments. Because it's kind of ridiculous. Even after all these questions
asked, I still don't understand how to go about this.

I finally settled on
* Guest 0 is the only one who orders more cupcakes
* Guest 0 tracks the number of cupcakes they had to order
* Any other guest will only ever eat one singular cupcake, and never reorder one if it's missing
* When Guest 0 has reordered N-1 cupcakes, he tells the Minotaur they're done

In order to enforce and check this is accurate, after Guest 0 notifies completion, every guest's `ateCupcake` flag is checked
to see if it's `true`
*/

package org.example;

import java.util.Random;
import java.util.concurrent.*;

public class MinotaurParty {

    // Parameters based on premise
    private static final Object lock = new Object();
    public static final int N_GUESTS = 3;
    private static final Guest[] guestList = new Guest[N_GUESTS];
    private static boolean cupcakePresent = true;
    private static boolean allDone = false;
    private static final Random rng = new Random();

    public static boolean hasCupcake() {
        return cupcakePresent;
    }

    public static void reorderCupcake() {
        cupcakePresent = true;
    }

    public static boolean eatCupcake() {
        if(cupcakePresent) {
            cupcakePresent = false;
            return true;
        }
        else {
            return false;
        }
    }

    public static void notifyCompletion() {
        allDone = true;
    }

    public static void main(String[] args) {
        // Thread pool for fun waiting stuff
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        Semaphore s = new Semaphore(1);

        for(int i = 0; i < N_GUESTS; i++)
        {
            guestList[i] = new Guest(i, s);
        }

        // while guest 0 has not notified completion, send guests through the labyrinth randomly
        while(!allDone) {
            int guestId = rng.nextInt(N_GUESTS);
            Guest g = guestList[guestId];
            if(s.availablePermits() > 0) {
                threadPool.execute(g);
            }
            if(allDone) {
                System.out.println("All done!");
                threadPool.shutdownNow();
            }
        }

        // shutdown the thread pool
        threadPool.shutdownNow();

        // check if all guests have eaten a cupcake, except for guest 0, this is essentially checking if everyone has been through
        for(int i = 1; i < N_GUESTS; i++) {
            if(!guestList[i].hasEatenCupcake) {
                System.out.println("Guest " + i + " did not eat a cupcake.");
                System.out.println("Everybody dies now for lying");
                System.exit(1);
            }
        }
        System.out.println("All guests have eaten a cupcake.");
        System.out.println("The Minotaur is pleased.");
    }

}

// Class for Guests
class Guest extends Thread {
    private final int guestId;
    public boolean hasEatenCupcake;
    private int cupcakeTracker;
    private final Semaphore semaphore;

    public Guest(int guestId, Semaphore semaphore) {
        this.guestId = guestId;
        this.hasEatenCupcake = false;
        this.cupcakeTracker = 0;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        enterLabyrinth();
        handleCupcake();
        exitLabyrinth();
        semaphore.release();
    }

    private void enterLabyrinth() {
        System.out.print("Guest " + guestId + " entered...");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            //e.printStackTrace();
        }
    }

    private void handleCupcake() {
        if(guestId == 0) {
            if(!MinotaurParty.hasCupcake()) {
                MinotaurParty.reorderCupcake();
                this.cupcakeTracker++;
                System.out.print(" ordering...");
            }
            else {
                System.out.print(" skipping...");
            }
        }
        else {

            if(MinotaurParty.hasCupcake() && !hasEatenCupcake) {
                hasEatenCupcake = MinotaurParty.eatCupcake();
                System.out.print(" eating...");
            }
            else {
                System.out.print(" skipping...");
            }
        }
    }

    private void exitLabyrinth() {
        // if guest is 0, check if everyone has been through by comparing cupcakeTracker to N_GUESTS - 1
        if(guestId == 0) {
            if(this.cupcakeTracker == MinotaurParty.N_GUESTS - 1) {
                MinotaurParty.notifyCompletion();
                System.out.println(" done.");
            }
            else {
                System.out.println(" not done... exiting.");
            }
        }
        else {
            System.out.println(" exited the labyrinth.");
        }
    }
}

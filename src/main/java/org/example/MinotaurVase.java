package org.example;

import java.util.concurrent.*;

public class MinotaurVase {
    static int N_GUESTS = 10;

    // Blocking Thread Queue and Single Thread Executor service to process guests one at a time.
    private final BlockingQueue<Guest> q;
    private final ExecutorService exec;

    public MinotaurVase(){
        q = new LinkedBlockingQueue<>();
        exec = Executors.newSingleThreadExecutor();
    }

    // Enqueue
    public void addGuest(Guest g) {
        q.offer(g);
    }

    // Process queue (dequeue and walk them through the room)
    private void processQueue() {
        while(!q.isEmpty()) {
            try {
                Guest g = q.take();
                g.start();
                g.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    // Go bye-byes (cleaning up)
    public void shutdown() {
        exec.shutdownNow();
    }

    // Main function. Lines up the guests and let each through the room one at a time.
    public static void main(String[] args) {
        MinotaurVase mv = new MinotaurVase();
        for(int i = 0; i < N_GUESTS; i++) {
            mv.addGuest(new Guest(i));
        }
        mv.processQueue();
        mv.shutdown();
    }

}

// Guest class
class Guest extends Thread {
    private final int guestId;

    public Guest(int guestId) {
        this.guestId = guestId;
    }

    // Guest takes 500ms to look at the vase and leave.
    @Override
    public void run() {
        try {
            System.out.println("Guest " + guestId + " is entering the room");
            // wait for the vase
            Thread.sleep(500);
            System.out.println("Guest " + guestId + " is leaving the room");
        }
        catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
}

# Assignment 2

Hunter Herbst  
COP4520  
Spring 2024

## How to run

Running the program is simple. Just navigate to the directory containing `MinotaurParty.java`, and run `javac MinotaurParty.java` then `java MinotaurParty`. The program will begin outputting information, such as when a thread is dispatched, and thread completion. When done, the program will check if the results are correct and print out the outcome.

For the part 2, in the same directory, run `javac MinotaurVase.java` then `java MinotaurVase`. The program will notify when a guest enters and exits the room. Every single person will be going through the room once, and after that is done the program simply exits.

I used Java 17 for this project, but I think it should work with Java 8 or higher.

## Approach and Testing

For part 1, I used the following approach:

* Guest 0 is the only one who orders more cupcakes
* Guest 0 tracks the number of cupcakes they had to order
* Any other guest will only ever eat one singular cupcake, and never reorder one if it's missing
* When Guest 0 has reordered N-1 cupcakes, he tells the Minotaur they're done

In order to enforce and check this is accurate, after Guest 0 notifies completion, every guest's `ateCupcake` flag is checked
to see if it's `true`

For part 2, I just used a blocking queue to ensure only one person goes through at a time, and just enqueued everybody before telling the program to process the queue. 
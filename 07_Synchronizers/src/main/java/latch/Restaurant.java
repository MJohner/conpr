package latch;

import java.util.concurrent.CountDownLatch;

public class Restaurant {

    public static void main(String[] args) {
        int nrGuests = 2;
        CountDownLatch waitForCook = new CountDownLatch(1);
        CountDownLatch waitForDishes = new CountDownLatch(nrGuests);

        new Cook(waitForCook).start();

        for (int i = 0; i < nrGuests; i++) {
            new Guest(waitForCook, waitForDishes).start();
        }

        new DishWasher(waitForDishes).start();
    }


    static class Cook extends Thread {
        CountDownLatch waitForCook;
        public Cook(CountDownLatch waitForCook) {
            this.waitForCook = waitForCook;
        }

        @Override
        public void run() {
            System.out.println("Start Cooking..");
            try {
                sleep(5000);
            } catch (InterruptedException e) {
            }
            System.out.println("Meal is ready");
            waitForCook.countDown();
        }
    }


    static class Guest extends Thread {
        CountDownLatch waitForCook;
        CountDownLatch waitForDishes;
        public Guest(CountDownLatch waitForCook,CountDownLatch waitForDishes) {
            this.waitForCook = waitForCook;
            this.waitForDishes = waitForDishes;
        }

        @Override
        public void run() {
            try {
                sleep(1000);
                System.out.println("Entering restaurant and placing order.");
                waitForCook.await();
                System.out.println("Enjoying meal.");
                sleep(5000);
                System.out.println("Meal was excellent!");
                waitForDishes.countDown();
            } catch (InterruptedException e) {
            }
        }
    }


    static class DishWasher extends Thread {
        CountDownLatch waitForDishes;
        public DishWasher(CountDownLatch waitForDishes) {
            this.waitForDishes = waitForDishes;
        }

        @Override
        public void run() {
            try {
                System.out.println("Waiting for dirty dishes.");
                waitForDishes.await();
                System.out.println("Washing dishes.");
                sleep(0);
            } catch (InterruptedException e) {
            }
        }
    }
}

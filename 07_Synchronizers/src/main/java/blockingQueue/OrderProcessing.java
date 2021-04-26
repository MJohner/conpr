package blockingQueue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class OrderProcessing {

    public static void main(String[] args) {
        int nCustomers = 10;
        int nValidators = 2;
        int nProcessors = 3;
        LinkedBlockingDeque<Order> ordersOrdered = new LinkedBlockingDeque<>();
        LinkedBlockingDeque<Order> ordersValidated = new LinkedBlockingDeque<>();

        for (int i = 0; i < nCustomers; i++) {
            new Customer("" + i, ordersOrdered).start();
        }

        for (int i = 0; i < nValidators; i++) {
            new OrderValidator(ordersOrdered, ordersValidated).start();
        }

        for (int i = 0; i < nProcessors; i++) {
            new OrderProcessor(ordersValidated).start();
        }
    }

    static class Order {
        public final String customerName;
        public final int itemId;

        public Order(String customerName, int itemId) {
            this.customerName = customerName;
            this.itemId = itemId;
        }

        @Override
        public String toString() {
            return "Order: [name = " + customerName + " ], [item = " + itemId + " ]";
        }
    }


    static class Customer extends Thread {
        LinkedBlockingDeque<Order> orders;
        public Customer(String name, LinkedBlockingDeque<Order> orders) {
            super(name);
            this.orders = orders;
        }

        private Order createOrder() {
            Order o = new Order(getName(), (int) (Math.random() * 100));
            System.out.println("Created:   " + o);
            return o;
        }

        private void handOverToValidator(Order o) throws InterruptedException {
            orders.push(o);
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Order o = createOrder();
                    handOverToValidator(o);
                    Thread.sleep((long) (Math.random() * 1000));
                }
            } catch (InterruptedException e) {
            }
        }
    }


    static class OrderValidator extends Thread {
        LinkedBlockingDeque<Order> ordersOrdered;
        LinkedBlockingDeque<Order> ordersValidated;
        public OrderValidator(LinkedBlockingDeque<Order> ordersOrdered, LinkedBlockingDeque<Order> ordersValidated) {
            this.ordersOrdered = ordersOrdered;
            this.ordersValidated = ordersValidated;
        }

        public Order getNextOrder() throws InterruptedException {
            return ordersOrdered.take();
        }

        public boolean isValid(Order o) {
            return o != null && o.itemId < 50;
        }

        public void handOverToProcessor(Order o) throws InterruptedException {
            ordersValidated.put(o);
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Order o = getNextOrder();
                    if (isValid(o)) {
                        handOverToProcessor(o);
                    } else {
                        System.err.println("Destroyed: " + o);
                    }
                    Thread.sleep((long) (Math.random() * 1000));
                }
            } catch (InterruptedException e) {
            }
        }
    }


    static class OrderProcessor extends Thread {
        LinkedBlockingDeque<Order> orders;
        public OrderProcessor(LinkedBlockingDeque<Order> orders) {
            this.orders = orders;
        }

        public Order getNextOrder() throws InterruptedException {
            return orders.take();
        }

        public void processOrder(Order o) {
            System.out.println("Processed: " + o);
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Order o = getNextOrder();
                    processOrder(o);
                    Thread.sleep((long) (Math.random() * 1000));
                }
            } catch (InterruptedException e) {
            }
        }
    }
}

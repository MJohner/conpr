package worksheet;

public class MaxThreads {
    public static void main(String[] args) throws Exception {
        for(int i = 0; i< Integer.MAX_VALUE; i++){
            new Thread(new Run(i)).start();
        }
        System.out.println("#CPUs: " + Runtime.getRuntime().availableProcessors());
        System.out.println("main: done");
    }
}

class Run implements Runnable {
    private int nr;

    public Run(int nr) { this.nr = nr; }

    @Override
    public void run() {
        System.out.println("Im Number: " + nr);
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

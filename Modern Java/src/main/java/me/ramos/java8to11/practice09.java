package me.ramos.java8to11;

public class practice09 {

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            while (true) {
                System.out.println("Thread: " + Thread.currentThread().getName());
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    System.out.println("interrupted!");
//                    System.out.println("exit!");
//                    return;
                }
            }
        });
        thread.start();

        System.out.println("Hello: " + Thread.currentThread().getName());
        Thread.sleep(3000L);
        thread.interrupt();
    }

//    static class MyThread extends Thread {
//        @Override
//        public void run() {
//            System.out.println("Thread: " + Thread.currentThread().getName());
//        }
//    }
}

package module1;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class exercise1 {

    // 工作线程类：不断打印信息，每 2 秒一次
    static class MyThread extends Thread {

        private final int id;                // 线程编号
        private volatile boolean running = true;   // 用于控制线程是否继续运行
        private static final DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("HH:mm:ss");

        public MyThread(int id) {
            this.id = id;
        }

        // 主线程会调用此方法要求该线程停止
        public void stopRunning() {
            running = false;
            this.interrupt();  // 防止 sleep 卡住，促使线程醒来退出
        }

        @Override
        public void run() {
            while (running) {
                String now = LocalTime.now().format(formatter);

                System.out.println("Hello World! I'm thread "
                        + id + ". The time is " + now);

                try {
                    Thread.sleep(2000);  // 睡 2 秒
                } catch (InterruptedException e) {
                    // 如果被打断并且 running 为 false，那么退出
                    if (!running) break;
                }
            }
        }
    }

    // 主程序：管理线程 + 接收用户输入
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        Map<Integer, MyThread> threadMap = new HashMap<>();  // 保存所有线程
        int nextId = 1;                                      // 用于分配线程 id

        while (true) {

            // 显示菜单
            System.out.println("\nHere are your options:");
            System.out.println("a - Create a new thread");
            System.out.println("b - Stop a given thread (e.g. \"b 2\" kills thread 2)");
            System.out.println("c - Stop all threads and exit this program.");

            // 用户输入
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            char option = line.charAt(0);

            // a：创建新线程
            if (option == 'a') {
                int id = nextId++;
                MyThread t = new MyThread(id);
                threadMap.put(id, t);
                t.start();// 只有调用 start()，Java 才会真正创建一个新线程，并在那个新线程里执行 run() 方法
                System.out.println("Started thread " + id + ".");
            }

            // b：停止指定线程
            else if (option == 'b') {

                String[] parts = line.split("\\s+");
                if (parts.length < 2) {
                    System.out.println("Format: b <threadId>");
                    continue;
                }

                try {
                    int id = Integer.parseInt(parts[1]);
                    MyThread t = threadMap.get(id);

                    if (t == null) {
                        System.out.println("No thread with id " + id);
                    } else {
                        t.stopRunning();
                        try { t.join(); } catch (InterruptedException ignored) {}
                        threadMap.remove(id);
                        System.out.println("Stopped thread " + id + ".");
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Invalid thread id.");
                }
            }

            // c：停止所有线程并退出
            else if (option == 'c') {
                System.out.println("Stopping all threads...");

                for (MyThread t : threadMap.values())
                    t.stopRunning();

                for (MyThread t : threadMap.values()) {
                    try { t.join(); } catch (InterruptedException ignored) {}
                }

                System.out.println("All threads stopped. Exiting.");
                break;
            }

            // 其他输入
            else {
                System.out.println("Unknown option.");
            }
        }

        scanner.close();
    }
}

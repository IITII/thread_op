package threads;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WaitNotify {
    private volatile static int count = 0;
    private static ArrayList<TextField> inputArraylist;
    private static ArrayList<TextField> outputArraylist;
    Button sure, clear;
    ToggleGroup toggleGroup;

    public WaitNotify(ArrayList<TextField> inputArraylist, ArrayList<TextField> outputArraylist, Button sure, Button clear, ToggleGroup toggleGroup) {
        this.sure = sure;
        this.clear = clear;
        this.toggleGroup = toggleGroup;
        WaitNotify.inputArraylist = inputArraylist;
        WaitNotify.outputArraylist = outputArraylist;
    }

    public void run() {
        ThreadPoolExecutor factory = new ThreadPoolExecutor(5,
                10,
                5,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(),
                (ThreadFactory) Thread::new);
        factory.execute(new Run(0));
        factory.execute(new Run(1));
        factory.execute(new Run(2));
        factory.execute(() -> {
            synchronized (this) {
                while (count != 3) {
                    try {
                        this.wait(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Last.last(inputArraylist, outputArraylist);
                Last.setDisable(sure, clear, toggleGroup, false);
            }
        });
        factory.shutdown();
    }

    private static class Run implements Runnable {
        private final int run;

        public Run(int run) {
            this.run = run;
        }

        @Override
        public void run() {
            try {
                while (run != count) {
                    synchronized (this) {
                        try {
                            this.wait(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                synchronized (this) {
                    outputArraylist.get(run).setText(inputArraylist.get(run).getText());
                    Thread.sleep(1000);
                    count++;
                    this.notifyAll();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

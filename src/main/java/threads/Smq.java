package threads;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import java.util.ArrayList;
import java.util.concurrent.*;

public class Smq {
    private final static Semaphore SEMAPHORE = new Semaphore(1);
    private static volatile int count = 0;
    private static ArrayList<TextField> inputArraylist;
    private static ArrayList<TextField> outputArraylist;
    Button sure, clear;
    ToggleGroup toggleGroup;

    public Smq(ArrayList<TextField> inputArraylist, ArrayList<TextField> outputArraylist, Button sure, Button clear, ToggleGroup toggleGroup) {
        this.sure = sure;
        this.clear = clear;
        this.toggleGroup = toggleGroup;
        Smq.inputArraylist = inputArraylist;
        Smq.outputArraylist = outputArraylist;
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
    }

    private static class Run implements Runnable {
        private final int id;

        public Run(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            synchronized (this) {
                try {
                    SEMAPHORE.acquire();
                    outputArraylist.get(id).setText(inputArraylist.get(id).getText());
                    count++;
                    Thread.sleep(1000);
                    SEMAPHORE.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

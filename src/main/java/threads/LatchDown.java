package threads;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class LatchDown {
    private final static int COUNT = 4;
    private final static CountDownLatch LATCH = new CountDownLatch(COUNT);
    private static ArrayList<TextField> inputArraylist;
    private static ArrayList<TextField> outputArraylist;
    Button sure, clear;
    ToggleGroup toggleGroup;

    public LatchDown(ArrayList<TextField> inputArraylist, ArrayList<TextField> outputArraylist, Button sure, Button clear, ToggleGroup toggleGroup) {
        this.sure = sure;
        this.clear = clear;
        this.toggleGroup = toggleGroup;
        LatchDown.inputArraylist = inputArraylist;
        LatchDown.outputArraylist = outputArraylist;
    }

    public void run() {
        for (int i = 0; i < COUNT - 1; i++) {
            new Run(i).start();
        }
        new Thread(() -> {
            try {
                Thread.sleep(1000 * COUNT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Last.last(inputArraylist, outputArraylist);
            Last.setDisable(sure, clear, toggleGroup, false);
            LATCH.countDown();
        }).start();
//        try {
//            LATCH.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    private static class Run extends Thread {
        private final int run;

        public Run(int run) {
            this.run = run;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(1000 * run);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            outputArraylist.get(run).setText(inputArraylist.get(run).getText());
            LATCH.countDown();
        }
    }
}

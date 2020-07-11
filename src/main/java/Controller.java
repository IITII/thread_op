import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.ToggleGroup;

import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.*;


/**
 * @author IITII
 */
public class Controller implements Initializable {
    private final ArrayList<TextField> inputArraylist = new ArrayList<>();
    private final ArrayList<TextField> outputArraylist = new ArrayList<>();
    private final ArrayList<TextField> allArraylist = new ArrayList<>();
    /**
     * 第一个 wait, notify 里面需要用到的共享变量
     */
    private volatile static int count = 0;
    /**
     * 按钮
     */
    @FXML
    public Button sure, clear;
    /**
     * 单选框
     */
    @FXML
    public ToggleGroup toggleGroup;
    /**
     * 7个TextFiled
     * 3个输入框
     * 4个默认禁用为输出框
     */
    @FXML
    TextField input1, input2, input3, output1, output2, output3, lastOutput;

    /**
     * 睡眠一段时间
     * 范围：0~2000 ms
     */
    private static void sleep() {
        try {
            Thread.sleep(new Random().nextInt(1000) + 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 睡眠一段时间
     * 范围：0~2000*id ms
     */
    private static void sleep(int id) {
        try {
            Thread.sleep((new Random().nextInt(1000) + 1000) * id);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 睡眠一段时间
     *
     * @param ms 睡眠一段指定的时间
     */
    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 继承自 Initializable 接口, 将会在构造函数之后被调用，可以用来初始化一些值
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //输入框数组
        inputArraylist.add(input1);
        inputArraylist.add(input2);
        inputArraylist.add(input3);
        //输出框数组
        outputArraylist.add(output1);
        outputArraylist.add(output2);
        outputArraylist.add(output3);
        outputArraylist.add(lastOutput);
        //所有
        allArraylist.addAll(inputArraylist);
        allArraylist.addAll(outputArraylist);
        // 文本居中
        allArraylist.forEach(e -> e.setAlignment(Pos.CENTER));
    }

    public void main() {
        // 判空
        for (TextField e : inputArraylist) {
            if ("".equals(e.getText())) {
                lastOutput.setText("请正确填充三个输入框...");
                return;
            }
        }
        // 清空所有输出框
        outputArraylist.forEach(TextInputControl::clear);
        // 禁用按钮
        setDisable(true);
        try {
            switch ((String) toggleGroup.getSelectedToggle().getUserData()) {

                case "0":
                    // wait, notify
                    waitNotify();
                    break;
                case "1":
                    // latchDown
                    countDownLatch();
                    break;
                case "2":
                    // Semaphore
                    semaphore();
                    break;
                default:
                    break;
            }
        } catch (NumberFormatException | ClassCastException | NullPointerException e) {
            lastOutput.setText("Boom!!!");
            e.printStackTrace();
        }

    }

    /**
     * 清空所有TextFiled
     */
    public void cleanText() {
        allArraylist.forEach(TextInputControl::clear);
    }

    /**
     * 设置最后一个输出框的值
     */
    private void setLastOutput() {
        StringBuilder allTextFiledText = new StringBuilder();
        for (int i = 0, inputsSize = inputArraylist.size(); i < inputsSize; i++) {
            TextField e = inputArraylist.get(i);
            allTextFiledText.append(e.getText());
            if (i + 1 != inputsSize) {
                allTextFiledText.append(" ");
            }
        }
        synchronized (this) {
            outputArraylist.get(outputArraylist.size() - 1)
                    .setText(allTextFiledText.toString());
        }
    }

    /**
     * 设置所有按钮和输入框的状态
     *
     * @param status true:禁用, false:启用
     */
    private void setDisable(boolean status) {
        inputArraylist.forEach(e -> e.setDisable(status));
        sure.setDisable(status);
        clear.setDisable(status);
        toggleGroup.getToggles().forEach(e -> {
            Node node = (Node) e;
            node.setDisable(status);
        });
    }

    /**
     * wait,notify 调度
     */
    private void waitNotify() {
        class Task implements Runnable {
            /**
             * run 为运行的值，当传入的 run 值和共享变量 waitNotifyCount 相等时，才运行
             */
            private final int run;

            public Task(int run) {
                this.run = run;
            }

            @Override
            public void run() {
                while (run != count) {
                    synchronized (this) {
                        try {
                            // 运行速度过快，避免出现一直等待
                            this.wait(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                synchronized (this) {
                    outputArraylist.get(run)
                            .setText(
                                    inputArraylist.get(run).getText()
                            );
                    sleep();
                    count++;
                    this.notifyAll();
                }
            }
        }

        // 避免因为反复执行导致 count 值一直增加
        synchronized (this) {
            count = 0;
        }
        ThreadPoolExecutor factory = new ThreadPoolExecutor(5,
                10,
                5,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(),
                (ThreadFactory) Thread::new);
        ThreadPoolExecutor factory1 = new ThreadPoolExecutor(5,
                10,
                5,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(),
                (ThreadFactory) Thread::new);
        for (int i = 0; i < inputArraylist.size(); i++) {
            factory.execute(new Task(i));
        }
        factory1.execute(() -> {
            factory.shutdown();
            //等待直到所有任务完成
            try {
                factory.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            setLastOutput();
            setDisable(false);
            factory1.shutdownNow();
        });
    }

    /**
     * CountDownLatch 调度
     */
    private void countDownLatch() {
        CountDownLatch countDownLatch = new CountDownLatch(inputArraylist.size());
        ThreadPoolExecutor factory = new ThreadPoolExecutor(5,
                10,
                5,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(),
                (ThreadFactory) Thread::new);
        ThreadPoolExecutor factory1 = new ThreadPoolExecutor(5,
                10,
                5,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(),
                (ThreadFactory) Thread::new);
        class Task implements Runnable {
            private final int run;

            public Task(int run) {
                this.run = run;
            }

            @Override
            public void run() {
                sleep(run);
                outputArraylist.get(run).setText(inputArraylist.get(run).getText());
                // 每次减一
                countDownLatch.countDown();
            }
        }
        for (int i = 0; i < inputArraylist.size(); i++) {
            factory.execute(new Task(i));
        }
        factory1.execute(() -> {
            //等待直到所有任务完成
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            setLastOutput();
            setDisable(false);
            factory.shutdownNow();
            factory1.shutdownNow();
        });
    }

    /**
     * Semaphore 调度
     * 计数信号量用来控制同时访问某个特定资源的操作数量，或者同时执行某个指定操作的数量。
     * 信号量还可以用来实现某种资源池，或者对容器施加边界。
     * Semaphore管理着一组许可（permit）,许可的初始数量可以通过构造函数设定，
     * 操作时首先要获取到许可，才能进行操作，操作完成后需要释放许可。
     * 如果没有获取许可，则阻塞到有许可被释放。
     * 如果初始化了一个许可为1的Semaphore，那么就相当于一个不可重入的互斥锁
     */
    private void semaphore() {
        // 避免因为反复执行导致 count 值一直增加
        synchronized (this) {
            count = 0;
        }
        Semaphore semaphore = new Semaphore(1);
        class Task implements Runnable {
            private final int id;

            public Task(int id) {
                this.id = id;
            }

            @Override
            public void run() {
                synchronized (this) {
                    try {
                        semaphore.acquire();
                        outputArraylist.get(id).setText(inputArraylist.get(id).getText());
                        count++;
                        sleep();
                        semaphore.release();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        ThreadPoolExecutor factory = new ThreadPoolExecutor(5,
                10,
                5,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(),
                (ThreadFactory) Thread::new);
        ThreadPoolExecutor factory1 = new ThreadPoolExecutor(5,
                10,
                5,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(),
                (ThreadFactory) Thread::new);
        for (int i = 0; i < inputArraylist.size(); i++) {
            factory.execute(new Task(i));
        }
        factory1.execute(() -> {
            //等待直到所有任务完成
            synchronized (this) {
                while (count != inputArraylist.size()) {
                    try {
                        this.wait(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
            setLastOutput();
            setDisable(false);
            factory.shutdownNow();
            factory1.shutdownNow();
        });
    }
}

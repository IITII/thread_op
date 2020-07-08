import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.ToggleGroup;
import threads.Last;
import threads.LatchDown;
import threads.Smq;
import threads.WaitNotify;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class Controller implements Initializable {
    private final ArrayList<TextField> inputArraylist = new ArrayList<>();
    private final ArrayList<TextField> outputArraylist = new ArrayList<>();
    private final ArrayList<TextField> allArraylist = new ArrayList<>();
    @FXML
    public Button sure, clear;
    @FXML
    public ToggleGroup toggleGroup;
    @FXML
    TextField input1, input2, input3, output1, output2, output3, lastOutput;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 文本居中
        output1.setAlignment(Pos.CENTER);
        output2.setAlignment(Pos.CENTER);
        output3.setAlignment(Pos.CENTER);
        lastOutput.setAlignment(Pos.CENTER);
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
    }

    public void waitNotify() {
        for (TextField e : inputArraylist) {
            if ("".equals(e.getText())) {
                lastOutput.setText("请正确填充三个输入框...");
                return;
            }
        }
        try {
            outputArraylist.forEach(TextInputControl::clear);
            Last.setDisable(sure, clear, toggleGroup, true);
            switch (Integer.parseInt((String) toggleGroup.getSelectedToggle().getUserData())) {
                case 0:
                    new WaitNotify(inputArraylist, outputArraylist, sure, clear, toggleGroup).run();
                    break;
                case 1:
                    new LatchDown(inputArraylist, outputArraylist, sure, clear, toggleGroup).run();
                    break;
                case 2:
                    new Smq(inputArraylist, outputArraylist, sure, clear, toggleGroup).run();
                    break;
                default:
                    break;
            }
        } catch (NumberFormatException | ClassCastException | NullPointerException e) {
            lastOutput.setText("Boom!!!");
            e.printStackTrace();
        }

    }

    public void cleanText() {
        allArraylist.forEach(TextInputControl::clear);
    }
}

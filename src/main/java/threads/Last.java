package threads;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import java.util.ArrayList;

public class Last {
    public static void last(ArrayList<TextField> inlist, ArrayList<TextField> outputList) {
        StringBuilder last = new StringBuilder();
        for (int i = 0, inputsSize = inlist.size(); i < inputsSize; i++) {
            TextField e = inlist.get(i);
            last.append(e.getText());
            if (i + 1 != inputsSize) {
                last.append(" ");
            }
        }
        outputList.get(outputList.size() - 1).setText(last.toString());
    }

    /**
     * @param status true 禁用按钮，false 激活按钮
     */
    public static void setDisable(Button sure, Button clear, ToggleGroup toggleGroup, boolean status) {
        sure.setDisable(status);
        clear.setDisable(status);
        toggleGroup.getToggles().forEach(e -> {
            Node node = (Node) e;
            node.setDisable(status);
        });
    }
}

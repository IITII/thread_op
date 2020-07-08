import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    final String title = "线程并发控制";
    final int defaultHeight = 150;
    final int defaultWidth = 750;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.setTitle(title);
        Scene scene = new Scene(root, defaultWidth, defaultHeight);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

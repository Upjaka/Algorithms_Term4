package View;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    private static Stage primary;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/StartScreen.fxml"));
        Scene startScene = new Scene(root, 450, 730);
        primaryStage.setScene(startScene);
        primaryStage.setTitle("Variant 30");
        primaryStage.getIcons().add(new Image("game_controller_icon.png"));
        primary = primaryStage;
        primaryStage.show();
    }

    public static Stage getPrimary() {
        return primary;
    }
}
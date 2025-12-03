import DAO.SQLFilesDAO;
import POJO.impl.File;
import POJO.impl.UserImpl;
import service.impl.ActionImpl;
import javafx.fxml.FXMLLoader;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                Main.class.getResource("/fxml/Controller.fxml")
        );
        Scene scene = new Scene(fxmlLoader.load(), 780, 320);
        stage.setTitle("Продуктовый магазин");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {



        launch(args);
    }
}
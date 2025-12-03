import DAO.SQLFilesDAO;
import POJO.impl.File;
import POJO.impl.UserImpl;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import service.impl.ActionImpl;

import java.io.IOException;
public class ControllerFX {
    @FXML
    private TextField inputField;
    @FXML
    private Button sendButton;
    @FXML
    private TextArea outputArea;
    private UserImpl user;
    private ActionImpl action = new ActionImpl();
    @FXML
    private void initialize() {
        SQLFilesDAO.connect();
        user = SQLFilesDAO.getUsers().stream()
                .filter(u -> u.getId() == 4)
                .findFirst()
                .orElse(null);

        sendButton.setOnAction(event -> {
            String text = inputField.getText();
            String command = "";
            if (!text.isEmpty()) {
                inputField.clear();  // Очищаем поле после отправки
                for (char c : text.toCharArray()) {
                    if (c == ' ') {
                        break;
                    }
                    else command += c;
                }
                switch (command) {
                    case "mkdir":
                        addFile(null, null);
                        break;
                    case "ls":
                        ls();
                        break;
                    case "cd":
                        cd(null);
                        break;
                    case "pwd":
                        pwd();
                 }

            }
        });

        // ИЛИ Способ 2: Обработчик через ссылку на метод
        // sendButton.setOnAction(this::handleButtonClick);
    }
    public void addFile(String path, String type){
        if (type == null){
            type = "package";
        }
        if (path == null){
            path = "/home";
        }
        File file = new File("users", path, type);
        user.setLocation("/home");
        SQLFilesDAO.addFile(file, user);
    }

    public void ls(){
        for (String elem : action.ls(user))
            outputArea.appendText(elem + '\n');

    }

    public void cd(String path){
        if (path == null){
            path = "/home";
        }
        user = action.cd("users", user);
        System.out.println(user.getlocation());
    }

    public void pwd(){
        for (String elem : user.getlocation())
            outputArea.appendText(elem);
        outputArea.appendText("\n");
    }
}

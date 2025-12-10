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
import supportive.Support;

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
    private boolean commandChek = false;
    private boolean ParhChek = false;
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
            String path = "";
            String pathSecond = "";
            boolean typeFile = false;
            if (!text.isEmpty()) {
                inputField.clear();  // Очищаем поле после отправки
                for (char c : text.toCharArray()) {
                    if (c == ' ') {
                        if (commandChek){
                            ParhChek = true;
                        }
                        commandChek = true;
                    }
                    else if (!commandChek) command += c;
                    else if (ParhChek)  pathSecond += c;
                    else path += c;
                }
                commandChek = false;
                ParhChek = false;
                if (Support.FindElem(path, ".") != null)
                    typeFile = true;
                switch (command) {
                    case "mkdir":
                        if (typeFile)
                            addFile(path, "file");
                        else addFile(path, null);
                        break;
                    case "ls":
                        outputArea.appendText(command + " " + path + ": ");
                        if (!path.isEmpty())
                            ls(path);
                        else ls();
                        break;
                    case "cd":
                        cd(path);
                        break;
                    case "pwd":
                        outputArea.appendText(command + " " + path + ": ");
                        pwd();
                        break;
                    case "clear":
                        clear();
                        break;
                    case "rm":
                        delete(path);
                        break;
                    case "mv":
                        move(path, pathSecond);
                        break;
                    case "reName":
                        reName(path, pathSecond);
                        break;
                    case "nano":
                        nano(path, pathSecond);
                        break;
                    case "cat":
                        cat(path);
                        break;
                 }

            }
        });
    }

    public void addFile(String path, String type){
        if (type == null){
            type = "package";
        }
        if (path == null){
            path = "/home";
        }
        File file = new File(path, null, type);
        SQLFilesDAO.addFile(user.getlocation(), file);
    }

    public void ls(){
        for (String elem : action.ls(user, null))
            outputArea.appendText(elem + '\n');

    }

    public void ls(String path){
        for (String elem : action.ls(user, path))
            outputArea.appendText(elem + '\n');

    }

    public void cd(String path){
        if (path == null || path.isEmpty()){
            path = "/home";
        }
        user = action.cd(path, user);
        System.out.println(user.getlocation());
    }

    public void pwd(){
        for (String elem : user.getlocation())
            outputArea.appendText(elem);
        outputArea.appendText("\n");
    }

    public void clear(){
        outputArea.clear();
    }

    public void delete(String file){
        action.delete(user.getlocation(), file);
    }

    public void move(String file, String path){
        action.move(user.getlocation(), file, path);
    }

    public void reName(String file, String newName){
        action.reName(user.getlocation(), file, newName);
        user = action.cd("..", user);
    }

    public void nano(String file, String text){
        action.nano(user.getlocation(), file, text);
    }

    public void cat(String file){
        outputArea.appendText("cat " + file + ": ");
        outputArea.appendText(action.cat(user.getlocation(), file));
        outputArea.appendText("\n");
    }

}

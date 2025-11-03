import DAO.SQLFilesDAO;
import POJO.impl.File;
import POJO.impl.UserImpl;

public class Main {
    public static void main(String[] args) {

        SQLFilesDAO.connect();
        File file = new File("users", "/home", "package");
        UserImpl user = SQLFilesDAO.getUsers().stream()
                .filter(u -> u.getName().equals("root"))
                .findFirst()
                .orElse(null);
        user.setLocation("/home");
        SQLFilesDAO.addFile(file, user);
    }
}
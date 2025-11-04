import DAO.SQLFilesDAO;
import POJO.impl.File;
import POJO.impl.UserImpl;
import service.impl.ActionImpl;

public class Main {
    public static void main(String[] args) {

        SQLFilesDAO.connect();
        File file = new File("users", "/home", "package");
        UserImpl user = SQLFilesDAO.getUsers().stream()
                .filter(u -> u.getId() == 4)
                .findFirst()
                .orElse(null);
        if (user != null) {
            user.setLocation("/home");
            SQLFilesDAO.addFile(file, user);
            ActionImpl action = new ActionImpl();
            for (String elem : action.ls(user))
                System.out.println(elem);
            user = action.cd("userss", user);
            System.out.println(user.getlocation());
        } else {
            System.out.println("User not found");
        }

    }
}
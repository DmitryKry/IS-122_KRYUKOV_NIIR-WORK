package service.impl;

import DAO.SQLFilesDAO;
import POJO.User;
import POJO.impl.File;
import POJO.impl.UserImpl;
import service.ActionService;

import java.util.ArrayList;
import java.util.List;

public class ActionImpl implements ActionService {
    private List<String> namesOfFiles;
    private List<File> filesOfOwner;
    private List<File> files;
    private File file;
    @Override
    public UserImpl cd(String act, UserImpl user) {
        user.setLocation(act);
        return user;
    }

    @Override
    public List<String> ls(UserImpl user, String path) {
        if (path != null) {
            return SQLFilesDAO.ls(path);
        }
        namesOfFiles = new ArrayList<String>();
        String TempForOwnLocale = "";
        for (int i = 0; i < user.getlocation().size() - 2; i++) {
            TempForOwnLocale += user.getlocation().get(i);
        }
        if (TempForOwnLocale.equals("")) {
            TempForOwnLocale = "/";
        }
        String ownLocale = TempForOwnLocale;
        file = SQLFilesDAO.getLocals().stream().filter(
                        t -> t.getName().equals(user.getlocation().get(user.getlocation().size() - 1)) &&
                                t.getPath().equals(ownLocale))
                .findFirst().orElse(null);
        filesOfOwner = SQLFilesDAO.getOwnLocale(file.getId());
        for (int i = 0; i < filesOfOwner.size(); i++) {
            namesOfFiles.add(filesOfOwner.get(i).getName());
        }
        return namesOfFiles;
    }

    @Override
    public void delete(List<String> ownerFiles,String file){
        SQLFilesDAO.deleteFile(ownerFiles, file);
    }

    @Override
    public void move(List<String> ownerFiles,String file, String path){
        SQLFilesDAO.move(ownerFiles, file, path);
    }

    @Override
    public void reName(List<String> ownerFiles, String file, String newName) {
        SQLFilesDAO.reName(ownerFiles, file, newName);
    }

    @Override
    public void nano(List<String> ownerFiles, String file, String text) {
        SQLFilesDAO.nano(ownerFiles, file, text);
    }

}

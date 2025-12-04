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
    public List<String> ls(UserImpl user) {
        namesOfFiles = new ArrayList<String>();
        file = SQLFilesDAO.getLocals().stream().filter(
                t -> t.getName().equals(user.getlocation().get(user.getlocation().size() - 1)))
                .findFirst().orElse(null);
        filesOfOwner = SQLFilesDAO.getOwnLocale(file.getId());
        for (int i = 0; i < filesOfOwner.size(); i++) {
            namesOfFiles.add(filesOfOwner.get(i).getName());
        }
        return namesOfFiles;
    }

    @Override
    public List<String> ls(String file) {
        return List.of();
    }

    @Override
    public void delete(String file){
        SQLFilesDAO.deleteFile(file);
    }

}

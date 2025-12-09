package service;

import POJO.User;
import POJO.impl.UserImpl;

import java.util.List;

public interface ActionService {
    User cd(String act, UserImpl user);
    List<String> ls(UserImpl user, String path);
    void delete(List<String> ownerFiles,String file);
    void move(List<String> ownerFiles, String file, String path);
    void reName(List<String> ownerFiles, String file, String newName);
}

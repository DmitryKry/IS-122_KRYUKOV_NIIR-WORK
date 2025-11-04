package service;

import POJO.User;
import POJO.impl.UserImpl;

import java.util.List;

public interface ActionService {
    User cd(String act, UserImpl user);
    List<String> ls(UserImpl user);
    List<String> ls(String file);
}

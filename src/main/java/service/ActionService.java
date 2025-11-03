package service;

import POJO.User;

import java.util.List;

public interface ActionService {
    User cd(String act, User user);
    List<String> ls();
    List<String> ls(String file);
}

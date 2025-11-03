package service.impl;

import POJO.User;
import service.ActionService;

import java.util.List;

public class ActionImpl implements ActionService {

    @Override
    public User cd(String act, User user) {
        user.setLocation();
        return user;
    }

    @Override
    public List<String> ls() {
        return List.of();
    }

    @Override
    public List<String> ls(String file) {
        return List.of();
    }
}

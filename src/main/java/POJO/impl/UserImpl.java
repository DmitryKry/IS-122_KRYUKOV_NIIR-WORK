package POJO.impl;

import POJO.User;

import javax.management.relation.Role;
import java.util.ArrayList;
import java.util.List;

public class UserImpl implements User {
    private long id;
    private String name;
    private String password;
    private String role;
    private List<String> locale;
    public UserImpl(){
        locale = new ArrayList<String>();
        locale.add("/");
        locale.add("home");
    }

    @Override
    public long getId() {
        return 0;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public Role getRole() {
        return null;
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public List<String> getlocation() {
        return locale;
    }

    @Override
    public void setId(long id) {

    }

    @Override
    public void setName(String name) {

    }

    @Override
    public void setRole(Role role) {

    }

    @Override
    public void setPassword(String password) {

    }

    @Override
    public void setLocation(String location) {

    }
}

package POJO.impl;

import DAO.SQLFilesDAO;
import POJO.User;
import supportive.Support;

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
        return this.id;
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
    public List<UserImpl> getUsers() {
        return SQLFilesDAO.getUsers();
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setRole(Long role) {
        this.role = String.valueOf(role);
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void setLocation(String location) {
        if (location.equals("..")){
            locale.remove(locale.size()-1);
            locale.remove(locale.size()-1);
            return;
        }
        /*if (Support.FindElem(location, '/') != null)
            return;*/
        File file = SQLFilesDAO.getLocals().stream().filter(
                t -> t.getName().equals(location)).findFirst().orElse(null);
        if (file != null) {
            if (SQLFilesDAO.getOwnLocale(file.getId()) != null)
                this.locale.add("/");
                this.locale.add(location);
        }

    }
}

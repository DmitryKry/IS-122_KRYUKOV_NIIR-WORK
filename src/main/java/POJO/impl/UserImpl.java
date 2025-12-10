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
        List<String> paths = new ArrayList<>();
        if (location.equals("..")){
            if (locale.size() < 3){
                return;
            }
            locale.remove(locale.size()-1);
            locale.remove(locale.size()-1);
            return;
        }
        if (Support.FindElem(location, "/") != null){
            String fileName = "";
            while(!location.isEmpty()){
                String tempForOwnFileName = "";
                String ownFileName = "";
                for (int i = location.length() - 1; i >= 0; i--) {
                    if (location.charAt(i) == '/'){
                        location = location.substring(0, location.length() - 1);
                        break;
                    }
                    else tempForOwnFileName += location.charAt(i);
                    location = location.substring(0, location.length() - 1);
                }
                for (int i = tempForOwnFileName.length() - 1; i >= 0; i--) {
                    ownFileName += tempForOwnFileName.charAt(i);
                }
                fileName = ownFileName;
                String tempPath = location;
                paths.add(0, fileName);
                paths.add(0, "/");
            }
            fileName = "";
            for (int i = 0; i < paths.size() - 2; i++) {
                fileName += paths.get(i);
            }
            String tfname = fileName;
            File fileCompile = SQLFilesDAO.getLocals().stream()
                    .filter(locals -> locals.getName().equals(paths.get(paths.size() - 1)) &&
                            locals.getPath().equals(tfname))
                    .findFirst().orElse(null);
            if (fileCompile != null) {
                this.locale.clear();
                this.locale.addAll(paths);
            }
        } else {
            String tLocale = location;
            File file = SQLFilesDAO.getLocals().stream().filter(
                    t -> t.getName().equals(tLocale)).findFirst().orElse(null);
            if (file != null) {
                if (SQLFilesDAO.getOwnLocale(file.getId()) != null)
                    this.locale.add("/");
                this.locale.add(location);
            }
        }
    }
}

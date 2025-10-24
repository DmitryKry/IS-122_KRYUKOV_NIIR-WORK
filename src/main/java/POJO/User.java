package POJO;

import javax.management.relation.Role;
import java.util.List;

public interface User {
    long getId();
    String getName();
    Role getRole();
    String getPassword();
    List<String> getlocation();
    void setId(long id);
    void setName(String name);
    void setRole(Role role);
    void setPassword(String password);
    void setLocation(String location);

}

package POJO;

import java.util.List;

public interface FileService {
    long getId();
    String getName();
    String getPath();
    String getType();
    List<String> getFiles();

    void setId(long id);
    void setName(String name);
    void setType(String type);
    void setFiles(List<String> files);
    void setPath(String path);

}

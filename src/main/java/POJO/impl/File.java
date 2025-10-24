package POJO.impl;

import POJO.FileService;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class File implements FileService {
    private long ID;
    private String name;
    private String path;
    private String type;
    private List<String> files;
    public File(String name, String path, String type) {
        this.ID = ID;
        this.name = name;
        this.path = path;
        this.type = type;
        this.files = null; // или new ArrayList<>()
    }

    // Реализация методов интерфейса FileService
    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public void setId(long id) {
        this.ID = id;
    }

    @Override
    public void setPath(String address) {
        this.path = address;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public long getId() {
        return this.ID;
    }
}
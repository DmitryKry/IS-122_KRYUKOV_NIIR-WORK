package POJO;
import POJO.FileService;
import POJO.impl.File;

public class FileFactory {
    public static FileService createFile(String name, String path, String type) {
        return new File(name, path, type);
    }
}
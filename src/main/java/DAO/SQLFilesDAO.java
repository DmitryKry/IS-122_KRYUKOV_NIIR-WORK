package DAO;

import POJO.FileService;
import POJO.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import POJO.impl.File;
import POJO.impl.UserImpl;
import supportive.Support;
public class SQLFilesDAO {
    private static final String URL = "jdbc:postgresql://localhost:5432/NIR";
    private static final String USER = "postgres";
    private static final String PASSWORD = "z7J.6q";
    private static Connection connection;

    public static Connection connect() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Подключение к базе прошло успешно.");
            return connection;
        } catch (SQLException e) {
            System.out.println("❌ Ошибка подключения: " + e.getMessage());
            return null;
        }
    }

    public static Long addFile(List<String> ownerFileNames,FileService fileService) {
        String sqlFile = "insert into file(name, path, type) VALUES (?, ?, ?) returning ID";
        String sqlСonnection = "insert into storage_files(id_of_owner, id_of_subordinate) VALUES (?, ?)";
        String sql = "SELECT id FROM file where name = ?";
        String sqlPath = "select * from file where path = ? and name = ?";
        String sqlCreatFile = "insert into data_of_file(file_id) VALUES (?) ";
        ResultSet resultSet;

        try {
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(sqlFile);
            PreparedStatement stmtConnect = connection.prepareStatement(sqlСonnection);
            PreparedStatement stmtSql = connection.prepareStatement(sql);
            PreparedStatement stmtPath = connection.prepareStatement(sqlPath);
            PreparedStatement stmtCreatFile = connection.prepareStatement(sqlCreatFile);

            String tempmainPath = "";
            for (int i = 0; i < ownerFileNames.size() - 2; i++) {
                tempmainPath += ownerFileNames.get(i);
            }
            String MAINPath = tempmainPath;
            File ownFile = getLocals().stream()
                    .filter(locals -> locals.getName()
                            .equals(ownerFileNames.get(ownerFileNames.size() - 1)) &&
                            locals.getPath().equals(MAINPath))
                    .findFirst().orElse(null);
            if (ownFile.getPath().equals("file"))
                return Long.valueOf(0);
            String Repiet = "";
            String path = "";
            for (String elem : ownerFileNames)
                path += elem;
            fileService.setPath(path);

            while (true) {
                stmtPath.setString(1, fileService.getPath());
                stmtPath.setString(2, fileService.getName() + Repiet);
                resultSet = stmtPath.executeQuery();
                if (resultSet.next()) {
                    Repiet += "_copy";
                } else break;
            }

            stmt.setString(1, fileService.getName() + Repiet);
            stmt.setString(2, fileService.getPath());


            if (Support.FindElem(fileService.getName(), "/") == null) {
                stmt.setString(2, fileService.getPath());
                stmtSql.setString(1, ownerFileNames.get(ownerFileNames.size() - 1));
                resultSet = stmtSql.executeQuery();
                if (resultSet.next()) {
                    //stmtConnect.setLong(1, resultSet.getLong("ID"));
                }
            }
            stmtConnect.setLong(1, ownFile.getId());
            stmt.setString(3, fileService.getType());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                long generatedId = rs.getLong("ID");
                fileService.setId(generatedId);
                stmtConnect.setLong(2, generatedId);
                if (fileService.getType().equals("file")){
                    stmtCreatFile.setLong(1, generatedId);
                    stmtCreatFile.executeUpdate();
                }
                System.out.println("Файл успешно создан!");
                System.out.println("✅ Сгенерированный ID: " + generatedId);
            }
            stmtConnect.executeUpdate();
            connection.commit();
            connection.setAutoCommit(true);
            return Long.valueOf(fileService.getId());
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return Long.valueOf(0);
        }
    }

    public static List<File> getOwnLocale(Long idOfOwner) {
        List<File> locals = new ArrayList<>();
        String sql = "select * from storage_files where id_of_owner = ?";
        ResultSet resultSet;
        try{
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setLong(1, idOfOwner);
            resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                Long fileOwn = resultSet.getLong("id_of_subordinate");
                File temp = SQLFilesDAO.getLocals().stream()
                        .filter(t -> t.getId() == fileOwn)
                        .findFirst().orElse(null);
                if (temp != null) {
                    locals.add(temp);
                }

            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
        return locals;
    }

    public static List<File> getLocals() {
        List<File> locals = new ArrayList<>();
        String sql = "select * from file";
        ResultSet resultSet;
        try{
            PreparedStatement stmt = connection.prepareStatement(sql);
            resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                File fileService = new File(resultSet.getString("name"),
                        resultSet.getString("path"),
                        resultSet.getString("type"));
                fileService.setId(resultSet.getLong("id"));
                locals.add(fileService);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
        return locals;
    }

    public static List<UserImpl> getUsers() {
        List<UserImpl> users = new ArrayList<>();
        String sql = "select * from user_file";
        ResultSet resultSet;
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                UserImpl userImpl = new UserImpl();
                userImpl.setId(resultSet.getLong("ID"));
                userImpl.setName(resultSet.getString("name"));
                userImpl.setRole(resultSet.getLong("role"));
                userImpl.setPassword(resultSet.getString("password"));
                users.add(userImpl);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
        return users;
    }

    public static void deleteFile(List<String> ownerFileNames, String fileName) {
        String sql = "delete from file where id = ?";
        String sqlOwn = "delete from storage_files where id_of_owner = ? and id_of_subordinate = ?";
        try {
            connection.setAutoCommit(false); // Начинаем транзакцию

            String tempmainPath = "";
            for (int i = 0; i < ownerFileNames.size() - 2; i++) {
                tempmainPath += ownerFileNames.get(i);
            }
            String MAINPath = tempmainPath;
            File ownFile = getLocals().stream()
                    .filter(locals -> locals.getName()
                            .equals(ownerFileNames.get(ownerFileNames.size() - 1)) &&
                            locals.getPath().equals(MAINPath))
                    .findFirst().orElse(null);
            File file = getLocals().stream()
                    .filter(locals -> locals.getName()
                            .equals(fileName) &&
                            locals.getPath().equals(ownFile.getPath() + "/" + ownFile.getName()))
                    .findFirst().orElse(null);
            if (file == null) {
                System.out.println("Файл не найден: " + fileName);
                return;
            }

            long fileId = file.getId();
            PreparedStatement stmtSqlSubordinate = connection.prepareStatement(sqlOwn);
            PreparedStatement stmtSql = connection.prepareStatement(sql);
            for (File elem : getOwnLocale(fileId)) {
                if (getOwnLocale(elem.getId()) == null) {
                    stmtSqlSubordinate.setLong(1, fileId);
                    stmtSqlSubordinate.setLong(2, elem.getId());
                    stmtSqlSubordinate.executeUpdate();
                    stmtSql.setLong(1, elem.getId());
                    stmtSql.executeUpdate();
                    connection.commit(); // Подтверждаем транзакцию
                    connection.setAutoCommit(true);
                    System.out.println("Файл успешно удален: " + fileName);
                }
                else {
                    List<String> tempPath = ownerFileNames;
                    if (tempPath.get(tempPath.size() - 1).equals(fileName)) {
                        deleteFile(tempPath, elem.getName());
                    }
                    else{
                        tempPath.add("/");
                        tempPath.add(fileName);
                        deleteFile(tempPath, elem.getName());
                    }
                }
            }
            // Удаляем основную запись
            stmtSqlSubordinate.setLong(1, ownFile.getId());
            stmtSqlSubordinate.setLong(2, fileId);
            stmtSqlSubordinate.executeUpdate();
            stmtSql.setLong(1, fileId);
            stmtSql.executeUpdate();

            connection.commit(); // Подтверждаем транзакцию
            connection.setAutoCommit(true);
            System.out.println("Файл успешно удален: " + fileName);

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    static public void move(List<String> ownerFileNames, String fileName, String path){
        String sqlPath = "select id from file where path = ? and name = ?";
        String sqlSubordinate = "update storage_files set id_of_owner = ? where id_of_subordinate = ?";
        String sqlUpdateFile = "update file set path = ? where id = ?";
        String sqlNameFileSubordinate = "select name from file where id = ?";
        String sqlOwn = "select id_of_subordinate from storage_files where id_of_owner = ?";
        ResultSet resultSet;
        ResultSet resultSetNameFileSubordinate;
        String ownFileName = "";
        String tempForOwnFileName = "";
        try {
            for (int i = path.length() - 1; i >= 0; i--) {
                if (path.charAt(i) == '/'){
                    path = path.substring(0, path.length() - 1);
                    break;
                }
                else tempForOwnFileName += path.charAt(i);
                path = path.substring(0, path.length() - 1);
            }
            for (int i = tempForOwnFileName.length() - 1; i >= 0; i--) {
                ownFileName += tempForOwnFileName.charAt(i);
            }
            connection.setAutoCommit(false);
            String tempmainPath = "";
            for (int i = 0; i < ownerFileNames.size(); i++) {
                tempmainPath += ownerFileNames.get(i);
            }
            String MAINPath = tempmainPath;
            File file = getLocals().stream()
                    .filter(locals -> locals.getName()
                            .equals(fileName) &&
                            locals.getPath().equals(MAINPath))
                    .findFirst().orElse(null);
            String tempOwnFileName = ownFileName;
            String tempOwnPath = path;
            File nextOwn = getLocals().stream()
                    .filter(locals -> locals.getName().equals(tempOwnFileName) &&
                            locals.getPath().equals(tempOwnPath)).findFirst().orElse(null);
            PreparedStatement stmtPath = connection.prepareStatement(sqlOwn);
            PreparedStatement stmtSubordinate = connection.prepareStatement(sqlSubordinate);
            PreparedStatement stmtUpdateFile = connection.prepareStatement(sqlUpdateFile);
            PreparedStatement stmNameFileSubordinate = connection.prepareStatement(sqlNameFileSubordinate);
            stmtUpdateFile.setString(1, path + '/' + ownFileName);
            stmtUpdateFile.setLong(2, file.getId());
            stmtUpdateFile.executeUpdate();

            stmtSubordinate.setLong(1, nextOwn.getId());
            stmtSubordinate.setLong(2, file.getId());
            stmtSubordinate.executeUpdate();

            List<String> TempFilePath = new ArrayList<>();
            TempFilePath.addAll(ownerFileNames);
            TempFilePath.add("/");
            TempFilePath.add(ownFileName);
            reName(TempFilePath, file.getName(), file.getName());
            /*
            while (resultSet.next()) {
                Long fileId = resultSet.getLong("id_of_subordinate");
                stmtSubordinate.setLong(1, fileId);
                stmtSubordinate.setLong(2, file.getId());
                stmtSubordinate.executeUpdate();
                System.out.println("Файл " + fileName + " был успешно перемещен");
                if (getOwnLocale(fileId) != null) {
                    List<String> TempFilePath = new ArrayList<>();
                    TempFilePath.addAll(ownerFileNames);
                    TempFilePath.add("/");
                    TempFilePath.add(file.getName());
                    stmNameFileSubordinate.setLong(1, fileId);
                    resultSetNameFileSubordinate = stmNameFileSubordinate.executeQuery();
                    if (!resultSetNameFileSubordinate.next())
                        continue;
                    String tempName = resultSetNameFileSubordinate.getString("name");
                    reName(TempFilePath, tempName, tempName);
                }
            }*/


            connection.commit(); // Подтверждаем транзакцию
            connection.setAutoCommit(true);
            System.out.println("Файл успешно перемещён: " + fileName);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    static public void reName(List<String> ownerFilePath, String fileName, String newName){
        String sqlNewName = "update file set name = ? where id = ?";
        String sqlOwn = "select id_of_subordinate from storage_files where id_of_owner = ?";
        String sqlNewPath = "update file set path = ? where id = ?";
        String sqlNameFileSubordinate = "select name from file where id = ?";
        String tempPath = "";
        ResultSet resultSet;
        ResultSet resultSetNameFileSubordinate;
        try {
            connection.setAutoCommit(false); // Начинаем транзакцию
            for (String elem : ownerFilePath) {
                tempPath += elem;
            }
            String mainPath = tempPath;
            PreparedStatement stmtNewName = connection.prepareStatement(sqlNewName);
            PreparedStatement stmtOwn = connection.prepareStatement(sqlOwn);
            PreparedStatement stmNewPath = connection.prepareStatement(sqlNewPath);
            PreparedStatement stmNameFileSubordinate = connection.prepareStatement(sqlNameFileSubordinate);
            File current = getLocals().stream()
                    .filter(locals -> locals.getName().equals(fileName) &&
                            locals.getPath().equals(mainPath))
                    .findFirst().orElse(null);
            stmtOwn.setLong(1, current.getId());
            resultSet = stmtOwn.executeQuery();
            stmtNewName.setString(1, newName);
            stmtNewName.setLong(2, current.getId());
            stmtNewName.executeUpdate();
            while (resultSet.next()) {
                Long fileId = resultSet.getLong("id_of_subordinate");
                stmNewPath.setString(1, mainPath + "/" + newName);
                stmNewPath.setLong(2, fileId);
                stmNewPath.executeUpdate();
                System.out.println("Файл был успешно переименован в: " + newName);
                if (getOwnLocale(fileId) != null) {
                    List<String> TempFilePath = new ArrayList<>();
                    TempFilePath.addAll(ownerFilePath);
                    TempFilePath.add("/");
                    TempFilePath.add(newName);
                    stmNameFileSubordinate.setLong(1, fileId);
                    resultSetNameFileSubordinate = stmNameFileSubordinate.executeQuery();
                    if (!resultSetNameFileSubordinate.next())
                        continue;
                    String tempName = resultSetNameFileSubordinate.getString("name");
                    reName(TempFilePath, tempName, tempName);
                }
            }
            connection.commit(); // Подтверждаем транзакцию
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        
    }

    static public List<String> ls(String path){
        List<File> list;
        List<String> tempList = new ArrayList<>();
        String tempForOwnFileName = "";
        String ownFileName = "";
        for (int i = path.length() - 1; i >= 0; i--) {
            if (path.charAt(i) == '/'){
                path = path.substring(0, path.length() - 1);
                break;
            }
            else tempForOwnFileName += path.charAt(i);
            path = path.substring(0, path.length() - 1);
        }
        for (int i = tempForOwnFileName.length() - 1; i >= 0; i--) {
            ownFileName += tempForOwnFileName.charAt(i);
        }
        String fileName = ownFileName;
        String tempPath = path;
        File file = getLocals().stream()
                .filter(locals -> locals.getName().equals(fileName) &&
                        locals.getPath().equals(tempPath))
                .findFirst().orElse(null);
        list = getOwnLocale(file.getId());
        for (File elem : list) {
            tempList.add(elem.getName());
        }
        return tempList;
    }

    static public void nano(List<String> ownerFilePath, String fileName, String text){
        String sql = "update data_of_file set tail = ? where file_id = ?";
        String tempPath = "";
        String tempForOwnFileName = "";
        String ownFileName = "";
        boolean check = false;
        try {
            connection.setAutoCommit(false);
            for (String elem : ownerFilePath) {
                tempPath += elem;
            }
            PreparedStatement stmt = connection.prepareStatement(sql);
            if (Support.FindElem(fileName, "/") != null){
                if (Support.FindElem(fileName, "/home") != null){
                    tempPath += "/" + fileName;
                }
                check = true;
                for (int i = fileName.length() - 1; i >= 0; i--) {
                    if (fileName.charAt(i) == '/'){
                        fileName = fileName.substring(0, fileName.length() - 1);
                        break;
                    }
                    else tempForOwnFileName += fileName.charAt(i);
                    fileName = fileName.substring(0, fileName.length() - 1);
                }
                for (int i = tempForOwnFileName.length() - 1; i >= 0; i--) {
                    ownFileName += tempForOwnFileName.charAt(i);
                }

            }
            Long idOfFile;
            String mainName = ownFileName == "" ? fileName : ownFileName;
            if (check)
                tempPath += "/" + fileName;
            String mainPath = tempPath;
            File current = getLocals().stream()
                    .filter(locals -> locals.getName().equals(mainName) &&
                            locals.getPath().equals(mainPath))
                    .findFirst().orElse(null);
            if (current == null){
                File file = new File(mainName, null, "file");
                idOfFile = addFile(ownerFilePath, file);
            }
            else idOfFile = current.getId();
            stmt.setString(1, text);
            stmt.setLong(2, idOfFile);
            stmt.executeUpdate();
            connection.commit();
            connection.setAutoCommit(true);
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    static public String cat(List<String> ownerFilePath, String fileName){
        String sqlOwn = "select tail from data_of_file where file_id = ?";
        String tempPath = "";
        ResultSet resultSet;
        String tempForOwnFileName = "";
        String ownFileName = "";
        String tempList = "";
        boolean check = false;
        try {
            connection.setAutoCommit(false); // Начинаем транзакцию
            for (String elem : ownerFilePath) {
                tempPath += elem;
            }
            PreparedStatement stmt = connection.prepareStatement(sqlOwn);
            if (Support.FindElem(fileName, "/") != null){
                if (Support.FindElem(fileName, "/home") != null){
                    tempPath += "/" + fileName;
                }
                check = true;
                for (int i = fileName.length() - 1; i >= 0; i--) {
                    if (fileName.charAt(i) == '/'){
                        fileName = fileName.substring(0, fileName.length() - 1);
                        break;
                    }
                    else tempForOwnFileName += fileName.charAt(i);
                    fileName = fileName.substring(0, fileName.length() - 1);
                }
                for (int i = tempForOwnFileName.length() - 1; i >= 0; i--) {
                    ownFileName += tempForOwnFileName.charAt(i);
                }

            }
            Long idOfFile;
            String mainName = ownFileName == "" ? fileName : ownFileName;
            if (check)
                tempPath += "/" + fileName;
            String mainPath = tempPath;
            File current = getLocals().stream()
                    .filter(locals -> locals.getName().equals(mainName) &&
                            locals.getPath().equals(mainPath))
                    .findFirst().orElse(null);
            if (current == null){
                return null;
            }
            stmt.setLong(1, current.getId());
            resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                tempList += resultSet.getString("tail");
            }

            connection.commit(); // Подтверждаем транзакцию
            connection.setAutoCommit(true);
            return tempList;
        }
        catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }
}

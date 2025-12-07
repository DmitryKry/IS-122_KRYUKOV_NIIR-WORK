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

    public static boolean addFile(FileService fileService, User user) {
        String sqlFile = "insert into file(name, path, type) VALUES (?, ?, ?) returning ID";
        String sqlСonnection = "insert into storage_files(id_of_owner, id_of_subordinate) VALUES (?, ?)";
        String sql = "SELECT id FROM file where name = ?";
        String sqlPath = "select * from file where path = ? and name = ?";
        ResultSet resultSet;

        try {
            PreparedStatement stmt = connection.prepareStatement(sqlFile);
            PreparedStatement stmtConnect = connection.prepareStatement(sqlСonnection);
            PreparedStatement stmtSql = connection.prepareStatement(sql);
            PreparedStatement stmtPath = connection.prepareStatement(sqlPath);

            String Repiet = "";
            String path = "";
            for (String elem : user.getlocation())
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


            if (Support.FindElem(fileService.getName(), '/') == null) {
                stmt.setString(2, fileService.getPath());
                stmtSql.setString(1, user.getlocation().get(user.getlocation().size() - 1));
                resultSet = stmtSql.executeQuery();
                if (resultSet.next()) {
                    stmtConnect.setLong(1, resultSet.getLong("ID"));
                }
            }
            stmt.setString(3, fileService.getType());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                long generatedId = rs.getLong("ID");
                stmtConnect.setLong(2, generatedId);
                System.out.println("Файл успешно создан!");
                System.out.println("✅ Сгенерированный ID: " + generatedId);
            }
            stmtConnect.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
        return false;
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
                    System.out.println("Файл успешно удален: " + fileName);
                }
                else {
                    List<String> tempPath = ownerFileNames;
                    tempPath.add("/");
                    tempPath.add(fileName);
                    deleteFile(tempPath, elem.getName());
                }
            }
            // Удаляем основную запись
            stmtSqlSubordinate.setLong(1, ownFile.getId());
            stmtSqlSubordinate.setLong(2, fileId);
            stmtSqlSubordinate.executeUpdate();
            stmtSql.setLong(1, fileId);
            stmtSql.executeUpdate();

            connection.commit(); // Подтверждаем транзакцию
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
        ResultSet resultSet;
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
            PreparedStatement stmtPath = connection.prepareStatement(sqlPath);
            PreparedStatement stmtSubordinate = connection.prepareStatement(sqlSubordinate);
            PreparedStatement stmtUpdateFile = connection.prepareStatement(sqlUpdateFile);
            stmtPath.setString(1, path);
            stmtPath.setString(2, ownFileName);
            resultSet = stmtPath.executeQuery();
            while (resultSet.next()) {
                Long fileId = resultSet.getLong("id");
                stmtSubordinate.setLong(1, fileId);
                stmtSubordinate.setLong(2, file.getId());
            }
            stmtSubordinate.executeUpdate();
            stmtUpdateFile.setString(1, path + '/' + ownFileName);
            stmtUpdateFile.setLong(2, file.getId());
            stmtUpdateFile.executeUpdate();
            connection.commit(); // Подтверждаем транзакцию
            System.out.println("Файл успешно перемещён: " + fileName);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    static public void reName(List<String> ownerFilePath, String fileName, String newName){
        String sqlPath = "update file set name = ? where id = ?";
        String tempPath = "";
        try {
            for (String elem : ownerFilePath) {
                tempPath += elem;
            }
            String mainPath = tempPath;
            PreparedStatement stmt = connection.prepareStatement(sqlPath);
            File current = getLocals().stream()
                    .filter(locals -> locals.getName().equals(fileName) &&
                            locals.getPath().equals(mainPath))
                    .findFirst().orElse(null);
            stmt.setString(1, newName);
            stmt.setLong(2, current.getId());
            stmt.executeUpdate();
            System.out.println("Файл был успешно переименован в: " + newName);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        
    }
/*
    public static boolean deleteProductById(long id) {
        String sql = "DELETE FROM products WHERE id = ?";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);

            pstmt.setLong(1, id);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Запись с id = " + id + " успешно удалена.");
                return true;
            } else {
                System.out.println("Запись с id = " + id + " не найдена.");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean updateProduct(FileService fileService) {
        String sql = "UPDATE products SET product_category = ?, product_name = ?, price = ?, manufacture_date = ?, expiry_date = ? WHERE id = ?";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setString(1, fileService.getProductСategory());
            stmt.setString(2, fileService.getProductName());
            stmt.setDouble(3, fileService.getPrice());
            stmt.setDate(4, java.sql.Date.valueOf(fileService.getManufactureDate()));
            stmt.setDate(5, java.sql.Date.valueOf(fileService.getExpiryDate()));
            stmt.setLong(6, fileService.getId()); // идентификатор записи, которую редактируем

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                //System.out.println("Продукт успешно обновлён.");
                return true;
            } else {
                System.out.println("Error");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<FileService> getAllfils() {
        List<FileService> products = new ArrayList<>();

        String sql = "SELECT * FROM products";

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                long id = rs.getLong("id");
                String category = rs.getString("product_category");
                String name = rs.getString("product_name");
                double price = rs.getDouble("price");
                LocalDate manufactureDate = rs.getDate("manufacture_date").toLocalDate();
                LocalDate expiryDate = rs.getDate("expiry_date").toLocalDate();

                FileService file = new FileService(id, category, name, price, manufactureDate, expiryDate);
                products.add(file);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public static int getProductCount() {
        String sql = "SELECT COUNT(*) FROM products";

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                return rs.getInt(1); // получаем значение COUNT(*)
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
 */

}

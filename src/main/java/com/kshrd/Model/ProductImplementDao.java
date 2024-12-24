package com.kshrd.Model;

import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;


public class ProductImplementDao implements ProductDao {
    public static final String red = "\u001B[31m";
    public static final String green = "\u001B[32m";
    public static final String yellow = "\u001B[33m";

    public static final String blue = "\u001B[34m";
    public static final String reset = "\u001B[0m";

    private final Validate validate = new Validate();
    public static Scanner sc = new Scanner(System.in);

    @Override
    public ArrayList<Product> getAllProducts() {

        ArrayList<Product> newProducts = new ArrayList<>();

        try {
            Connection connection = Connect.connection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("Select * from stock_tb");
            while (rs.next()) {
                newProducts.add(
                        new Product(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getDouble("unit_price"),
                                rs.getInt("stock_qty"),
                                rs.getDate("import_date").toLocalDate())
                );
            }
            connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return newProducts;
    }

    int id = setId();

    public int setId() {
        try {
            Connection connection = Connect.connection();
            String query = "SELECT id FROM stock_tb ORDER BY id DESC LIMIT 1";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                id = rs.getInt("id");
            }
            connection.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return id;
    }

    @Override
    public Product insertProduct() {
        String name;
        double unit_price;
        int stock_qty;
        LocalDate date = LocalDate.now();
        id++;
        System.out.println("ID: " + id);
        name = validate.validateString("Input Product Name : ");
        unit_price = validate.validatePrice();
        stock_qty = validate.validateQuantity();
        System.out.println(yellow + "Enter to continue....." + reset);
        sc.nextLine();
        return new Product(id, name, unit_price, stock_qty, date);
    }

    public void saveProduct(ArrayList<Product> productsForInsertToDB, ArrayList<Product> productsForUpdateToDB) {
        System.out.println("'ui' for save insert products and 'uu' for save update products or 'b' for back to menu");
        String choice = validate.validateString("Enter your choice : ");
        switch (choice) {
            case "ui":
                saveInsertProduct(productsForInsertToDB);
                break;
            case "uu":
                saveUpdateProduct(productsForUpdateToDB);
                break;
            case "b":
                break;
        }
    }

    public void saveInsertProduct(ArrayList<Product> saveInsertProduct) {
        for (Product product : saveInsertProduct) {
            try {
                Connection connection = Connect.connection();
                String insertSql = "INSERT INTO stock_tb ( id ,name, unit_price, stock_qty, import_date) VALUES ('" + product.getId() + "','" + product.getName() + "','" + product.getUnitPrice() + "','" + product.getQuantity() + "', '" + product.getImportDate() + "')";
                Statement statement = connection.createStatement();
                int success = statement.executeUpdate(insertSql);
                if (success > 0) {
                    System.out.println("Product " + product.getId() + green + " successfully added." + reset);
                } else
                    System.out.println("Product " + product.getId() + " failed to add");
                connection.close();
            } catch (Exception e) {
                System.out.println(red + "Error: " + e.getMessage() + reset);
            }
            System.out.println("Enter to continue...");
            sc.nextLine();
        }
        saveInsertProduct.clear();
    }

    public void saveUpdateProduct(ArrayList<Product> productForUpdateToDB) {
        for (Product product : productForUpdateToDB) {
            try {
                Connection connection = Connect.connection();

                String updateSql = "UPDATE stock_tb SET name = '" + product.getName() + "', unit_price = " + product.getUnitPrice() + ", stock_qty = " + product.getQuantity() + " WHERE id = " + product.getId();
                Statement statement = connection.createStatement();
                int success = statement.executeUpdate(updateSql);
                if (success > 0) {
                    System.out.println("Product " + product.getId() + green + " successfully update." + reset);
                } else {
                    System.out.println("Product " + product.getId() + " failed to update.");
                    connection.close();
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        System.out.println("Enter to continue...");
        sc.nextLine();
        productForUpdateToDB.clear();
    }


    @Override
    public void getProduct() {
        try {
            Connection connection = Connect.connection();
            int id = validate.inputID("Please input id to get record : ");

            String select = "SELECT * FROM stock_tb where id =" + id;

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(select);
            if (resultSet.next()) {
                int idTb = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int price = resultSet.getInt("unit_price");
                int qty = resultSet.getInt("stock_qty");
                String date = resultSet.getString("import_date");

                CellStyle center = new CellStyle(CellStyle.HorizontalAlign.CENTER);
                Table t = new Table(5, BorderStyle.UNICODE_ROUND_BOX);

                t.setColumnWidth(0, 10, 15);
                t.setColumnWidth(1, 25, 30);
                t.setColumnWidth(2, 20, 25);
                t.setColumnWidth(3, 15, 120);
                t.setColumnWidth(4, 15, 20);

                t.addCell("ID", center);
                t.addCell("Name", center);
                t.addCell("Unit Price", center);
                t.addCell("Qty", center);
                t.addCell("Import Date", center);

                t.addCell("" + idTb, center);
                t.addCell(name, center);
                t.addCell("" + price, center);
                t.addCell("" + qty, center);
                t.addCell(date, center);
                System.out.println(t.render());
                System.out.println("Enter to continue...");
                sc.nextLine();
                return;
            } else {
                System.out.println("No data found");
            }
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteProduct() {
        Scanner sc = new Scanner(System.in);
        try {
            Connection connection = Connect.connection();
            int id = validate.inputID("Please input id to delete record : ");
            String delete = "DELETE FROM stock_tb WHERE id = ?";
            PreparedStatement pst = connection.prepareStatement(delete);
            pst.setInt(1, id);

            String select = "SELECT * FROM stock_tb where id =" + id;
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(select);
            if (resultSet.next()) {
                do {
                    int idTb = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    int price = resultSet.getInt("unit_price");
                    int qty = resultSet.getInt("stock_qty");
                    String date = resultSet.getString("import_date");

                    CellStyle center = new CellStyle(CellStyle.HorizontalAlign.CENTER);
                    Table t = new Table(5, BorderStyle.UNICODE_ROUND_BOX);

                    t.setColumnWidth(0, 10, 15);
                    t.setColumnWidth(1, 25, 30);
                    t.setColumnWidth(2, 20, 25);
                    t.setColumnWidth(3, 15, 20);
                    t.setColumnWidth(4, 15, 25);

                    t.addCell("ID", center);
                    t.addCell("Name", center);
                    t.addCell("Unit Price", center);
                    t.addCell("Qty", center);
                    t.addCell("Import Date", center);

                    t.addCell("" + idTb, center);
                    t.addCell(name, center);
                    t.addCell("" + price, center);
                    t.addCell("" + qty, center);
                    t.addCell(date, center);
                    System.out.println(t.render());
                } while (resultSet.next());
                System.out.print(yellow + "Are you sure to delete product id : " + reset + red + id + reset + " (y/n) : ");
                String rs = sc.next();
                if (rs.equals("y") || rs.equals("Y")) {
                    pst.executeUpdate();
                    System.out.println(green + "Delete Successfully" + reset);
                } else if (rs.equals("n") || rs.equals("N")) {
                    System.out.println("Delete Cancel");
                } else {
                    System.out.println("Invalid input");
                }
            } else {
                System.out.println("No data found");
                System.out.println("Enter to continue...");
                sc.nextLine();
            }

            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateProduct(ArrayList<Product> updatedProducts) {
        int idTb;
        double price;
        int qty;
        String name;
        Scanner sc = new Scanner(System.in);

        try {
            Connection connection = Connect.connection();
            int id = validate.inputID("Input ID to update: ");
            String update = "UPDATE FROM stock_tb WHERE id = ?";
            PreparedStatement pst = connection.prepareStatement(update);
            pst.setInt(1, id);

            Statement statement = connection.createStatement();

            String select = "SELECT * FROM stock_tb where id =" + id;
            ResultSet resultSet = statement.executeQuery(select);
            if (resultSet.next()) {

                idTb = resultSet.getInt("id");
                name = resultSet.getString("name");
                price = resultSet.getDouble("unit_price");
                qty = resultSet.getInt("stock_qty");
                String date = resultSet.getString("import_date");

                CellStyle center = new CellStyle(CellStyle.HorizontalAlign.CENTER);
                Table t = new Table(5, BorderStyle.UNICODE_ROUND_BOX);

                t.setColumnWidth(0, 15, 15);
                t.setColumnWidth(1, 30, 30);
                t.setColumnWidth(2, 20, 20);
                t.setColumnWidth(3, 15, 15);
                t.setColumnWidth(4, 15, 15);

                t.addCell("ID", center);
                t.addCell("Name", center);
                t.addCell("Unit Price", center);
                t.addCell("Qty", center);
                t.addCell("Import Date", center);

                t.addCell("" + idTb, center);
                t.addCell(name, center);
                t.addCell("" + price, center);
                t.addCell("" + qty, center);
                t.addCell(date, center);
                System.out.println(t.render());

                System.out.print("Enter the new product name: ");
                name = sc.nextLine();
                System.out.print("Enter the new product price: ");
                price = sc.nextDouble();
                System.out.print("Enter the new product quantity: ");
                int quantity = sc.nextInt();
                LocalDate Date = LocalDate.now();
                updatedProducts.add(new Product(id, name, price, quantity, Date));
                System.out.println(green + "update Successfully" + reset);

            } else {
                System.out.println("No ID found");
            }

            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void unSaveProduct(ArrayList<Product> unsavedInsertProducts, ArrayList<Product> unsavedUpdateProducts) {
        System.out.println("'ui' for viewing insert products and 'uu' for viewing update products or 'b' for back to menu");
        String choice = validate.validateString("Enter your choice : ");
        switch (choice) {
            case "ui":
                viewInsertedProducts(unsavedInsertProducts);
                break;
            case "uu":
                viewUpdatedProducts(unsavedUpdateProducts);
                break;
            case "b":
                break;
        }
    }

    @Override
    public ArrayList<Product> searchByProductName() {
        ArrayList<Product> products = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter product name to search : ");
        String searchName = scanner.next();
        String searchSql = "SELECT * FROM stock_tb WHERE name ILIKE '%" + searchName + "%'";
        Connection connection = Connect.connection();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(searchSql);
            while (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                double unitPrice = rs.getDouble(3);
                int quantity = rs.getInt(4);
                LocalDate importDate = rs.getDate(5).toLocalDate();
                products.add(new Product(id, name, unitPrice, quantity, importDate));
            }
            rs.close();
            connection.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return products;
    }

    public void viewInsertedProducts(ArrayList<Product> unsavedInsertProducts) {

        CellStyle center = new CellStyle(CellStyle.HorizontalAlign.CENTER);
        Table t = new Table(5, BorderStyle.UNICODE_ROUND_BOX, ShownBorders.ALL);

        t.setColumnWidth(0, 10, 15);
        t.setColumnWidth(1, 25, 30);
        t.setColumnWidth(2, 20, 25);
        t.setColumnWidth(3, 15, 20);
        t.setColumnWidth(4, 15, 20);

        t.addCell("ID", center);
        t.addCell("Name", center);
        t.addCell("Unit Price", center);
        t.addCell("Qty", center);
        t.addCell("Import Date", center);

        if(!unsavedInsertProducts.isEmpty()){
            for (Product unsavedProduct : unsavedInsertProducts) {
                t.addCell(Integer.toString(unsavedProduct.getId()), center);
                t.addCell(unsavedProduct.getName(), center);
                t.addCell(Double.toString(unsavedProduct.getUnitPrice()), center);
                t.addCell(Integer.toString(unsavedProduct.getQuantity()), center);
                t.addCell(String.valueOf(unsavedProduct.getImportDate()), center);
            }
        }
        else {
            t.addCell("---", center);
            t.addCell("---", center);
            t.addCell("---", center);
            t.addCell("---", center);
            t.addCell("---", center);
        }
        System.out.println(t.render());
        System.out.println("Enter to continue...");
        sc.nextLine();
    }

    public void viewUpdatedProducts(ArrayList<Product> unsavedUpdateProducts) {

        CellStyle center = new CellStyle(CellStyle.HorizontalAlign.CENTER);
        Table t = new Table(5, BorderStyle.UNICODE_ROUND_BOX, ShownBorders.ALL);

        t.setColumnWidth(0, 10, 15);
        t.setColumnWidth(1, 25, 30);
        t.setColumnWidth(2, 20, 25);
        t.setColumnWidth(3, 15, 20);
        t.setColumnWidth(4, 15, 20);

        t.addCell("ID", center);
        t.addCell("Name", center);
        t.addCell("Unit Price", center);
        t.addCell("Qty", center);
        t.addCell("Import Date", center);
        if(!unsavedUpdateProducts.isEmpty()){
            for (Product unsavedProduct : unsavedUpdateProducts) {
                t.addCell(Integer.toString(unsavedProduct.getId()), center);
                t.addCell(unsavedProduct.getName(), center);
                t.addCell(Double.toString(unsavedProduct.getUnitPrice()), center);
                t.addCell(Integer.toString(unsavedProduct.getQuantity()), center);
                t.addCell(String.valueOf(unsavedProduct.getImportDate()), center);
            }
        }
        else {
            t.addCell("---", center);
            t.addCell("---", center);
            t.addCell("---", center);
            t.addCell("---", center);
            t.addCell("---", center);
        }
        System.out.println(t.render());
        System.out.println("Enter to continue...");
        sc.nextLine();
    }

    // @Override
    public static ArrayList<Product> SearchByProductName() {
        ArrayList<Product> products = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter product name to search : ");
        String searchName = scanner.next();
        String searchSql = "SELECT * FROM stock_tb WHERE name ILIKE '%" + searchName + "%'";
        Connection connection = Connect.connection();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(searchSql);
            while (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                double unitPrice = rs.getDouble(3);
                int quantity = rs.getInt(4);
                LocalDate importDate = rs.getDate(5).toLocalDate();
                products.add(new Product(id, name, unitPrice, quantity, importDate));
            }
            rs.close();
            connection.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return products;
    }


    @Override
    public void recovery() {
        int row = validate.inputID("Please input number row per page : ");
        Connection connection = Connect.connection();
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("UPDATE set_row SET num_row = " + row);
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void backupData() {
        String url = "jdbc:postgresql://localhost:5432/mini_project_db";
        String user = "postgres";
        String pass = "1234";
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        FileWriter fileWriter = null;

        String tableName = "stock_tb";

        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_hh_mm_ss"));

        Scanner scanner = new Scanner(System.in); // Create a scanner
        String fileName = null;
        try {
            System.out.print("Enter the file name to Store backup : ");
            fileName = scanner.nextLine(); // Read the file name

            String filePath = "D:\\KSHRD\\Java\\JavaMIniProject\\stockManagementSystem\\backUp\\" + fileName + "_" + date + ".csv";

            connection = DriverManager.getConnection(url, user, pass);
            statement = connection.createStatement();

            String sqlQuery = "SELECT * FROM stock_tb";
            resultSet = statement.executeQuery(sqlQuery);

            // Create a FileWriter to write the data to the CSV file
            fileWriter = new FileWriter(filePath);

            // Write the column headers to the CSV file
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                fileWriter.append(metaData.getColumnName(i));
                if (i < columnCount) {
                    fileWriter.append(",");
                }
            }
            fileWriter.append("\n");
            // Write the data rows to the CSV file
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    fileWriter.append(resultSet.getString(i));
                    if (i < columnCount) {
                        fileWriter.append(",");
                    }
                }
                fileWriter.append("\n");
            }

            System.out.println(green + "Data Backup successfully." + reset);

        } catch (Exception e) {
            System.out.println(red + "Error: " + e.getMessage() + reset);
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
                if (fileWriter != null) {
                    fileWriter.close();
                }
            } catch (IOException | SQLException e) {
                System.out.println(red + "Error closing resources: " + e.getMessage() + reset);
            }
        }
    }

    @Override
    public void restoreBackup() {
        String url = "jdbc:postgresql://localhost:5432/mini_project_db";
        String user = "postgres";
        String pass = "1234";
        Connection connection = null;
        String delete = "DELETE FROM stock_tb";

        System.out.println("If you want to restore the backup database,current data will be replaced.");
        System.out.print("Enter the filename to restore : ");
        String filename = sc.next();
        String filePath = "D:\\KSHRD\\Java\\JavaMIniProject\\stockManagementSystem\\backUp\\" + filename + ".csv";

        try {
            connection = DriverManager.getConnection(url, user, pass);
            connection.setAutoCommit(false);
            Statement stmtDelete = connection.createStatement();
            String sql = "INSERT INTO stock_tb(id, name, unit_price, stock_qty, import_date) VALUES (?,?,?,?,?)";

            PreparedStatement statement = connection.prepareStatement(sql);
//            ----read file----
            BufferedReader file = new BufferedReader(new FileReader(filePath));

            String lineText = null;
            int count = 0;
            file.readLine();
            while ((lineText = file.readLine()) != null) {
                String[] data = lineText.split(",");
                String id = data[0];
                String name = data[1];
                String unit_price = data[2];
                String stock_qty = data[3];
                String import_date = data[4];

                statement.setInt(1, Integer.parseInt(id));
                statement.setString(2, name);
                statement.setDouble(3, Double.parseDouble(unit_price));
                statement.setDouble(4, Double.parseDouble(stock_qty));

                // Convert import_date to the correct format (yyyy-MM-dd)
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date parsedDate = dateFormat.parse(import_date);
                Date sqlDate = new Date(parsedDate.getTime());

                statement.setDate(5, sqlDate);
                statement.addBatch();

            }
            file.close();
            stmtDelete.executeUpdate(delete);
            statement.executeBatch();
            connection.commit();
            connection.close();
            System.out.println(green + "Data has been Restore form Backup successfully" + reset);

        } catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
        }
    }

    @Override
    public ArrayList<Product> saveData() {
        return null;
    }

    @Override
    public ArrayList<Product> saveUpdate() {
        return null;
    }

    // method for validating
    double inputPrice() {
        Scanner sc = new Scanner(System.in);
        String value;
        while (true) {
            System.out.print("Please input price : ");
            value = sc.next();
            boolean isValidValue = Pattern.matches("\\d+", value);
            if (!isValidValue)
                System.out.println("You cannot input text here !");
            else
                return Double.parseDouble(value);
        }
    }

    int inputQuantity() {
        Scanner sc = new Scanner(System.in);
        String value;
        while (true) {
            System.out.print("Please input quantity : ");
            value = sc.next();
            boolean isValidValue = Pattern.matches("\\d+", value);
            if (!isValidValue)
                System.out.println(red + "You cannot input text here !" + reset);
            else
                return Integer.parseInt(value);
        }
    }

    public static void main(String[] args) {
        ProductImplementDao testDao = new ProductImplementDao();
        testDao.backupData();
    }


}

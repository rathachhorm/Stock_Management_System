package com.kshrd;

import com.kshrd.Controller.ProductController;
import com.kshrd.Model.*;
import com.kshrd.View.ProductView;

import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class Main {
    public static final String red = "\u001B[31m";
    public static final String green = "\u001B[32m";
    public static final String yellow = "\u001B[33m";

    public static final String blue = "\u001B[34m";
    public static final String reset = "\u001B[0m";
    public static String choice;
    static int currentPage = 1;

    public static void main(String[] args) throws SQLException {

        ProductDao productDAO = new ProductImplementDao();
        ProductView productView = new ProductView();
        ProductController controller = new ProductController(productDAO, productView);

        do {
            ArrayList<Product> products;
            products = productDAO.getAllProducts();
            Validate validate = new Validate();
            Connection connection = Connect.connection();
            Statement statement = connection.createStatement();

            ResultSet rs = statement.executeQuery("SELECT num_row FROM set_row");
            int count = 0;
            if (rs.next()) {
                count = rs.getInt("num_row");
            }
            int recordsPerPage = count; // Adjust the number of records per page as needed
            int totalPages = (int) Math.ceil((double) products.size() / recordsPerPage);

            CellStyle center = new CellStyle(CellStyle.HorizontalAlign.CENTER);
            Table t = new Table(5, BorderStyle.UNICODE_ROUND_BOX, ShownBorders.ALL);
            t.setColumnWidth(0, 15, 15);
            t.setColumnWidth(1, 30, 30);
            t.setColumnWidth(2, 20, 20);
            t.setColumnWidth(3, 15, 15);
            t.setColumnWidth(4, 15, 15);

            t.addCell(blue + "ID" + reset, center);
            t.addCell(blue + "Name" + reset, center);
            t.addCell(blue + "Unit Price" + reset, center);
            t.addCell(blue + "Qty" + reset, center);
            t.addCell(blue + "Import Date" + reset, center);


            int startIndex = (currentPage - 1) * recordsPerPage;
            int endIndex = Math.min(startIndex + recordsPerPage, products.size());

            for (int i = startIndex; i < endIndex; i++) {
                t.addCell(green + Integer.toString(products.get(i).getId()) + reset, center);
                t.addCell(products.get(i).getName(), center);
                t.addCell(Double.toString(products.get(i).getUnitPrice()), center);
                t.addCell(Integer.toString(products.get(i).getQuantity()), center);
                t.addCell(String.valueOf(products.get(i).getImportDate()), center);
            }
            t.addCell("Page : " + yellow + currentPage + reset + " of " + red + totalPages + reset, center, 2);
            t.addCell("Total Record : " + green + products.size() + reset, center, 3);

            System.out.println(t.render());
            System.out.println("___________ Display __________");
            System.out.println("\tN. Next Page \t\tP. Previous Page \t\tF. First Page \t\tL. Last Page \t\t G. Goto\n");
            System.out.println("W) Write\t\tR) Read\t\t\tU) Update\t\tD) Delete \t\tS) Search\t\tSe) Set rows\nSa) Save\t\tUn) Unsaved\t\tBa) Backup\t\tRe) Restore\t\tE)Exit\n\t\t\t\t\t\t--------------------- ");
            choice = validate.validateString(yellow + "=> Choose an option() : " + reset).toUpperCase();

            if (choice.equals("N") && currentPage < totalPages) {
                products = productDAO.getAllProducts();
                currentPage++;
            } else if (choice.equals("P") && currentPage > 1) {
                products = productDAO.getAllProducts();
                currentPage--;
            } else if (choice.equals("F")) {
                products = productDAO.getAllProducts();
                currentPage = 1;
            } else if (choice.equals("L")) {
                products = productDAO.getAllProducts();
                currentPage = totalPages;
            } else if (choice.equals("G")) {
                products = productDAO.getAllProducts();
                currentPage = Integer.parseInt(validate.validateId("page number : "));
            } else {
                switch (choice) {
                    case "W":
                        controller.insertProduct();
                        break;
                    case "R":
                        controller.getProduct();
                        break;
                    case "U":
                        controller.updatedProduct();
                        break;
                    case "D":
                        controller.deleteProduct();
                        break;
                    case "S": {
                        controller.searchByProductName();
                        break;
                    }
                    case "SE":
                        controller.setRows();
//                    productDAO.recovery();
                        break;
                    case "SA":
                        controller.saveProduct();
                        break;
                    case "UN":
                        controller.unsavedProducts();
                        break;
                    case "BA":
                        productDAO.backupData();
                        break;
                    case "RE":
                        productDAO.restoreBackup();
                        products = productDAO.getAllProducts();
                        break;
                    case "E": {
                        System.out.println(green + "===== Good bye ====" + reset);
                        System.exit(0);
                        break;
                    }
                    default:
                        System.out.println(red + "Invalid option" + reset);
                        break;
                }
            }

        } while (true);

    }

}
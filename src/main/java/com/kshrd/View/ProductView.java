package com.kshrd.View;

import com.kshrd.Model.Product;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.Table;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ProductView {
    public void displayProductList(ArrayList<Product> products) {
        String paginationChoice;
        int recordsPerPage = 3; // Adjust the number of records per page as needed
        int totalPages = (int) Math.ceil((double) products.size() / recordsPerPage);
        int currentPage = 1;
        Scanner sc = new Scanner(System.in);
        do {
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


            int startIndex = (currentPage - 1) * recordsPerPage;
            int endIndex = Math.min(startIndex + recordsPerPage, products.size());

            for (int i = startIndex; i < endIndex; i++) {
                t.addCell(Integer.toString(products.get(i).getId()), center);
                t.addCell(products.get(i).getName(), center);
                t.addCell(Double.toString(products.get(i).getUnitPrice()), center);
                t.addCell(Integer.toString(products.get(i).getQuantity()), center);
                t.addCell(String.valueOf(products.get(i).getImportDate()), center);
            }
            System.out.println(t.render());

            //   System.out.println("1. Next Page \t\t2. Previous Page \t\t3. First Page \t\t4. Last Page \t\t 5. Goto \t\t 6. Break");

            while (true) {
                System.out.print("Choose option from 1 - 6 : ");
                paginationChoice = sc.next();
                // paginationChoice = main.choice;
                boolean validInput = Pattern.matches("\\d+", paginationChoice);
                if (!validInput)
                    System.out.println("Please Enter number!");
                else if (Integer.parseInt(paginationChoice) < 1 || Integer.parseInt(paginationChoice) > 6)
                    System.out.println("Please enter number from 1 - 6");
                else
                    break;
            }

            if (paginationChoice.equals("1") && currentPage < totalPages) {
                currentPage++;
            } else if (paginationChoice.equals("2") && currentPage > 1) {
                currentPage--;
            } else if (paginationChoice.equals("3")) {
                currentPage = 1;
            } else if (paginationChoice.equals("4")) {
                currentPage = totalPages;
            } else if (paginationChoice.equals("5")) {
                int numPage = Integer.parseInt(paginationChoice);
                currentPage = numPage;
            } else if (paginationChoice.equals("6")) {
                break;
            }
            System.out.println("---------------& & & -----------------");
        } while (!paginationChoice.equals("6"));
    }

    ArrayList<Product> productForInsertToDB = new ArrayList<>();
    ArrayList<Product> updatedProducts = new ArrayList<>();

    public void insert(Product product) {
        productForInsertToDB.add(product);
    }

    public ArrayList<Product> saveProductToDB() {
        return productForInsertToDB;
    }

    public ArrayList<Product> UpdatedProducts() {
        return updatedProducts;
    }

    private final Scanner scanner;

    public ProductView() {
        this.scanner = new Scanner(System.in);
    }

    public void displaySearchProduct(ArrayList<Product> products) {
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
        for (Product product : products) {
            t.addCell(String.valueOf(product.getId()), center);
            t.addCell(String.valueOf(product.getName()), center);
            t.addCell(String.valueOf(product.getUnitPrice()), center);
            t.addCell(String.valueOf(product.getQuantity()), center);
            t.addCell(String.valueOf(product.getImportDate()), center);
        }
        System.out.println(t.render());
        System.out.println("Enter to continue...");
        scanner.nextLine();
    }
}


package com.kshrd.Controller;

// ProductController.java (Controller)

import java.util.ArrayList;

import com.kshrd.Model.Product;
import com.kshrd.Model.ProductDao;
import com.kshrd.View.ProductView;

public class ProductController {
    private final ProductDao productDAO;
    private final ProductView productView;

    public ProductController(ProductDao productDAO, ProductView productView) {
        this.productDAO = productDAO;
        this.productView = productView;
    }

//    public void displayProductList() {
//        ArrayList<Product> products = productDAO.getAllProducts();
//        productView.displayProductList(products);
//    }

    public void insertProduct() {
        Product products = productDAO.insertProduct();
        productView.insert(products);
    }

    public void deleteProduct() {
        productDAO.deleteProduct();
    }

    public void saveProduct() {
        ArrayList<Product> productInsertToDB = productView.saveProductToDB();
        ArrayList<Product> productForUpdateToDB = productView.UpdatedProducts();
        productDAO.saveProduct(productInsertToDB, productForUpdateToDB);
    }

    public void updatedProduct() {
        ArrayList<Product> productUpdateToDB = productView.UpdatedProducts();
        productDAO.updateProduct(productUpdateToDB);

    }

    public void unsavedProducts() {
        ArrayList<Product> productInsertToDB = productView.saveProductToDB();
        ArrayList<Product> productForUpdateToDB = productView.UpdatedProducts();
        productDAO.unSaveProduct(productInsertToDB, productForUpdateToDB);
    }

    public void getProduct() {
        productDAO.getProduct();
    }

    public void searchByProductName() {
        ArrayList<Product> products = productDAO.searchByProductName();
        productView.displaySearchProduct(products);
    }

    public void setRows() {
        productDAO.recovery();
    }


}


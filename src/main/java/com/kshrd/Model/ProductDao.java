package com.kshrd.Model;

import java.util.ArrayList;
import java.util.List;

public interface ProductDao {
    ArrayList<Product> getAllProducts();

    Product insertProduct();

    void getProduct();

    void deleteProduct();

    void updateProduct(ArrayList<Product> products);

    void saveProduct(ArrayList<Product> products, ArrayList<Product> updatedProducts);

    void unSaveProduct(ArrayList<Product> unsavedInsertProducts, ArrayList<Product> unsavedUpdateProducts);

    ArrayList<Product> searchByProductName();

    void recovery();

    void backupData();

    void restoreBackup();

    ArrayList<Product> saveData();

    ArrayList<Product> saveUpdate();

}

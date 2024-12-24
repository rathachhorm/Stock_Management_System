package com.kshrd.Model;

import java.sql.*;
import java.util.ArrayList;

public class Connect {
    public static Connection connection() {
        String url = "jdbc:postgresql://localhost:5432/mini_project_db";
        String user = "postgres";
        String pass = "1234";

        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, user, pass);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return connection;
    }

}


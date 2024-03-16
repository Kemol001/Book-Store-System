package com.book.store.system.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {

    private static String connectioString = "jdbc:sqlite:Database.db";

    private DB(){
        //This is a singleton class
    }

    public static Connection getConnection(){
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(connectioString);
        } catch (SQLException e) {
            System.out.println("Error while connecting to the database");
            e.printStackTrace();
            return null;
        }

        return connection;
    }

   
    
}

package com.book.store.system.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class db {

    private static String connectioString = "jdbc:sqlite:sample.db";
    private static Connection connection = null;

    private db(){
        //This is a singleton class
    }

    public static Connection connect(){
        if(connection == null){
            try {
                connection = DriverManager.getConnection(connectioString);
            } catch (SQLException e) {
                System.out.println("Error while connecting to the database");
                e.printStackTrace();
                return null;
            }
        }
        return connection;
    }
    
}

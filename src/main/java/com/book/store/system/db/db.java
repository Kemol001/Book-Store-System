package com.book.store.system.Db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.book.store.system.Entities.*;

public class Db {

    private static String connectioString = "jdbc:sqlite:library.db";
    private static Connection connection = null;

    private Db(){
        //This is a singleton class
    }

    public static Connection connect(){
        if(connection == null){
            try {
                return DriverManager.getConnection(connectioString);
            } catch (SQLException e) {
                System.out.println("Error while connecting to the database");
                e.printStackTrace();
                return null;
            }
        }
        return connection;
    }

    public static boolean init(){
        try {
            connection = DriverManager.getConnection(connectioString);
            if(User.init(connection) && Book.init(connection) && Request.init(connection))
                return true;
        } catch (SQLException e) {
            // e.printStackTrace();
            System.out.println("Error while initializing the database");
            return false;
        }
        return false;
    }
    
}
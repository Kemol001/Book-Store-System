
package com.book.store.system.Database;


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
        // if(connection == null){
            try {
                return DriverManager.getConnection(connectioString);
            } catch (SQLException e) {
                System.out.println("Error while connecting to the database");
                // e.printStackTrace();
                return null;
            }
        // }
        // return connection;
    }


    public static boolean init(){
        try {
            connection = DriverManager.getConnection(connectioString);
            if( new User().init(connection) && 
                new Book().init(connection) && 
                new Request().init(connection) && 
                new Message().init(connection) )
                return true;
        } catch (SQLException e) {
            // e.printStackTrace();
            return false;
        }
        return false;
    }


    public static boolean initDummy(){
        try {
            connection = DriverManager.getConnection(connectioString);
            new User().dummyUser(connection);
            new Book().dummyBook(connection);
            return true;
        } catch (SQLException e) {
            // e.printStackTrace();
        }
        return false;
    }

}

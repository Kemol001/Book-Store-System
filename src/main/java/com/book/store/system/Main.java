package com.book.store.system;

import org.mindrot.jbcrypt.BCrypt;

public class Main {
    public static void main(String[] args) {
        String password = "mySecretPassword";

        // Hashing the password
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        // Printing the hashed password
        System.out.println("Hashed password: " + hashedPassword);

        // Checking if the password matches the hashed value
        if (BCrypt.checkpw(password, hashedPassword)) {
            System.out.println("Password matches the hashed value.");
        } else {
            System.out.println("Password does not match the hashed value.");
        }
    }
}

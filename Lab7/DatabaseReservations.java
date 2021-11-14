import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;

import java.sql.Date;
import java.util.Scanner;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import java.util.*;
import java.lang.*;
import java.io.*;
import java.util.Random;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.Instant;
import java.util.Calendar;

public class DatabaseReservations {
    private static Connection connection = null;

    private DatabaseReservations() {
    }

    public static Connection getConnection() {
        if (connection == null) {
            try {
                establishConnection();
            } catch (SQLException se) {
                System.err.println(se.getMessage());
                System.err.println("Unable to connect to database");
                System.exit(1);
            }
        }
        System.out.println("Connected to database");
        return connection;
    }

    private static void establishConnection() throws SQLException {
        connection = DriverManager.getConnection(System.getenv("LAB7_JDBC_URL"),
                System.getenv("LAB7_JDBC_USER"),
                System.getenv("LAB7_JDBC_PW"));
    }

    private static void printOptions() {
        System.out.println("\nMain Menu");
        System.out.println("[1]Rooms and Rates");
        System.out.println("[2]Book Resrvations");
        System.out.println("[3]Change Resrvations");
        System.out.println("[4]Cancel Resrvations");
        System.out.println("[5]Revenue Summary");
        System.out.println("[M]ain Menu");
        System.out.println("[0]Exit\n");
    }

    private static void optionSelect() {
        String command;

        try {
            Scanner scanner = new Scanner(System.in);
            printOptions();
            System.out.print("Input Command: ");

            while (scanner.hasNext()) {
                String option_selected = scanner.next();
                option_selected = option_selected.replaceAll("\\s", "");

                if (option_selected.equals("1")) {
                    System.out.println("\n1...");
                    FR1();
                    System.out.println();
                    printOptions();
                } else if (option_selected.equals("2")) {
                    System.out.println("\n2...");
                    FR2();
                    System.out.println();
                    printOptions();
                } else if (option_selected.equals("3")) {
                    System.out.println("\n3...");
                    FR3();
                    System.out.println();
                    printOptions();
                } else if (option_selected.equals("4")) {
                    System.out.println("\n4...");
                    FR4();
                    System.out.println();
                    printOptions();
                }

            }
        }
    }
}
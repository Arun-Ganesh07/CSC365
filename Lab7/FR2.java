import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.text.ParseException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.Map;
import java.util.Scanner;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Calendar;

import java.time.ZoneId;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FR2 {
    private static final String RESERVATIONS_TABLE = "aganes01.lab7_reservations";
    private static final String ROOMS_TABLE = "aganes01.lab7_rooms";
    private Connection conn;
    Scanner sc = new Scanner(System.in);

    private String firstname;
    private String lastname;
    private String roomCode;
    private String bedType;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private int nChildren;
    private int nAdults;

    private List<Room> availRooms; //Rooms that fit request
    private List<Room> closeRooms; //Rooms that are close

    public void makeReservation() throws SQLException {
        userInput(); //fill the fillable fields
        if (available()){
            option();
        }
        else{
            closeAvailable();
            closeOption();
        }
    }

    private void option(){
    }

    private boolean available() throws SQLException {
        try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
                System.getenv("HP_JDBC_USER"),
                System.getenv("HP_JDBC_PW"))) {
            // dates non-conflicting string
            String dncs = "SELECT DISTINCT * FROM " +
                    ROOMS_TABLE +
                    "WHERE " +
                    "roomCode = ? AND " +
                    "bedType = ? AND " +
                    "NOT EXISTS" +
                    "(SELECT * FROM" + ROOMS_TABLE + "JOIN "+ RESERVATIONS_TABLE+" ON RoomId=Room " +
                    "   WHERE NOT (checkIn BETWEEN ? AND ?) AND NOT (checkOut BETWEEN ? AND ?)" +
                    ")";

            // dates non-conflicting prepared statement
            try (PreparedStatement dncsps = conn.prepareStatement(dncs)) {

                dncsps.setString(1, roomCode);
                dncsps.setString(2, bedType);
                dncsps.setDate(3, java.sql.Date.valueOf(checkIn));
                dncsps.setDate(4, java.sql.Date.valueOf(checkOut));
                dncsps.setDate(5, java.sql.Date.valueOf(checkIn));
                dncsps.setDate(6, java.sql.Date.valueOf(checkOut));

                try (ResultSet rs = dncsps.executeQuery()) {
                    while (rs.next()) {
                        availRooms.add(new Room(rs));
                    }
                    return !availRooms.isEmpty();
                }
            }
        }
    }

    private void closeOption(){

    }
    private void closeAvailable() throws SQLException {

    }

    private void userInput() {
            System.out.println("firstname: ");
            firstname = sc.nextLine();

            System.out.println("lastname: ");
            lastname = sc.nextLine();

            System.out.println("roomCode: ");
            roomCode = sc.nextLine();

            System.out.println("bedType: ");
            bedType = sc.nextLine();

            System.out.println("checkIn: ");
            checkIn = LocalDate.parse(sc.nextLine());

            System.out.println("checkOut: ");
            checkOut = LocalDate.parse(sc.nextLine());

            System.out.println("nChildren: ");
            nChildren = sc.nextInt();

            System.out.println("nAdults: ");
            nAdults = sc.nextInt();
    }

    private class Room {
        public String RoomCode;
        public String RoomName;
        public int beds;
        public String bedType;
        public int maxOcc;
        public float basePrice;
        public String decor;

        Room(java.sql.ResultSet rs) throws SQLException{
            RoomCode = rs.getString("RoomCode");
            RoomName = rs.getString("RoomName");
            beds = rs.getInt("beds");
            bedType = rs.getString("bedType");
            basePrice = rs.getFloat("basePrice");
            maxOcc = rs.getInt("maxOcc");
            decor = rs.getString("decor");
        }

        public String toString() {
            return String.join(" ",
                    RoomCode, RoomName, String.valueOf(beds),
                    bedType, String.valueOf(basePrice),
                    String.valueOf(basePrice), String.valueOf(maxOcc),
                    decor
            );
        }
    }
}

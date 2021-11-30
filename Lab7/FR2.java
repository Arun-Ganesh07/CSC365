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

    private void option() throws SQLException{
        System.out.println("---Available Room Options---");
        for (int i=0; i < availRooms.size(); i++){
            System.out.println(String.valueOf(i) + ": " + availRooms.get(i).toString());
        }
        reserve(availRooms.get(sc.nextInt()));
    }

    //if rooms would be valid for reservation, returns them as objects
    private boolean available() throws SQLException {
        boolean roomAny = false;
        boolean bedAny = false;
        try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
                System.getenv("HP_JDBC_USER"),
                System.getenv("HP_JDBC_PW"))) {
            // dates non-conflicting string
            String dncs = "SELECT DISTINCT * FROM " +
                    ROOMS_TABLE +
                    "WHERE " +
                    "maxOcc >= ?" +
                    "NOT EXISTS" +
                    "(SELECT * FROM" + ROOMS_TABLE + "JOIN "+ RESERVATIONS_TABLE+" ON RoomId=Room " +
                    "   WHERE NOT (checkIn BETWEEN ? AND ?) AND NOT (checkOut BETWEEN ? AND ?)" +
                    ")";
            if (!roomCode.toLowerCase().equals("any")){
                dncs = dncs + " AND roomCode = ?";
                roomAny=true;
            }
            if (!bedType.toLowerCase().equals("any")){
                dncs = dncs + " AND bedType = ?";
                bedAny=true;
            }

            // dates non-conflicting prepared statement
            try (PreparedStatement dncsps = conn.prepareStatement(dncs)) {
                dncsps.setInt(1,nChildren+nAdults);
                dncsps.setDate(2, java.sql.Date.valueOf(checkIn));
                dncsps.setDate(3, java.sql.Date.valueOf(checkOut));
                dncsps.setDate(4, java.sql.Date.valueOf(checkIn));
                dncsps.setDate(5, java.sql.Date.valueOf(checkOut));

                if (!roomAny){
                    dncsps.setString(6, roomCode);
                    if (!bedAny){
                        dncsps.setString(7, bedType);
                    }
                }
                else if (!bedAny){
                    dncsps.setString(6, bedType);
                }

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
        try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
                System.getenv("HP_JDBC_USER"),
                System.getenv("HP_JDBC_PW"))) {
            String ps = "SELECT * FROM "+RESERVATIONS_TABLE+
                    " ";
        }
    }
    //checks if the room is available on this day
    private boolean dateCheck(LocalDate date) throws SQLException {
        try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
                System.getenv("HP_JDBC_USER"),
                System.getenv("HP_JDBC_PW"))) {
            String s = "SELECT * FROM " + RESERVATIONS_TABLE +
                    " WHERE" +
                    " ? BETWEEN checkIn AND checkOut";
            try (PreparedStatement ps = conn.prepareStatement(s)) {
                try (ResultSet rs = ps.executeQuery()) {
                    return !rs.next();
                }
            }
        }
    }

    //TODO: return max resCode + 1 using DBMS
    private int newReservationCode(){
        return 0;
    }

    private void reserve(Room choiceRoom) throws SQLException{
        try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
                System.getenv("HP_JDBC_USER"),
                System.getenv("HP_JDBC_PW"))) {
            // dates non-conflicting string
            String r = "INSERT INTO" +
                    RESERVATIONS_TABLE +
                    "(CODE, Room, CheckIn, Checkout, Rate, LastName, FirstName, Adults, Kids) " +
                    "VALUES " +
                    "(?, ?, ?, ?, ?, ?, ?, ?, ?)";

            // dates non-conflicting prepared statement
            try (PreparedStatement rPS = conn.prepareStatement(r)) {
                rPS.setInt(1, 0);
                rPS.setString(2,choiceRoom.RoomCode);
                rPS.setDate(3,java.sql.Date.valueOf(checkIn));
                rPS.setDate(4,java.sql.Date.valueOf(checkOut));
                rPS.setFloat(5,choiceRoom.basePrice);
                rPS.setString(6,lastname);
                rPS.setString(7,firstname);
                rPS.setInt(8,nAdults);
                rPS.setInt(9,nChildren);

                rPS.executeQuery();
            }
        }
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

}

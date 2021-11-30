import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;

import java.util.Scanner;

import java.lang.*;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
public class InnReservations {
    private static final String RESERVATIONS_TABLE = "aganes01.lab7_reservations";
    private static final String ROOMS_TABLE = "aganes01.lab7_rooms";
    private static Connection conn;
    //private Connection conn;


    public InnReservations() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver loaded");
        } catch (ClassNotFoundException ex) {
            System.err.println("Unable to load JDBC Driver");
            System.exit(-1);
        }

        try {
            conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
                    System.getenv("HP_JDBC_USER"),
                    System.getenv("HP_JDBC_PW"));
        } catch (SQLException e) {
            System.err.println("Unable to connect to database: " + e.getMessage());
            System.exit(-1);
        }
    }

    public static void main(String[] arg) throws SQLException {
        InnReservations i = new InnReservations();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the reservation system");
        System.out.println("1. List popular rooms");
        System.out.println("2. Make a reservation");
        System.out.println("3. Change a reservation");
        System.out.println("4. Cancel a reservation");
        System.out.println("5. View reservation details");
        System.out.println("6. List monthly revenue");
        System.out.println("Or q to quit\n");
        System.out.print("Please enter a command: ");

        String input = scanner.nextLine();

        while (!input.equals("exit")) {

            switch (input) {
                case "q":
                    return;
                case "1":
                    FR1();
                    break;
//                case "2":
//                    FR2();
//                    break;
                case "3":
                    FR3();
                    break;
                case "4":
                    FR4();
                    break;
                case "5":
                    FR5();
                    break;
                case "6":
                    FR6();
                break;
                default:
                    System.out.println("Sorry, that's not a valid command\n");
                    break;
            }

            System.out.print("Please enter a command: ");
            input = scanner.nextLine();
        }
    }
    //private static void FR1 {
//        private static final String RESERVATIONS_TABLE = "aganes01.lab7_reservations";
//        private static final String ROOMS_TABLE = "aganes01.lab7_rooms";
//        private Connection connection;

    private static String FR1() {
        StringBuilder sb = new StringBuilder("with DaysOccupiedLast180 as (\n" +
                "    select \n" + "    Room,\n" +
                "    SUM(DateDiff(Checkout,\n" +
                "    case \n" +
                "        when CheckIn >=  curdate() - interval 180 day\n" +
                "        then CheckIn\n" +
                "        else CheckIn = 0\n" +
                "    end\n" +
                "    )) as DaysOccupied\n" +
                "    from lab7_reservations\n" +
                "    join lab7_rooms on Room = RoomCode\n" +
                "    where CheckOut > curdate() - interval 180 day\n" +
                "    group by Room\n" +
                "),\n" +
                "MostRecentReservation as (\n" +
                "    select Room,\n" +
                "    MAX(CheckIn) as MostRecentCheckin,\n" +
                "    MAX(Checkout) as MostRecentCheckout\n" +
                "    from lab7_reservations\n" +
                "    where CheckIn <= curdate()\n" +
                "    group by Room\n" +
                "),\n" +
                "FirstAvailables as (\n" +
                "   select\n" +
                "   Room,\n" +
                "   Case\n" +
                "    When not exists (\n" +
                "     select * from lab7_reservations r2\n" +
                "     where r2.Room = r1.Room\n" +
                "     and CheckIn <= curdate()\n" +
                "     and CheckOut > curdate()\n" +
                "    )\n" +
                "    then curdate()\n" +
                "    else (\n" +
                "       select MIN(CheckOut) from lab7_reservations r2\n" +
                "       where CheckOut > curdate()\n" +
                "       and r2.Room = r1.Room\n" +
                "       and not exists (\n" +
                "        select Room from lab7_reservations r3 \n" +
                "        where r3.Room = r2.Room\n" +
                "        and r2.CheckOut = r3.CheckIn\n" +
                "       ) \n" +
                "    )\n" +
                "   end as FirstAvailable\n" +
                "   from lab7_reservations r1\n" +
                "   group by room\n" +
                ")\n" +
                "select \n" +
                "MostRecentReservation.Room,\n" +
                "RoomName,\n" +
                "Beds,\n" +
                "bedType,\n" +
                "maxOcc,\n" +
                "basePrice,\n" +
                "decor,\n" +
                "ROUND(DaysOccupied / 180, 2) as Popularity,\n" +
                "DATE_ADD(MostRecentCheckout, interval 1 day) as FirstAvailable,\n" +
                "DATEDIFF(MostRecentCheckout,MostRecentCheckin) as LastStayLength,\n" +
                "MostRecentCheckout\n" +
                "from MostRecentReservation\n" +
                "join DaysOccupiedLast180 on DaysOccupiedLast180.Room = MostRecentReservation.Room\n" +
                "join FirstAvailables on FirstAvailables.Room = MostRecentReservation.Room\n" +
                "join lab7_rooms on FirstAvailables.Room = RoomCode\n" +
                "order by Popularity desc\n" +
                ";");

        try (PreparedStatement pstmt = conn.prepareStatement(sb.toString())) {

            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("Room Info:");
                while (rs.next()) {
                    System.out.format("%s %s (%.2f) %s %s %d %n", rs.getString("Room"), rs.getString("RoomName"), rs.getDouble("Popularity"), rs.getDate("FirstAvailable").toString(), rs.getDate("MostRecentCheckout").toString(), rs.getInt("LastStayLength"));
                }
            }
        } catch (SQLException se) {
            return "Failed to run query";
        }
        return null;
    }

//    private void FR2() throws SQLException {
////         final String RESERVATIONS_TABLE = "aganes01.lab7_reservations";
////        private final String ROOMS_TABLE = "aganes01.lab7_rooms";
////        private Connection conn;
////        Scanner sc = new Scanner(System.in);
//
////        private String firstname;
////        private String lastname;
////        private String roomCode;
////        private String bedType;
////        private LocalDate checkIn;
////        private LocalDate checkOut;
////        private int nChildren;
////        private int nAdults;
//
//        List<Room> availRooms; //Rooms that fit request
//        List<Room> closeRooms; //Rooms that are close
//
//
//            userInput(); //fill the fillable fields
//            if (available()){
//                option();
//            }
//            else{
//                closeAvailable();
//                closeOption();
//            }
//        }
//
//
//        private void option() throws SQLException{
//            System.out.println("---Available Room Options---");
//            for (int i=0; i < availRooms.size(); i++){
//                System.out.println(String.valueOf(i) + ": " + availRooms.get(i).toString());
//            }
//            reserve(availRooms.get(sc.nextInt()));
//        }
//
//        //if rooms would be valid for reservation, returns them as objects
//        private boolean available() throws SQLException {
//            boolean roomAny = false;
//            boolean bedAny = false;
//            try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
//                    System.getenv("HP_JDBC_USER"),
//                    System.getenv("HP_JDBC_PW"))) {
//                // dates non-conflicting string
//                String dncs = "SELECT DISTINCT * FROM " +
//                        ROOMS_TABLE +
//                        "WHERE " +
//                        "maxOcc >= ?" +
//                        "NOT EXISTS" +
//                        "(SELECT * FROM" + ROOMS_TABLE + "JOIN "+ RESERVATIONS_TABLE+" ON RoomId=Room " +
//                        "   WHERE NOT (checkIn BETWEEN ? AND ?) AND NOT (checkOut BETWEEN ? AND ?)" +
//                        ")";
//                if (!roomCode.toLowerCase().equals("any")){
//                    dncs = dncs + " AND roomCode = ?";
//                    roomAny=true;
//                }
//                if (!bedType.toLowerCase().equals("any")){
//                    dncs = dncs + " AND bedType = ?";
//                    bedAny=true;
//                }
//
//                // dates non-conflicting prepared statement
//                try (PreparedStatement dncsps = conn.prepareStatement(dncs)) {
//                    dncsps.setInt(1,nChildren+nAdults);
//                    dncsps.setDate(2, java.sql.Date.valueOf(checkIn));
//                    dncsps.setDate(3, java.sql.Date.valueOf(checkOut));
//                    dncsps.setDate(4, java.sql.Date.valueOf(checkIn));
//                    dncsps.setDate(5, java.sql.Date.valueOf(checkOut));
//
//                    if (!roomAny){
//                        dncsps.setString(6, roomCode);
//                        if (!bedAny){
//                            dncsps.setString(7, bedType);
//                        }
//                    }
//                    else if (!bedAny){
//                        dncsps.setString(6, bedType);
//                    }
//
//                    try (ResultSet rs = dncsps.executeQuery()) {
//                        while (rs.next()) {
//                            availRooms.add(new Room(rs));
//                        }
//                        return !availRooms.isEmpty();
//                    }
//                }
//            }
//        }
//
//        private void closeOption(){
//
//        }
//        private void closeAvailable() throws SQLException {
//            try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
//                    System.getenv("HP_JDBC_USER"),
//                    System.getenv("HP_JDBC_PW"))) {
//                String ps = "SELECT * FROM "+RESERVATIONS_TABLE+
//                        " ";
//            }
//        }
//        //checks if the room is available on this day
//        private boolean dateCheck(LocalDate date) throws SQLException {
//            try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
//                    System.getenv("HP_JDBC_USER"),
//                    System.getenv("HP_JDBC_PW"))) {
//                String s = "SELECT * FROM " + RESERVATIONS_TABLE +
//                        " WHERE" +
//                        " ? BETWEEN checkIn AND checkOut";
//                try (PreparedStatement ps = conn.prepareStatement(s)) {
//                    try (ResultSet rs = ps.executeQuery()) {
//                        return !rs.next();
//                    }
//                }
//            }
//        }
//
//        //TODO: return max resCode + 1 using DBMS
//        private int newReservationCode(){
//            return 0;
//        }
//
//        private void reserve(Room choiceRoom) throws SQLException{
//            try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
//                    System.getenv("HP_JDBC_USER"),
//                    System.getenv("HP_JDBC_PW"))) {
//                // dates non-conflicting string
//                String r = "INSERT INTO" +
//                        RESERVATIONS_TABLE +
//                        "(CODE, Room, CheckIn, Checkout, Rate, LastName, FirstName, Adults, Kids) " +
//                        "VALUES " +
//                        "(?, ?, ?, ?, ?, ?, ?, ?, ?)";
//
//                // dates non-conflicting prepared statement
//                try (PreparedStatement rPS = conn.prepareStatement(r)) {
//                    rPS.setInt(1, 0);
//                    rPS.setString(2,choiceRoom.RoomCode);
//                    rPS.setDate(3,java.sql.Date.valueOf(checkIn));
//                    rPS.setDate(4,java.sql.Date.valueOf(checkOut));
//                    rPS.setFloat(5,choiceRoom.basePrice);
//                    rPS.setString(6,lastname);
//                    rPS.setString(7,firstname);
//                    rPS.setInt(8,nAdults);
//                    rPS.setInt(9,nChildren);
//
//                    rPS.executeQuery();
//                }
//            }
//        }
//
//
//
//        private void userInput() {
//            System.out.println("firstname: ");
//            firstname = sc.nextLine();
//
//            System.out.println("lastname: ");
//            lastname = sc.nextLine();
//
//            System.out.println("roomCode: ");
//            roomCode = sc.nextLine();
//
//            System.out.println("bedType: ");
//            bedType = sc.nextLine();
//
//            System.out.println("checkIn: ");
//            checkIn = LocalDate.parse(sc.nextLine());
//
//            System.out.println("checkOut: ");
//            checkOut = LocalDate.parse(sc.nextLine());
//
//            System.out.println("nChildren: ");
//            nChildren = sc.nextInt();
//
//            System.out.println("nAdults: ");
//            nAdults = sc.nextInt();
//        }
//    }

    private static void FR3() {
        try {
            Scanner scanner = new Scanner(System.in);
            List<Object> params = new ArrayList<Object>();
            // get reservation code.
            System.out.println("\nEnter your reservation code: ");
            int reserv_code = scanner.nextInt();

            scanner.nextLine(); // flush3
            StringBuilder sb = new StringBuilder("UPDATE lab7_reservations SET ");
            StringJoiner sj = new StringJoiner(", ");


            Map<String, String> toChange = new HashMap<>();

            while (true) {
                System.out.println("Select attribute to change:");
                System.out.println("1 - First name");
                System.out.println("2 - Last name");
                System.out.println("3 - Begin date");
                System.out.println("4 - End date");
                System.out.println("5 - Number of adults");
                System.out.println("6 - Number of children");
                System.out.println("0 - DONE");

                String input = scanner.next();
                scanner.nextLine();
                if (input.equalsIgnoreCase("0")) {
                    break;
                }
                switch (input) {
                    case "1":
                        System.out.println("Enter new first name (or 'no change'):");
                        String firstName = scanner.nextLine();
                        toChange.put("FirstName", firstName);
                        if (!"no change".equalsIgnoreCase(firstName)) {
                            sb.append("FirstName = ?");
                            params.add(firstName);
                        }
                        break;
                    case "2":
                        System.out.println("Enter new last name (or 'no change'):");
                        String lastName = scanner.nextLine();
                        toChange.put("LastName", lastName);
                        if (!"no change".equalsIgnoreCase(lastName)) {
                            sb.append("LastName = ?");
                            params.add(lastName);
                        }
                        break;
                    case "3":
                        System.out.println("Enter new begin date (or 'no change'): ");
                        String begin = scanner.next();
                        //String checkIn = String.valueOf(scanner.nextLine());
                        toChange.put("CheckIn", begin);
                        if (!"no change".equalsIgnoreCase(begin)) {
                            sb.append("CheckIn = ?");
                            params.add(begin);
                        } else {
                            System.out.println("Conflict with reservation check-in date");
                        }
                        break;
                    case "4":
                        System.out.println("Enter new end date (or 'no change'): ");
                        String end = scanner.next();
                        toChange.put("Checkout", end);
                        if (!"no change".equalsIgnoreCase(end)) {
                            sb.append("Checkout = ?");
                            params.add(end);
                        } else {
                            System.out.println("Conflict with reservation check-out date");
                        }
                        break;
                    case "5":
                        System.out.println("Enter number of adults (or 'no change'): ");
                        String adults = scanner.next();
                        toChange.put("Adults", adults);
                        if (!"no change".equalsIgnoreCase(adults)) {
                            sb.append("Adults = ?");
                            params.add(adults);
                        }
                        break;
                    case "6":
                        System.out.println("Enter number of kids (or 'no change'):");
                        String kids = scanner.next();
                        toChange.put("Kids", kids);
                        if (!"no change".equalsIgnoreCase(kids)) {
                            sb.append("Kids = ?");
                            params.add(kids);
                        }
                        break;
                }
            }
            conn.setAutoCommit(false);
            if (toChange.containsKey("FirstName")) {
                try (PreparedStatement preparedStatement = conn.prepareStatement("update lab7_reservations set firstname=? where code = ?;")) {
                    preparedStatement.setString(1, toChange.get("FirstName"));
                    preparedStatement.setInt(2, reserv_code);
                    preparedStatement.executeUpdate();
                    conn.commit();
                    System.out.println("Success updating FirstName.");
                } catch (SQLException e) {
                    System.out.println("\nError updating FirstName");
                    e.printStackTrace();
                    conn.rollback();
                }
            }
            if (toChange.containsKey("LastName")) {
                try (PreparedStatement preparedStatement = conn.prepareStatement("update lab7_reservations set LastName=? where code = ?;")) {
                    preparedStatement.setString(1, toChange.get("LastName"));
                    preparedStatement.setInt(2, reserv_code);
                    preparedStatement.executeUpdate();
                    conn.commit();
                    System.out.println("Success updating LastName.");
                } catch (SQLException e) {
                    System.out.println("\nError updating LastName");
                    e.printStackTrace();
                    conn.rollback();
                }
            }
            if (toChange.containsKey("Adults")) {
                try (PreparedStatement preparedStatement = conn.prepareStatement("update lab7_reservations set Adults=? where code = ?;")) {
                    preparedStatement.setInt(1, Integer.parseInt(toChange.get("Adults")));
                    preparedStatement.setInt(2, reserv_code);
                    preparedStatement.executeUpdate();
                    conn.commit();
                    System.out.println("Success updating Adults.");
                } catch (SQLException e) {
                    System.out.println("\nError updating Adults");
                    e.printStackTrace();
                    conn.rollback();
                }
            }
            if (toChange.containsKey("Kids")) {
                try (PreparedStatement preparedStatement = conn.prepareStatement("update lab7_reservations set Kids=? where code = ?;")) {
                    preparedStatement.setInt(1, Integer.parseInt(toChange.get("Kids")));
                    preparedStatement.setInt(2, reserv_code);
                    preparedStatement.executeUpdate();
                    conn.commit();
                    System.out.println("Success updating Kids.");
                } catch (SQLException e) {
                    System.out.println("\nError updating Kids");
                    e.printStackTrace();
                    conn.rollback();
                }
            }

            if (toChange.containsKey("CheckIn")) {
                try (PreparedStatement preparedStatement = conn.prepareStatement("update lab7_reservations set CheckIn=? where code = ?;")) {
                    preparedStatement.setString(1, toChange.get("CheckIn"));
                    preparedStatement.setInt(2, reserv_code);
                    preparedStatement.executeUpdate();
                    conn.commit();
                    System.out.println("Success updating CheckIn.");
                } catch (SQLException e) {
                    System.out.println("\nError updating CheckIn");
                    e.printStackTrace();
                    conn.rollback();
                }
            }
            if (toChange.containsKey("Checkout")) {
                try (PreparedStatement preparedStatement = conn.prepareStatement("update lab7_reservations set Checkout=? where code = ?;")) {
                    preparedStatement.setString(1, toChange.get("Checkout"));
                    preparedStatement.setInt(2, reserv_code);
                    preparedStatement.executeUpdate();
                    conn.commit();
                    System.out.println("Success updating Checkout.");
                } catch (SQLException e) {
                    System.out.println("\nError updating Checkout");
                    e.printStackTrace();
                    conn.rollback();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    private static void FR4() throws SQLException {
        Scanner sc = new Scanner(System.in);

        System.out.println("Please enter the Reservation Code " +
                "for the reservation to cancel: ");
        final int res_code = sc.nextInt();

        System.out.println("Enter 'y' to confirm deletion of reservation " + res_code + ":");
        Scanner s2 = new Scanner(System.in);
        String confirmation = sc.nextLine();
        Scanner s3 = new Scanner(System.in);
        if (confirmation.equals("y")) {
            StringBuilder sb = new StringBuilder("DELETE FROM lab7_reservations WHERE CODE = ?");
            //Scanner s6 = new Scanner(System.in);
            conn.setAutoCommit((false));
            try(PreparedStatement pstmt = conn.prepareStatement(sb.toString())){
                pstmt.setInt(1,res_code);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    System.out.format("\tDeleting reservation for %s %s...\n", rs.getString("Firstname"), rs.getString("Lastname"));
                }
                Scanner s = new Scanner(System.in);
            }
            catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        //Scanner s = new Scanner(System.in);
    }


    private static void FR5() throws SQLException {
        int resCount = 0;
        Scanner s = new Scanner(System.in);
        System.out.println("Enter first name:");
        String firstName = s.nextLine();

        if (firstName.isEmpty()) {
            firstName = "";
        }


        Scanner s2 = new Scanner(System.in);
        System.out.println("Enter last name:");
        String lastName = s.nextLine();

        if (lastName.isEmpty()) {
            lastName = "";
        }

        Scanner s3 = new Scanner(System.in);
        System.out.println("Enter date range (2 dates separated by a space): ");
        String dates = s.nextLine();

        if (dates.isEmpty()) {
            dates = "";
        }

        if (dates != "") {
            String[] datecheck = dates.split(" ");
            if (datecheck.length != 2) {
                while (datecheck.length != 2 && (!dates.equals(""))) {
                    Scanner s8 = new Scanner(System.in);
                    System.out.println("Enter proper date range (2 dates separated by a space):");
                    dates = s.nextLine();
                    if (dates.isEmpty()) {
                        dates = "";
                    }
                    datecheck = dates.split(" ");

                }
            }
        }


        Scanner s5 = new Scanner(System.in);
        System.out.println("Enter room code: ");
        String roomCode = s.nextLine();
        if (roomCode.isEmpty()) {
            roomCode = "";
        }

        Scanner s6 = new Scanner(System.in);
        System.out.println("Enter reservation code: ");
        String reservationCode = s6.nextLine();

        if (reservationCode.isEmpty()) {
            reservationCode = "-1";
        }

        int resCode = Integer.parseInt(reservationCode);


        StringBuilder query = new StringBuilder("");

        query.append("SELECT * from lab7_reservations \n");

        if ((firstName.equals("")) && (lastName.equals("")) && dates.equals("") && roomCode.equals("") && resCode == -1) {

        } else {
            query.append("WHERE ");
        }

        if (firstName != "") {
            query.append("FirstName LIKE ");
            query.append("'" + firstName + "'");
            resCount += 1;
        }


        if (lastName != "") {

            if (resCount > 0) {
                query.append(" and ");

            }

            query.append("LastName LIKE ");
            query.append("'" + lastName + "'");
            resCount += 1;
        }


        if (dates != "") {
            if (resCount > 0) {
                query.append(" and ");

            }
            String[] rDates = dates.split(" ");
            query.append("CheckIn between ");
            query.append("'" + rDates[0] + "'");
            query.append(" and ");
            query.append("'" + rDates[1] + "'");
            resCount += 1;
        }


        if (roomCode != "") {
            if (resCount > 0) {
                query.append(" and ");

            }
            query.append("Room LIKE ");
            query.append("'" + roomCode + "'");
            resCount += 1;
        }


        if (resCode != -1) {
            if (resCount > 0) {
                query.append(" and ");

            }
            query.append("CODE = ");
            query.append(resCode);
            resCount += 1;
        }


       // System.out.print(query);

        try (PreparedStatement pstmt = conn.prepareStatement(query.toString())) {

            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("Room Info:");
                while (rs.next()) {
                    System.out.format("%d %s %s %s (%.2f) %s %s %d %d %n", rs.getInt("CODE"), rs.getString("Room"), rs.getDate("CheckIn").toString(), rs.getDate("Checkout").toString(), rs.getFloat("Rate"), rs.getString("FirstName"), rs.getString("LastName"), rs.getInt("Adults"), rs.getInt("Kids"));


                }


            }


        }


    }
    private static void FR6() throws SQLException {
        StringBuilder query = new StringBuilder("");

    //t1
        query.append("WITH t1 as ( \n");
        query.append("SELECT t4.rm, t4.mon, SUM(t4.sup) as sup from (\n");
        query.append("SELECT Code, lab7_reservations.Room as rm, (MONTH(CheckIn)) as mon, (CASE \n");
        query.append("    WHEN MONTH(CheckIn) = MONTH(CheckOut) and YEAR(CheckIn) = YEAR(CheckOut)\n");
        query.append("        THEN DATEDIFF(CheckOut, CheckIn)\n");
        query.append("    WHEN MONTH(CheckIn) != MONTH(CheckOut) and CheckIn != LAST_DAY(CheckIn)\n");
        query.append("        THEN DATEDIFF(LAST_DAY(CheckIn), CheckIn)\n");
        query.append("    WHEN CheckIn = LAST_DAY(CheckIn)\n");
        query.append("        THEN 1\n");
        query.append("    end) * rate as sup\n");
        query.append("from lab7_reservations\n");
        query.append("WHERE YEAR(CheckIn) = 2021\n");
        query.append("GROUP by Code, rm, mon, sup\n");
        query.append("ORDER by rm, mon asc) as t4\n");
        query.append("GROUP by t4.rm, t4.mon)\n");
        query.append(",\n");
        query.append("\n");
        query.append("\n");

    //t2
        query.append("t2 as (\n");
        query.append("SELECT t5.rm, t5.mon, SUM(t5.sup) as sup from (\n");
        query.append("SELECT Code, lab7_reservations.Room as rm, (MONTH(CheckOut)) as mon, SUM(CASE\n");
        query.append("    WHEN MONTH(CheckIn) = MONTH(CheckOut) and YEAR(CheckIn) = YEAR(CheckOut)\n");
        query.append("        THEN 0\n");
        query.append("    WHEN MONTH(CheckIn) != MONTH(CheckOut) and YEAR(CheckIn) = YEAR(CheckOut)\n");
        query.append("        THEN DATEDIFF(CheckOut, DATE_ADD(DATE_ADD(LAST_DAY(CheckOut), INTERVAL 1 DAY), INTERVAL -1 MONTH))\n");
        query.append("    ELSE\n");
        query.append("        0\n");
        query.append("    end) * rate as sup\n");
        query.append("from lab7_reservations\n");
        query.append("WHERE YEAR(CheckIn) = 2021\n");
        query.append("GROUP by Code, rm, mon\n");
        query.append("ORDER by rm, mon asc) as t5\n");
        query.append("GROUP by t5.rm, t5.mon\n");
        query.append("),\n");
        query.append("\n");

    //t3
        query.append("t3 as (\n");
        query.append("SELECT t2.rm as rm, t2.mon as mon, SUM(t1.sup + t2.sup) as sup \n");
        query.append("from t1, t2\n");
        query.append("WHERE t1.rm = t2.rm and t1.mon = t2.mon\n");
        query.append("GROUP by rm, mon with rollup\n");
        query.append(")\n");

        query.append("SELECT t3.rm,\n");
        query.append("         MAX(CASE WHEN t3.mon = 1 THEN t3.sup ELSE NULL END) AS January,\n");
        query.append("         MAX(CASE WHEN t3.mon = 2 THEN t3.sup ELSE NULL END) AS February,\n");
        query.append("         MAX(CASE WHEN t3.mon = 3 THEN t3.sup ELSE NULL END) AS March,\n");
        query.append("         MAX(CASE WHEN t3.mon = 4 THEN t3.sup ELSE NULL END) AS April,\n");
        query.append("         MAX(CASE WHEN t3.mon = 5 THEN t3.sup ELSE NULL END) AS May,\n");
        query.append("         MAX(CASE WHEN t3.mon = 6 THEN t3.sup ELSE NULL END) AS June,\n");
        query.append("         MAX(CASE WHEN t3.mon = 7 THEN t3.sup ELSE NULL END) AS July,\n");
        query.append("         MAX(CASE WHEN t3.mon = 8 THEN t3.sup ELSE NULL END) AS August,\n");
        query.append("         MAX(CASE WHEN t3.mon = 9 THEN t3.sup ELSE NULL END) AS September,\n");
        query.append("         MAX(CASE WHEN t3.mon = 10 THEN t3.sup ELSE NULL END) AS October,\n");
        query.append("         MAX(CASE WHEN t3.mon = 11 THEN t3.sup ELSE NULL END) AS November,\n");
        query.append("         MAX(CASE WHEN t3.mon = 12 THEN t3.sup ELSE NULL END) AS December,\n");
        query.append("         MAX(CASE WHEN t3.mon is NULL THEN t3.sup ELSE NULL END) AS RoomTotal\n");
        query.append("    FROM t3, lab7_rooms as rooms\n");
        query.append("    WHERE t3.rm = rooms.RoomCode\n");
        query.append("GROUP BY rm with ROLLUP\n");

        System.out.print(query);

}



}
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
//                case "4":
//                    FR4();
//                    break;
//                case "5":
//                    FR5();
//                    break;
//                case "6":
//                    FR6();
                //break;
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
                        }else{
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
                        }
                        else{
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
        }
        catch (Exception e) {
            e.printStackTrace();
        }


    }


}
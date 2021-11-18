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
public class FR1 {
    private static final String RESERVATIONS_TABLE = "aganes01.lab7_reservations";
    private static final String ROOMS_TABLE = "aganes01.lab7_rooms";
    private Connection connection;

    private void roomsAndRates() throws SQLException {
        StringBuilder sb = new StringBuilder("with partA as " +
                "(select room, roomname, round(sum(checkout-checkin)/180,2) popularity "+
                "from " + ROOMS_TABLE + " rooms join "+ RESERVATIONS_TABLE + " reservations on roomcode=room " +
                "where checkout > date_sub(curdate(), interval 180 day) " +
                "group by room "+
                "order by popularity desc), " +

                "partB as " +
                "(select r1.room room, min(r1.checkout) nextAvailCheckin " +
                "from " + RESERVATIONS_TABLE + " r1 join " + RESERVATIONS_TABLE + " r2 " +
                "on r1.room=r2.room and r1.code<>r2.code " +
                "where r1.checkout > curdate() and r2.checkout > curdate() " +
                "and r1.checkout < r2.checkin " +
                "group by r1.room), " +

                "partC as " +
                "(with mostRecents as (select room, max(checkout) co " +
                "from " + ROOMS_TABLE + " rooms join " + RESERVATIONS_TABLE + " reservations on roomcode=room " +
                "group by room) " +

                "select mostRecents.room, datediff(checkout,checkin) lengthStay, co mostRecentCheckout " +
                "from " + RESERVATIONS_TABLE + " reservations join mostRecents " +
                "on reservations.room=mostRecents.room and co=checkout " +
                "order by datediff(checkout, checkin) desc " +
                ") " +

                "select partA.room, roomname, popularity, nextAvailCheckin, lengthStay, mostRecentCheckout " +
                "from partC join partA on partC.room=partA.room " +
                "join partB on partB.room=partC.room " +
                ";");

        try (PreparedStatement pstmt = connection.prepareStatement(sb.toString())) {

            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("Room Info:");
                while (rs.next()) {
                    System.out.format("%s %s ($%.2f) %s %s %d %n", rs.getString("room"), rs.getString("roomname"), rs.getDouble("popularity"), rs.getDate("nextAvailCheckin").toString(), rs.getDate("mostRecentCheckout").toString(), rs.getInt("lengthStay"));
                }
            }
        }
    }



}

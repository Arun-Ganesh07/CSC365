import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class FR4 {
    private static final String RESERVATIONS_TABLE = "aganes01.lab7_reservations";
    private static final String ROOMS_TABLE = "aganes01.lab7_rooms";
    private Connection connection;


    FR4() throws SQLException {
        Scanner sc = new Scanner(System.in);

        System.out.println("Please enter the Reservation Code " +
                "for the reservation to cancel: ");
        final int res_code = sc.nextInt();

        System.out.format("Enter 'y' to confirm deletion of reservation %d: ", res_code);

        if (sc.nextLine().equals("y")) {
            StringBuilder sb = new StringBuilder("DELETE FROM "+RESERVATIONS_TABLE +
                    " WHERE CODE = ?");

            try(PreparedStatement pstmt = connection.prepareStatement(sb.toString())){
                pstmt.setInt(1,res_code);
                ResultSet rs = pstmt.executeQuery();
            }
        }
    }
}

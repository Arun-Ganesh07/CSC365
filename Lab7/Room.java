import java.sql.SQLException;

public class Room {
    public String RoomCode;
    public String RoomName;
    public int beds;
    public String bedType;
    public int maxOcc;
    public float basePrice;
    public String decor;

    Room(java.sql.ResultSet rs) throws SQLException {
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
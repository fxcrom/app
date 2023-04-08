import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class DatabaseDelete {

    public static void deleteData(Record record) {
        Connection conn = null;
        PreparedStatement selectStmt = null;
        PreparedStatement deleteStmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnector.getConnection();

            // SELECT-Anweisung ausführen, um alle Einträge mit der ID zu finden
            String selectSql = "SELECT idgastos FROM gastos WHERE idgastos = ?";
            selectStmt = conn.prepareStatement(selectSql);
            selectStmt.setInt(1, record.getId());
            rs = selectStmt.executeQuery();


            // Wenn es mehr als einen Eintrag gibt, eine Fehlermeldung ausgeben und den Vorgang abbrechen
            int count = 0;
            while (rs.next()) {
                count++;
            }
            if (count > 1) {
                System.out.println("Es gibt mehr als einen Eintrag mit der ID " + record.getId());
                return;
            }

            // DELETE-Anweisung ausführen, um den Eintrag zu löschen
            String deleteSql = "DELETE FROM gastos WHERE idgastos = ?";
            deleteStmt = conn.prepareStatement(deleteSql);
            deleteStmt.setInt(1, record.getId());
            deleteStmt.executeUpdate();

            System.out.println("ID: " + record.getId());
            deleteStmt.setInt(1, record.getId());
            deleteStmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (selectStmt != null) selectStmt.close();
                if (deleteStmt != null) deleteStmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }


}

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DatabaseInsert {
    public static void insertData(List<Record> records) {
        String sql = "INSERT INTO gastos (date, month, name, description, type, value, iva, ret, inv, netvalue) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            for (Record record : records) {
                String query = "SELECT * FROM gastos WHERE date = ? AND description = ? AND value = ?";
                PreparedStatement queryStatement = connection.prepareStatement(query);
                queryStatement.setString(1, record.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
                queryStatement.setString(2, record.getDescription());
                queryStatement.setDouble(3, record.getValue());

                if (queryStatement.executeQuery().next()) {
                    System.out.println("Datensatz existiert bereits: " + record.getDate() + ", " + record.getDescription() + ", " + record.getValue());
                    continue;
                }

                statement.setString(1, record.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
                statement.setString(2, record.getMonth().format(DateTimeFormatter.ofPattern("MM-yyyy")));
                statement.setString(3, record.getName());
                statement.setString(4, record.getDescription());
                statement.setString(5, record.getType());
                statement.setDouble(6, record.getValue());
                statement.setDouble(7, record.getIva());
                statement.setDouble(8, record.getRet());
                statement.setInt(9, record.getInv());
                statement.setDouble(10, record.getNetvalue());

                statement.addBatch();
            }

            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertVentas(Ventas ventas) {
        String sql = "INSERT INTO ventas (pedidos, ventasb, ventasn, mes) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setDouble(1, ventas.getPedidos());
            statement.setDouble(2, ventas.getVentasb());
            statement.setDouble(3, ventas.getVentasn());
            statement.setString(4, ventas.getMes());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateVentas(Ventas ventas) {
        String sql = "UPDATE ventas SET pedidos = ?, totalIngresos = ?, ingresoNeto = ? WHERE mes = ?";

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setDouble(1, ventas.getPedidos());
            statement.setDouble(2, ventas.getVentasb());
            statement.setDouble(3, ventas.getVentasn());
            statement.setString(4, ventas.getMes());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}

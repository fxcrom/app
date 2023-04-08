import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DatabaseSelect {
    public static List<Record> selectData() {
        String sql = "SELECT * FROM gastos";
        List<Record> records = new ArrayList<>();
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("idgastos");
                LocalDate date = resultSet.getDate("date").toLocalDate();
                YearMonth month = YearMonth.from(date);
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                String type = resultSet.getString("type");
                double value = resultSet.getDouble("value");
                double iva = resultSet.getDouble("iva");
                double ret = resultSet.getDouble("ret");
                int inv = resultSet.getInt("inv");
                double netvalue = resultSet.getDouble("netvalue");

                Record record = new Record(id, date, month, name, description, type, value, iva, ret, inv, netvalue);
                records.add(record);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;

    }

    public static List<Record> selectDataByYear(int year) {
        String sql = "SELECT * FROM gastos WHERE YEAR(date) = ?";
        List<Record> records = new ArrayList<>();

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, year);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("idgastos");
                    LocalDate date = resultSet.getDate("date").toLocalDate();
                    YearMonth month = YearMonth.from(date);
                    String name = resultSet.getString("name");
                    String description = resultSet.getString("description");
                    String type = resultSet.getString("type");
                    double value = resultSet.getDouble("value");
                    double iva = resultSet.getDouble("iva");
                    double ret = resultSet.getDouble("ret");
                    int inv = resultSet.getInt("inv");
                    double netvalue = resultSet.getDouble("netvalue");

                    Record record = new Record(id, date, month, name, description, type, value, iva, ret, inv, netvalue);
                    records.add(record);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }

    public static List<Ventas> selectVentasData() {
        String sql = "SELECT idventas, pedidos, ventasb, ventasn, mes FROM ventas";
        List<Ventas> ventasList = new ArrayList<>();

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("idventas");
                String mes = resultSet.getString("mes");

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM", Locale.ENGLISH);
                Month month = Month.from(formatter.parse(mes));

                double pedidos = resultSet.getDouble("pedidos");
                double totalIngresos = resultSet.getDouble("ventasb");
                double ingresoNeto = resultSet.getDouble("ventasn");

                Ventas ventas = new Ventas(id, YearMonth.of(2023, month.getValue()), pedidos, totalIngresos, ingresoNeto, mes);
                ventasList.add(ventas);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ventasList;
    }




    public static List<Ventas> selectVentasDataByYear(int year) {
        String sql = "SELECT * FROM ventas WHERE YEAR(mes) = ?";
        List<Ventas> ventasList = new ArrayList<>();

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, year);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("idventas");
                    String mes = resultSet.getString("mes");
                    YearMonth month = YearMonth.parse(year + "-" + mes);
                    double pedidos = resultSet.getDouble("pedidos");
                    double totalIngresos = resultSet.getDouble("ventasb");
                    double ingresoNeto = resultSet.getDouble("ventasn");

                    Ventas ventas = new Ventas(id, month, pedidos, totalIngresos, ingresoNeto, mes);
                    ventasList.add(ventas);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ventasList;
    }

    public static boolean ventasExists(String mes) {
        String sql = "SELECT COUNT(*) FROM ventas WHERE mes = ?";

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, mes);

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                int count = resultSet.getInt(1);
                return count > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

}

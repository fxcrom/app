import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.*;

import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChartUtils {

    public static HBox createCharts(int year, List<Record> records, List<Ventas> ventasList) {
        // Daten für die Diagramme abrufen und berechnen
        Map<YearMonth, Double> monthlyExpenses = calculateMonthlyExpenses(records);
        Map<YearMonth, Double> monthlyVentas = calculateMonthlyVentas(ventasList);
        Map<String, Double> categoryExpenses = calculateCategoryExpenses(records);

        XYChart.Series<String, Number> expensesSeries = new XYChart.Series<>();
        expensesSeries.setName("Ausgaben");
        for (Map.Entry<YearMonth, Double> entry : monthlyExpenses.entrySet()) {
            expensesSeries.getData().add(new XYChart.Data<>(entry.getKey().toString(), entry.getValue()));
        }

        XYChart.Series<String, Number> ventasSeries = new XYChart.Series<>();
        ventasSeries.setName("Ventas");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM", Locale.ENGLISH);
        for (Map.Entry<YearMonth, Double> entry : monthlyVentas.entrySet()) {
            String monthString = entry.getKey().getMonth().name();
            String formattedMonth = formatter.format(Month.valueOf(monthString));
            ventasSeries.getData().add(new XYChart.Data<>(formattedMonth, entry.getValue()));
        }



        // Balkendiagramm erstellen
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        xAxis.setLabel("Monate");
        yAxis.setLabel("Kosten");

        barChart.getData().addAll(expensesSeries, ventasSeries);

        barChart.setCategoryGap(0.8);
        barChart.setBarGap(0.1);
        barChart.widthProperty().addListener((obs, oldVal, newVal) -> {
            double barWidth = newVal.doubleValue() / 12 * 0.99; // 80% Breite für die Balken
            barChart.lookupAll(".default-color0.chart-bar").forEach(n -> n.setStyle("-fx-bar-width: " + barWidth + "px;"));
        });

        // Monate als Kategorien hinzufügen
        ObservableList<String> categories = FXCollections.observableArrayList();
        for (int month = 1; month <= 12; month++) {
            YearMonth yearMonth = YearMonth.of(year, month);
            categories.add(yearMonth.toString());
        }
        xAxis.setCategories(categories);

        // Balkendiagramm-Daten hinzufügen

        ventasSeries.setName("Ventas");

        for (Map.Entry<YearMonth, Double> entry : monthlyVentas.entrySet()) {
            String monthString = entry.getKey().getMonth().name();
            String formattedMonth = formatter.format(Month.valueOf(monthString));
            ventasSeries.getData().add(new XYChart.Data<>(formattedMonth, entry.getValue()));
        }


        // Kreisdiagramm erstellen
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Double> entry : categoryExpenses.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }
        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setTitle("Kosten pro Kategorie");

        // Erstelle die Kacheln
        VBox barChartTile = new VBox(barChart);
        VBox pieChartTile = new VBox(pieChart);
        barChartTile.setStyle("-fx-border-color: #000000; -fx-border-width: 1; -fx-background-color: #FFFFFF;");
        pieChartTile.setStyle("-fx-border-color: #000000; -fx-border-width: 1; -fx-background-color: #FFFFFF;");

        // Setze Höhe und relative Breite für die Kacheln
        barChartTile.setMinHeight(500);
        pieChartTile.setMinHeight(500);
        barChartTile.setMaxHeight(500);
        pieChartTile.setMaxHeight(500);
        HBox.setHgrow(barChartTile, Priority.ALWAYS);
        HBox.setHgrow(pieChartTile, Priority.ALWAYS);

        // Füge Balkendiagramm und Kreisdiagramm in einem HBox-Layout hinzu
        HBox hbox = new HBox(20, barChartTile, pieChartTile);
        hbox.setPadding(new Insets(20));

        return hbox;
    }


    private static Map<YearMonth, Double> calculateMonthlyExpenses(List<Record> records) {
        Map<YearMonth, Double> expenses = new HashMap<>();

        for (Record record : records) {
            YearMonth month = record.getMonth();
            double value = record.getValue();
            expenses.put(month, expenses.getOrDefault(month, 0.0) + value);
        }

        return expenses;
    }

    private static Map<String, Double> calculateCategoryExpenses(List<Record> records) {
        Map<String, Double> expenses = new HashMap<>();

        for (Record record : records) {
            String category = record.getType();
            double value = record.getValue();
            expenses.put(category, expenses.getOrDefault(category, 0.0) + value);
        }

        return expenses;
    }
    private static Map<YearMonth, Double> calculateMonthlyVentas(List<Ventas> ventasList) {
        Map<YearMonth, Double> ventas = new HashMap<>();

        for (Ventas venta : ventasList) {
            YearMonth month = YearMonth.parse(venta.getMes(), DateTimeFormatter.ofPattern("MM-yyyy"));
            double value = venta.getPedidos();
            ventas.put(month, ventas.getOrDefault(month, 0.0) + value);
        }

        return ventas;
    }
}

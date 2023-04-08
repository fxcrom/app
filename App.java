import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.css.converter.StringConverter;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.scene.layout.HBox;
import java.io.File;
import java.time.LocalDate;
import java.util.List;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Erstelle einen "Übersicht"-Button und füge ihn zu einer ToolBar hinzu
        Button overviewButton = new Button("Übersicht");
        Button showExpensesButton = new Button("Ausgaben anzeigen");
        Button importExcelButton = new Button("Excel importieren");
        Button addExpenseButton = new Button("Ausgaben hinzufügen");
        Button insertVentasButton = new Button("Umsätze einfügen");

        ToolBar toolBar = new ToolBar(overviewButton, showExpensesButton, importExcelButton, addExpenseButton, insertVentasButton);


        String sql = "SELECT mes FROM ventas";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String mes = resultSet.getString("mes");
                System.out.println(mes);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        insertVentasButton.setOnAction(e -> {
            // Erstelle ein neues Fenster (Stage) für die Eingabe der Ventas-Werte
            Stage inputStage = new Stage();
            inputStage.setTitle("Ventas-Werte eingeben");


            // Erstelle das Dropdown-Menü für die Monatsauswahl
            ComboBox<Integer> monthComboBox = new ComboBox<>();
            for (int i = 1; i <= 12; i++) {
                monthComboBox.getItems().add(i);
            }
            monthComboBox.setPromptText("Monat wählen");
            monthComboBox.setOnAction(event -> {
                int selectedMonth = monthComboBox.getValue();
                YearMonth yearMonth = YearMonth.of(2023, selectedMonth);

            });

            // Erstelle die Textfelder für die Eingabe der Ventas-Werte
            TextField pedidosInput = new TextField();
            pedidosInput.setPromptText("Pedidos");
            TextField totalIngresosInput = new TextField();
            totalIngresosInput.setPromptText("Total Ingresos");
            TextField ingresoNetoInput = new TextField();
            ingresoNetoInput.setPromptText("Ingreso Neto");

            // Erstelle den "Eintragen" Button
            Button submitButton = new Button("Eintragen");
            submitButton.setOnAction(event -> {
                if (monthComboBox.getSelectionModel().isEmpty() ||
                        pedidosInput.getText().isEmpty() ||
                        totalIngresosInput.getText().isEmpty() ||
                        ingresoNetoInput.getText().isEmpty()) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Bitte füllen Sie alle Felder aus.", ButtonType.OK);
                    errorAlert.showAndWait();
                } else {
                    int selectedMonth = monthComboBox.getSelectionModel().getSelectedIndex() + 1;
                    String monthName = Month.of(selectedMonth).name();
                    YearMonth month = YearMonth.of(LocalDateTime.now().getYear(), selectedMonth);
                    Ventas ventas = new Ventas(
                            0,
                            month,
                            Double.parseDouble(pedidosInput.getText()),
                            Double.parseDouble(totalIngresosInput.getText()),
                            Double.parseDouble(ingresoNetoInput.getText()),
                            monthName
                    );

                    if (DatabaseSelect.ventasExists(monthName)) {
                        DatabaseInsert.updateVentas(ventas);
                        System.out.println("Aktualisierter Datensatz: " + ventas);
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Der Eintrag für den Monat " + ventas.getMes() + " wurde erfolgreich aktualisiert.", ButtonType.OK);
                        successAlert.showAndWait();
                    } else {
                        DatabaseInsert.insertVentas(ventas);
                        System.out.println("Eingefügter Datensatz: " + ventas);
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Der Eintrag mit (" + ventas.getPedidos() + ") wurde erfolgreich eingetragen.", ButtonType.OK);
                        successAlert.showAndWait();
                    }

                    // Schließe das Eingabefenster
                    inputStage.close();
                }
            });


            // Erstelle das Layout und füge die Steuerelemente hinzu
            VBox vbox = new VBox(10, monthComboBox, pedidosInput, totalIngresosInput, ingresoNetoInput, submitButton);
            vbox.setPadding(new Insets(20));

            // Erstelle eine Szene mit dem Layout und füge sie der Bühne hinzu
            Scene inputScene = new Scene(vbox);
            inputStage.setScene(inputScene);

            // Zeige das Eingabefenster an
            inputStage.show();
        });


        // Füge Event-Handler zum "Übersicht"-Button hinzu
        overviewButton.setOnAction(event -> {
            int selectedYear = 2023;
            List<Record> records = DatabaseSelect.selectDataByYear(selectedYear);
            List<Ventas> ventasList = DatabaseSelect.selectVentasDataByYear(selectedYear);
            HBox charts = ChartUtils.createCharts(selectedYear, records, ventasList);
            BorderPane root = (BorderPane) primaryStage.getScene().getRoot();
            root.setCenter(charts);
        });


        showExpensesButton.setOnAction(event -> {
            // Daten aus der Datenbank abrufen
            List<Record> records = DatabaseSelect.selectData();

            // ExpenseTable erstellen und anzeigen
            TableView<Record> expenseTable = createExpensesTable();
            expenseTable.getItems().addAll(records);
            BorderPane root = (BorderPane) primaryStage.getScene().getRoot();
            root.setCenter(expenseTable);
        });

        importExcelButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel-Dateien (*.xlsx)", "*.xlsx"));
            File selectedFile = fileChooser.showOpenDialog(primaryStage);

            if (selectedFile != null) {
                // Verwenden Sie den ExcelImporter, um die Daten aus der ausgewählten Datei zu importieren
                ExcelImport excelImporter = new ExcelImport();
                List<Record> records = excelImporter.importData(selectedFile);

                // Füge die Daten in die Datenbank ein
                DatabaseInsert.insertData(records);

                // Erstelle das Pop-up-Fenster
                Stage popupStage = new Stage();
                Label statusLabel = new Label("0 Datensätze importiert");
                ProgressBar progressBar = new ProgressBar(0);

                // Erstelle eine VBox für das Layout des Pop-up-Fensters
                VBox popupLayout = new VBox();
                popupLayout.getChildren().addAll(statusLabel, progressBar);
                popupLayout.setSpacing(10);
                popupLayout.setPadding(new Insets(10));

                // Erstelle die Szene für das Pop-up-Fenster
                Scene popupScene = new Scene(popupLayout, 300, 100);

                // Setze den Titel des Pop-up-Fensters
                popupStage.setTitle("Importieren...");

                // Setze die Szene für das Pop-up-Fenster und zeige es
                popupStage.setScene(popupScene);
                popupStage.show();

                // Importiere die Daten und aktualisiere die Statusanzeige
                int count = 0;
                DatabaseInsert.insertData(records);
                for (Record record : records) {
                    count++;
                    final int currentCount = count;
                    Platform.runLater(() -> {
                        statusLabel.setText(currentCount + " Datensätze importiert");
                        progressBar.setProgress((double) currentCount / records.size());
                    });
                }

                // Schließe das Pop-up-Fenster und zeige eine Bestätigungsmeldung an
                Platform.runLater(() -> {
                    popupStage.close();
                    Alert confirmationAlert = new Alert(Alert.AlertType.INFORMATION);
                    confirmationAlert.setTitle("Import abgeschlossen");
                    confirmationAlert.setHeaderText(null);
                    confirmationAlert.setContentText("Alle " + records.size() + " Datensätze wurden importiert.");
                    confirmationAlert.showAndWait();
                });
            }
        });


        addExpenseButton.setOnAction(event -> {
            AddExpenseDialog dialog = new AddExpenseDialog();
            dialog.showAndWait();
        });

        // Füge ToolBar als oberen Teil des BorderPane hinzu
        BorderPane root = new BorderPane();
        root.setTop(toolBar);

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double windowWidth = screenBounds.getWidth() * 0.8;
        double windowHeight = screenBounds.getHeight() * 0.8;

        List<Record> records = DatabaseSelect.selectDataByYear(2023);
        List<Ventas> ventasList = DatabaseSelect.selectVentasDataByYear(2023); // Füge diese Zeile hinzu
        HBox charts = ChartUtils.createCharts(2023, records, ventasList); // Füge ventasList als dritten Parameter hinzu


        // Füge das Diagramm zum BorderPane hinzu

        root.setTop(toolBar);
        root.setCenter(charts);


        Scene scene = new Scene(root, windowWidth, windowHeight);
        scene.getStylesheets().add("main.css");

        primaryStage.setTitle("Felix' App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private TableView<Record> createExpensesTable() {
        TableView<Record> tableView = new TableView<>();

        TableColumn<Record, LocalDate> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setPrefWidth(100);

        TableColumn<Record, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(100);

        TableColumn<Record, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setPrefWidth(150);

        TableColumn<Record, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeColumn.setPrefWidth(100);

        TableColumn<Record, Double> valueColumn = new TableColumn<>("Value");
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        valueColumn.setPrefWidth(100);

        TableColumn<Record, String> monthColumn = new TableColumn<>("Monat");
        monthColumn.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getDate();
            String month = date.format(DateTimeFormatter.ofPattern("MMMM"));
            return new ReadOnlyStringWrapper(month);
        });
        monthColumn.setPrefWidth(100);


        TableColumn<Record, Double> ivaColumn = new TableColumn<>("IVA");
        ivaColumn.setCellValueFactory(new PropertyValueFactory<>("iva"));
        ivaColumn.setPrefWidth(100);

        TableColumn<Record, Double> retColumn = new TableColumn<>("Retefuente");
        retColumn.setCellValueFactory(new PropertyValueFactory<>("ret"));
        retColumn.setPrefWidth(100);

        TableColumn<Record, Integer> invColumn = new TableColumn<>("Rechnung?");
        invColumn.setCellValueFactory(new PropertyValueFactory<>("inv"));
        invColumn.setPrefWidth(100);
        invColumn.setCellFactory(column -> {
            return new TableCell<Record, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item.toString());
                        if (item == 0) {
                            setTextFill(Color.BLACK);
                            setStyle("-fx-background-color: lightcoral");
                        } else {
                            setTextFill(Color.BLACK);
                            setStyle("");
                        }
                    }
                }
            };
        });

        TableColumn<Record, Double> netvalueColumn = new TableColumn<>("Nettowert");
        netvalueColumn.setCellValueFactory(new PropertyValueFactory<>("netvalue"));
        netvalueColumn.setPrefWidth(100);


        tableView.getColumns().addAll(dateColumn, monthColumn, nameColumn, descriptionColumn, typeColumn, valueColumn, ivaColumn, retColumn, invColumn, netvalueColumn);


        TableColumn<Record, Void> deleteButtonColumn = createDeleteButtonColumn();
        tableView.getColumns().add(deleteButtonColumn);

        return tableView;
    }

    private TableColumn<Record, Void> createDeleteButtonColumn() {
        TableColumn<Record, Void> deleteButtonColumn = new TableColumn<>("");

        Callback<TableColumn<Record, Void>, TableCell<Record, Void>> cellFactory = param -> {
            final TableCell<Record, Void> cell = new TableCell<>() {
                private final Button deleteButton = new Button("X");

                {
                    deleteButton.setOnAction(event -> {
                        Record record = getTableView().getItems().get(getIndex());
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Löschen bestätigen");
                        alert.setHeaderText("Sind Sie sicher, dass Sie die Ausgabe löschen möchten?");
                        alert.setContentText("Ausgabe: " + record.getName());

                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.isPresent() && result.get() == ButtonType.OK) {
                            DatabaseDelete.deleteData(record);

                            // Aktualisiere die Tabelle
                            getTableView().getItems().remove(record);
                            getTableView().refresh();

                            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                            successAlert.setTitle("Löschung erfolgreich");
                            successAlert.setHeaderText(null);
                            successAlert.setContentText("Ausgabe " + record.getName() + " wurde erfolgreich gelöscht.");
                            successAlert.showAndWait();
                        }
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(deleteButton);
                    }
                }
            };
            return cell;
        };

        deleteButtonColumn.setCellFactory(cellFactory);

        return deleteButtonColumn;
    }



}



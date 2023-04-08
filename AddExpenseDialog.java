import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import java.util.stream.Collectors;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;

public class AddExpenseDialog extends Dialog<Record> {
    private ComboBox<String> typeComboBox = new ComboBox<>();

    public AddExpenseDialog() {
        setTitle("Ausgabe hinzufügen");
        setHeaderText("Geben Sie die Details der neuen Ausgabe ein.");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        DatePicker datePicker = new DatePicker();
        TextField nameTextField = new TextField();
        TextField descriptionTextField = new TextField();
        createExpenseTypeComboBox(); // Füge die gewünschten Typen hinzu
        TextField amountTextField = new TextField();
        TextField ivaTextField = new TextField();
        TextField retTextField = new TextField();
        TextField invTextField = new TextField();
        TextField netvalueTextField = new TextField();

        grid.add(new Label("Datum:"), 0, 0);
        grid.add(datePicker, 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameTextField, 1, 1);
        grid.add(new Label("Beschreibung:"), 0, 2);
        grid.add(descriptionTextField, 1, 2);
        grid.add(new Label("Typ:"), 0, 3);
        grid.add(typeComboBox, 1, 3);
        grid.add(new Label("Betrag:"), 0, 4);
        grid.add(amountTextField, 1, 4);
        grid.add(new Label("IVA:"), 0, 5);
        grid.add(ivaTextField, 1, 5);
        grid.add(new Label("RET:"), 0, 6);
        grid.add(retTextField, 1, 6);
        grid.add(new Label("INV:"), 0, 7);
        grid.add(invTextField, 1, 7);
        grid.add(new Label("Nettowert:"), 0, 8);
        grid.add(netvalueTextField, 1, 8);

        getDialogPane().setContent(grid);

        ButtonType addButton = new ButtonType("Hinzufügen");
        getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        Button addButtonNode = (Button) getDialogPane().lookupButton(addButton);
        addButtonNode.setOnAction(event -> {
            LocalDate date = datePicker.getValue();
            String name = nameTextField.getText();
            String description = descriptionTextField.getText();
            String type = typeComboBox.getValue();
            double amount = Double.parseDouble(amountTextField.getText());
            double iva = Double.parseDouble(ivaTextField.getText());
            double ret = Double.parseDouble(retTextField.getText());
            int inv = Integer.parseInt(invTextField.getText());
            double netvalue = Double.parseDouble(netvalueTextField.getText());
            YearMonth month = YearMonth.from(date);

            Record newRecord = new Record(-1, date, month, name, description, type, amount, iva, ret, inv, netvalue);

            DatabaseInsert.insertData(Collections.singletonList(newRecord));

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Eintragung erfolgreich");
            alert.setHeaderText(null);
            alert.setContentText("Der Eintrag wurde erfolgreich hinzugefügt.");
            alert.showAndWait();
        });
    }

    private void createExpenseTypeComboBox() {
        TreeItem<String> root = new TreeItem<>("Root");

        TreeItem<String> costosFijos = new TreeItem<>("Costos fijos");
        costosFijos.getChildren().addAll(new TreeItem<>("Arriendo"), new TreeItem<>("Empleados"), new TreeItem<>("Servicios"));

        TreeItem<String> costosVariables = new TreeItem<>("Costos variables");
        costosVariables.getChildren().addAll(new TreeItem<>("Shopify"), new TreeItem<>("MercadoPago"), new TreeItem<>("Compras productos"), new TreeItem<>("Envíos y Bodega"), new TreeItem<>("Empaque"));

        TreeItem<String> marketing = new TreeItem<>("Marketing");
        marketing.getChildren().addAll(new TreeItem<>("GoogleAds"), new TreeItem<>("Influencer"));

        TreeItem<String> otros = new TreeItem<>("Otros");
        otros.getChildren().addAll(new TreeItem<>("Desarrollo página"), new TreeItem<>("Desarrollo productos"), new TreeItem<>("Retiros"), new TreeItem<>("Oficina"), new TreeItem<>("Viajes"), new TreeItem<>("Restaurante"), new TreeItem<>("Otros"));

        TreeItem<String> impuestos = new TreeItem<>("Impuestos");
        impuestos.getChildren().addAll(new TreeItem<>("4x1000"));

        root.getChildren().addAll(costosFijos, costosVariables, marketing, otros, impuestos);

        typeComboBox.setCellFactory(param -> {
            ListCell<String> cell = new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item != null && !empty) {
                        setText(item);
                    } else {
                        setText(null);
                    }
                }
            };

            return cell;
        });

        typeComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item != null && !empty) {
                    setText(item);
                } else {
                    setText(null);
                }
            }
        });

        typeComboBox.getItems().setAll(root.getChildren().stream()
                .flatMap(topLevelItem -> topLevelItem.getChildren().stream())
                .map(TreeItem::getValue)
                .collect(Collectors.toList()));
    }

}

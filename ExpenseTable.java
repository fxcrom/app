import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.TableCell;
import javafx.util.Callback;

import java.util.List;

public class ExpenseTable extends TableView<Record> {
    public ExpenseTable(List<Record> records) {
        ObservableList<Record> data = FXCollections.observableArrayList(records);

        TableColumn<Record, String> dateColumn = new TableColumn<>("Datum");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setMinWidth(100);

        TableColumn<Record, String> monthColumn = new TableColumn<>("Monat");
        monthColumn.setCellValueFactory(new PropertyValueFactory<>("month"));
        monthColumn.setMinWidth(100);

        TableColumn<Record, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setMinWidth(200);

        TableColumn<Record, String> descriptionColumn = new TableColumn<>("Beschreibung");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setMinWidth(300);

        TableColumn<Record, String> typeColumn = new TableColumn<>("Typ");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeColumn.setMinWidth(150);

        TableColumn<Record, Double> valueColumn = new TableColumn<>("Bruttowert");
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        valueColumn.setMinWidth(100);

        TableColumn<Record, Double> ivaColumn = new TableColumn<>("IVA");
        ivaColumn.setCellValueFactory(new PropertyValueFactory<>("iva"));
        ivaColumn.setMinWidth(100);

        TableColumn<Record, Double> retColumn = new TableColumn<>("Retefuente");
        retColumn.setCellValueFactory(new PropertyValueFactory<>("ret"));
        retColumn.setMinWidth(100);

        TableColumn<Record, Integer> invColumn = new TableColumn<>("Rechnung?");
        invColumn.setCellValueFactory(new PropertyValueFactory<>("inv"));
        invColumn.setMinWidth(100);

        TableColumn<Record, Double> netvalueColumn = new TableColumn<>("Nettowert");
        netvalueColumn.setCellValueFactory(new PropertyValueFactory<>("netvalue"));
        netvalueColumn.setMinWidth(100);

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


        getColumns().addAll(dateColumn, monthColumn, nameColumn, descriptionColumn, typeColumn, valueColumn, ivaColumn, retColumn, invColumn, netvalueColumn);
        setItems(data);
    }
}

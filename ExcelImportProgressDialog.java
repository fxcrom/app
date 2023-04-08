import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class ExcelImportProgressDialog extends Dialog<Void> {
    private ProgressBar progressBar;
    private Label progressLabel;
    private int totalRecords;
    private int importedRecords;

    public ExcelImportProgressDialog(int totalRecords) {
        this.totalRecords = totalRecords;
        this.importedRecords = 0;

        progressBar = new ProgressBar();
        progressBar.setProgress(0);

        progressLabel = new Label("0 / " + totalRecords);

        VBox vbox = new VBox(progressBar, progressLabel);
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        getDialogPane().setContent(vbox);

        setOnCloseRequest(event -> {
            event.consume();
        });
    }

    public void updateProgress(int importedRecords) {
        Platform.runLater(() -> {
            this.importedRecords = importedRecords;
            double progress = (double) importedRecords / totalRecords;
            progressBar.setProgress(progress);
            progressLabel.setText(importedRecords + " / " + totalRecords);
            if (importedRecords == totalRecords) {
                ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                getDialogPane().getButtonTypes().add(okButton);
                setResultConverter(buttonType -> {
                    if (buttonType == okButton) {
                        return null;
                    }
                    return null;
                });
                setTitle("Import abgeschlossen");
                progressLabel.setText("Alle " + totalRecords + " Datensätze wurden importiert");
            }
        });
    }
}


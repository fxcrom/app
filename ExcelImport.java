import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.time.ZoneId;

    public class ExcelImport {

        public List<Record> importData(File file) {
            List<Record> records = new ArrayList<>();

            try (FileInputStream fis = new FileInputStream(file);
                 Workbook workbook = WorkbookFactory.create(fis)) {

                Sheet sheet = workbook.getSheetAt(0);
                int i = 1; // Start from 1 to skip the header row
                while (true) {
                    Row row = sheet.getRow(i);

                    // Beenden Sie die Schleife, wenn eine leere Zelle in Spalte A gefunden wird
                    if (row == null || row.getCell(0) == null || row.getCell(0).getCellType() == CellType.BLANK) {
                        break;
                    }

                    // Get the values based on the cell type
                    LocalDate date;
                    Cell dateCell = row.getCell(0);

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

                    if (dateCell.getCellType() == CellType.STRING) {
                        try {
                            date = LocalDate.parse(dateCell.getStringCellValue(), formatter);
                        } catch (DateTimeParseException e) {
                            System.err.println("WARNING: Invalid date value found at row " + (i + 1) + " column A.");
                            continue;
                        }
                    } else {
                        System.err.println("WARNING: Non-string date value found at row " + (i + 1) + " column A.");
                        continue;
                    }

                    YearMonth month = YearMonth.from(date);
                    String name = getStringValue(row.getCell(2));
                    String description = getStringValue(row.getCell(3));
                    String type = getStringValue(row.getCell(4));
                    double value = getNumericValue(row.getCell(5));
                    double iva = getNumericValue(row.getCell(6));
                    double ret = getNumericValue(row.getCell(9));
                    int inv = (int) getNumericValue(row.getCell(10));
                    double netvalue = getNumericValue(row.getCell(8));

                    // Include the new columns in the Record constructor
                    Record record = new Record(-1, date, month, name, description, type, value, iva, ret, inv, netvalue);
                    records.add(record);

                    i++; // Zähler für die nächste Zeile erhöhen
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return records;
        }

        private String getStringValue(Cell cell) {
            if (cell != null && cell.getCellType() == CellType.STRING) {
                return cell.getStringCellValue();
            }
            return "";
        }

        private double getNumericValue(Cell cell) {
            if (cell != null && cell.getCellType() == CellType.NUMERIC) {
                return cell.getNumericCellValue();
            }
            return 0;


    }


}

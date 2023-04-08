import java.time.LocalDate;
import java.time.YearMonth;

public class Record {
    private int id;
    private LocalDate date;
    private YearMonth month;
    private String name;
    private String description;
    private String type;
    private double value;
    private double iva;
    private double ret;
    private int inv;
    private double netvalue;

    public Record(int id, LocalDate date, YearMonth month, String name, String description, String type, double value,
                  double iva, double ret, int inv, double netvalue) {
        this.id = id;
        this.date = date;
        this.month = month;
        this.name = name;
        this.description = description;
        this.type = type;
        this.value = value;
        this.iva = iva;
        this.ret = ret;
        this.inv = inv;
        this.netvalue = netvalue;
    }

    public double getIva() {
        return iva;
    }
    public void setIva(double iva) {
        this.iva = iva;
    }
    public double getRet() {
        return ret;
    }
    public void setRet(double ret) {
        this.ret = ret;
    }
    public int getInv() {
        return inv;
    }
    public void setInv(int inv) {
        this.inv = inv;
    }
    public double getNetvalue() {
        return netvalue;
    }
    public void setNetvalue(double netvalue) {
        this.netvalue = netvalue;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public YearMonth getMonth() {
        return month;
    }

    public void setMonth(YearMonth month) {
        this.month = month;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", date=" + date +
                ", month=" + month +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", value=" + value +
                ", iva=" + iva +
                ", ret=" + ret +
                ", inv=" + inv +
                ", netvalue=" + netvalue +
                '}';
    }
}

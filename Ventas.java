import java.time.YearMonth;

public class Ventas {
    private int id;
    private YearMonth month;
    private double pedidos;
    private double ventasb;
    private double ventasn;
    private String mes;

    public Ventas(int id, YearMonth month, double pedidos, double ventasb, double ventasn, String mes) {
        this.id = id;
        this.month = month;
        this.pedidos = pedidos;
        this.ventasb = ventasb;
        this.ventasn = ventasn;
        this.mes = mes;
    }

    public double getPedidos() {
        return pedidos;
    }

    public void setPedidos(double pedidos) {
        this.pedidos = pedidos;
    }

    public double getVentasb() {
        return ventasb;
    }

    public void setVentasb(double ventasb) {
        this.ventasb = ventasb;
    }

    public double getVentasn() {
        return ventasn;
    }

    public void setVentasn(double ventasn) {
        this.ventasn = ventasn;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public YearMonth getMonth() {
        return month;
    }

    public void setMonth(YearMonth month) {
        this.month = month;
    }
}

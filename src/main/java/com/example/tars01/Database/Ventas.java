package com.example.tars01.Database;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Ventas {
    private final SimpleStringProperty id;
    private final SimpleStringProperty fecha;
    private final SimpleStringProperty totalVenta;
    private final SimpleStringProperty pagado;
    private final SimpleStringProperty cambio;
    private final SimpleStringProperty detalles;

    public Ventas(String id, String fecha, String totalVenta,String pagado,String cambio,String detalles) {
        this.id = new SimpleStringProperty(id);
        this.fecha = new SimpleStringProperty(fecha);
        this.totalVenta = new SimpleStringProperty(totalVenta);
        this.pagado = new SimpleStringProperty(pagado);
        this.cambio = new SimpleStringProperty(cambio);
        this.detalles = new SimpleStringProperty(detalles);
    }

    public String getId() {
        return id.get();
    }

    public SimpleStringProperty idProperty() {
        return id;
    }

    public String getFecha() {
        return fecha.get();
    }

    public SimpleStringProperty fechaProperty() {
        return fecha;
    }

    public String getTotalVenta() {
        return totalVenta.get();
    }

    public SimpleStringProperty totalVentaProperty() {
        return totalVenta;
    }

    public String getPagado() {
        return pagado.get();
    }

    public SimpleStringProperty pagadoProperty() {
        return pagado;
    }

    public String getCambio() {
        return cambio.get();
    }

    public SimpleStringProperty cambioProperty() {
        return cambio;
    }

    public String getDetalles() {
        return detalles.get();
    }

    public SimpleStringProperty detallesProperty() {
        return detalles;
    }
}

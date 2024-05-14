package com.example.tars01.Database;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Producto {
    private final SimpleStringProperty name;
    private final SimpleStringProperty price;
    private final SimpleIntegerProperty quantity;

    public Producto(String name, String price) {
        this.name = new SimpleStringProperty(name);
        this.price = new SimpleStringProperty(price);
        this.quantity = new SimpleIntegerProperty(1); // Cantidad inicial es 1
    }


    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public String getPrice() {
        return price.get();
    }

    public void setPrice(String newPrice){
        price.set(newPrice);

    }

    public SimpleStringProperty priceProperty() {
        return price;
    }

    public int getQuantity() {
        return quantity.get();
    }

    public void setQuantity(int newQuantity) {
        quantity.set(newQuantity);
    }

    public SimpleIntegerProperty quantityProperty() {
        return quantity;
    }
}

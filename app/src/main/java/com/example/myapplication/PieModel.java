package com.example.myapplication;

public class PieModel {
    String product;
    String quantity;


    public PieModel(String product, String quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public String getProduct() {
        return product;
    }

    public String getQuantity() {
        return quantity;
    }
}

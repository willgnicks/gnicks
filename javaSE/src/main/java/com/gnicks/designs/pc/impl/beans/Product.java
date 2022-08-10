package com.gnicks.designs.pc.impl.beans;

public class Product {
    private int serialID;
    private String type;
    private String name;
    private int price;
    private int batchNum;

    public Product(int serialID, String type, String name, int price, int batchNum) {
        this.serialID = serialID;
        this.type = type;
        this.name = name;
        this.price = price;
        this.batchNum = batchNum;
    }

    public int getSerialID() {
        return serialID;
    }

    public void setSerialID(int serialID) {
        this.serialID = serialID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getBatchNum() {
        return batchNum;
    }

    public void setBatchNum(int batchNum) {
        this.batchNum = batchNum;
    }

    @Override
    public String toString() {
        return "Product{" +
                "serialID=" + serialID +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}

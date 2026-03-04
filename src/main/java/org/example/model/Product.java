package org.example.model;

public class Product {
    int id;
    String name;
    int categoryid;
    int price;
    int available;
    public Product(int id,String name,int categoryid,int price,int available){
        this.id=id;
        this.name=name;
        this.categoryid=categoryid;
        this.price=price;
        this.available=available;
    }
}

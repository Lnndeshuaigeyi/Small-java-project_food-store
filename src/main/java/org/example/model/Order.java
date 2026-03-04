package org.example.model;

public class Order {
    int id;
    int customerId;
    int categoryId;
    int quantity;
    int totalPaid;
    String address;
    int postcode;
    public Order(int id,int customerId,int categoryId,int quantity,int totalPaid,String address,int postcode){
        this.id=id;
        this.customerId=customerId;
        this.categoryId=categoryId;
        this.quantity=quantity;
        this.totalPaid=totalPaid;
        this.address=address;
        this.postcode=postcode;
    }
}


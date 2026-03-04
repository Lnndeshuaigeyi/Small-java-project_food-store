package org.example.model;
import java.util.*;

public class storeSystem {
    private final Map<Integer,Customer> customers;
    private final Map<Integer, Product> products;
    private final Map<Integer, Order> orders;
    private int nextOrderId=1;
    public storeSystem(){
        customers=new HashMap<>();
        products=new HashMap<>();
        orders=new HashMap<>();
    }
    public boolean registerCustomer(Customer customer){
         if(customers.containsKey(customer.id)) {
             return false;
         }
        this.customers.put(customer.id,customer);
         return true;
    }
    public boolean addProduct(Product product){
        if(products.containsKey(product.id)){
            return false;
        }
        this.products.put(product.id,product);
        return true;
    }
    public boolean placeOrder(int cusId,int productId, int quantity, String address, int postcode){
        Customer c=customers.get(cusId);
        Product p=products.get(productId);
        if(c==null||p==null||quantity<1||p.available<quantity) {
            return false;
        }
            p.available-=quantity;
            Order o=new Order(nextOrderId++,cusId,productId,quantity,p.price*quantity,address,postcode);
            orders.put(o.id,o);
            return true;
        }

    }

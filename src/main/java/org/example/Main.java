package org.example;
import org.example.model.Customer;
import org.example.model.Product;
import org.example.model.StoreSystemDB;
import org.example.model.storeSystem;

public class Main {
    public static void main(String[] args) {
        StoreSystemDB s = new StoreSystemDB();

        // 1) 注册客户
        Customer lee = new Customer(10, "Lee", "guangzhou", 510700);
        boolean cOk = s.registerCustomer(lee);
        System.out.println("registerCustomer ok = " + cOk);

        // 2) 添加商品（id=1，库存=500，单价=10）
        Product egg = new Product(1, "egg", 10, 10,500);
        boolean pOk = s.addProduct(egg);
        System.out.println("addProduct ok = " + pOk);

        // 3) 下单：客户1买商品1，买2个
        boolean ok = s.placeOrder(1, 1, 2, "guangzhou", 510700);
        System.out.println("placeOrder ok = " + ok);

        // 4) 再下一个大单测试库存不足（可选）
        boolean ok2 = s.placeOrder(1, 1, 10000, "guangzhou", 510700);
        System.out.println("placeOrder big order ok = " + ok2);
    }
}

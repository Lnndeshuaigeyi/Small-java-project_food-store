package org.example.model;
import org.example.db.Db;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StoreSystemDB {
        public boolean registerCustomer(Customer customer) {
            if (customer == null) return false;

            String sql = "INSERT INTO ofs_customers (id, name, address, postcode) VALUES (?, ?, ?, ?)";
            try (Connection conn = Db.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, customer.id);
                ps.setString(2, customer.name);
                ps.setString(3, customer.address);
                ps.setInt(4, customer.postcode);

                ps.executeUpdate();
                return true;

            } catch (Exception e) {
                // 最常见：id 重复、类型不匹配、表名列名不对
                e.printStackTrace();
                return false;
            }
        }

        public boolean addProduct(Product product) {
            if (product == null) return false;

            // 如果你的 products 表还有 categoryid，就要一起插进去
            String sql = "INSERT INTO ofs_products (id, name, categoryid,price, available) VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = Db.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, product.id);
                ps.setString(2, product.name);
                ps.setInt(3,product.categoryid);
                ps.setInt(4, product.price);       // 如果 price 是 BigDecimal/NUMERIC，后面要改成 setBigDecimal
                ps.setInt(5, product.available);

                ps.executeUpdate();
                return true;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    public boolean placeOrder(int cusId, int productId, int quantity, String address, int postcode) {
        if (quantity < 1 || address == null || address.isBlank()) return false;

        String checkCustomerSql = "SELECT 1 FROM ofs_customers WHERE id = ?";
        String getPriceSql = "SELECT price FROM ofs_products WHERE id = ?";
        String decreaseStockSql =
                "UPDATE ofs_products SET available = available - ? WHERE id = ? AND available >= ?";
        String insertOrderSql =
                "INSERT INTO ofs_orders (custid, prodid, prodquant, prodprice, totalpaid, address, postcode, deliveryid) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, NULL)";

        try (Connection conn = Db.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // 1) customer exists?
                try (PreparedStatement ps = conn.prepareStatement(checkCustomerSql)) {
                    ps.setInt(1, cusId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            return false;
                        }
                    }
                }

                // 2) get product price (and product exists)
                BigDecimal price;
                try (PreparedStatement ps = conn.prepareStatement(getPriceSql)) {
                    ps.setInt(1, productId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            return false;
                        }
                        price = rs.getBigDecimal(1);
                    }
                }

                // 3) atomic decrease stock
                int updated;
                try (PreparedStatement ps = conn.prepareStatement(decreaseStockSql)) {
                    ps.setInt(1, quantity);
                    ps.setInt(2, productId);
                    ps.setInt(3, quantity);
                    updated = ps.executeUpdate();
                }
                if (updated != 1) { // stock not enough (or product missing)
                    conn.rollback();
                    return false;
                }

                // 4) insert order
                BigDecimal totalPaid = price.multiply(BigDecimal.valueOf(quantity));
                try (PreparedStatement ps = conn.prepareStatement(insertOrderSql)) {
                    ps.setInt(1, cusId);
                    ps.setInt(2, productId);
                    ps.setInt(3, quantity);
                    ps.setBigDecimal(4, price);
                    ps.setBigDecimal(5, totalPaid);
                    ps.setString(6, address);
                    ps.setInt(7, postcode);
                    ps.executeUpdate();
                }

                // 5) commit
                conn.commit();
                return true;

            } catch (Exception e) {
                try { conn.rollback(); } catch (Exception ignore) {}
                e.printStackTrace();
                return false;
            } finally {
                try { conn.setAutoCommit(true); } catch (Exception ignore) {}
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    }


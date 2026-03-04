package org.example.repo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

public class Order_Repository {
    private final Connection conn;

    public Order_Repository(Connection conn) {
        this.conn = conn;
    }

    public record CreateOrderRequest(
            int custId,
            int prodId,
            int quantity,
            BigDecimal unitPrice,
            BigDecimal totalPaid,
            String address,
            String postcode,
            Integer deliveryId // can be null
    ) {}

    /** Inserts an order and returns the generated order id. */
    public int createOrder(CreateOrderRequest req) {
        if (req.quantity() <= 0) throw new IllegalArgumentException("quantity must be >= 1");
        if (req.unitPrice() == null || req.totalPaid() == null) throw new IllegalArgumentException("price fields required");
        if (req.address() == null || req.address().isBlank()) throw new IllegalArgumentException("address required");
        if (req.postcode() == null || req.postcode().isBlank()) throw new IllegalArgumentException("postcode required");

        final String sql = """
        INSERT INTO ofs_orders
          (custid, prodid, prodquant, prodprice, totalpaid, address, postcode, deliveryid)
        VALUES
          (?, ?, ?, ?, ?, ?, ?, ?)
        RETURNING id
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, req.custId());
            ps.setInt(2, req.prodId());
            ps.setInt(3, req.quantity());
            ps.setBigDecimal(4, req.unitPrice());
            ps.setBigDecimal(5, req.totalPaid());
            ps.setString(6, req.address());
            ps.setString(7, req.postcode());

            if (req.deliveryId() == null) {
                ps.setNull(8, Types.INTEGER);
            } else {
                ps.setInt(8, req.deliveryId());
            }

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (Exception e) {
            throw new RuntimeException("createOrder failed", e);
        }
    }
}
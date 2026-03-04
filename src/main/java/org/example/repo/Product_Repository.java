package org.example.repo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

public class Product_Repository {
    private final Connection conn;

    public Product_Repository(Connection conn) {
        this.conn = conn;
    }

    /** Small DTO for price + stock. */
    public record ProductInfo(BigDecimal price, int available) {}

    /** Returns empty if product not found. */
    public Optional<ProductInfo> findProductInfo(int productId) {
        final String sql = """
        SELECT price, available
        FROM ofs_products
        WHERE id = ?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                BigDecimal price = rs.getBigDecimal("price");
                int available = rs.getInt("available");
                return Optional.of(new ProductInfo(price, available));
            }
        } catch (Exception e) {
            throw new RuntimeException("findProductInfo failed, productId=" + productId, e);
        }
    }

    /**
     * Atomically decrease stock ONLY if enough stock is available.
     * Returns true if stock decreased, false if not enough (or product missing).
     */
    public boolean tryDecreaseStock(int productId, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be >= 1");

        final String sql = """
        UPDATE ofs_products
        SET available = available - ?
        WHERE id = ?
          AND available >= ?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, productId);
            ps.setInt(3, quantity);
            int updated = ps.executeUpdate();
            return updated == 1;
        } catch (Exception e) {
            throw new RuntimeException("tryDecreaseStock failed, productId=" + productId, e);
        }
    }
}

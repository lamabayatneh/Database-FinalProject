package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PurchaseItemDAO {

    public static void insertPurchaseItem(Connection con, int purchaseId, int bookId, int qty, double unitCost) throws SQLException {
        String sql = "INSERT INTO PurchaseItem (PurchaseID, BookID, Quantity, UnitCost) VALUES (?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, purchaseId);
            ps.setInt(2, bookId);
            ps.setInt(3, qty);
            ps.setDouble(4, unitCost);
            ps.executeUpdate();
        }
    }
}
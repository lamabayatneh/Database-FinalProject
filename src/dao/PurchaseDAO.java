package dao;

import db.DBConnection;
import model.Purchase;
import model.Supplier;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PurchaseDAO {

    public static int insertPurchaseReturnId(Connection con, int supplierId, int staffId, LocalDate date, double totalCost) throws SQLException {
        String sql = "INSERT INTO Purchase (SupplierID, StaffID, PurchaseDate, TotalCost) VALUES (?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, supplierId);
            ps.setInt(2, staffId);
            ps.setDate(3, Date.valueOf(date));
            ps.setDouble(4, totalCost);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Failed to get generated PurchaseID");
    }

    public static List<Purchase> getAllPurchases() {
        List<Purchase> list = new ArrayList<>();
        String sql = """
            SELECT pu.PurchaseID, pu.StaffID, pu.PurchaseDate, pu.TotalCost,
                   s.SupplierID, s.SupplierName, s.City, s.Email, s.Phone, s.ContactPerson
            FROM Purchase pu
            JOIN Supplier s ON s.SupplierID = pu.SupplierID
            ORDER BY pu.PurchaseDate DESC, pu.PurchaseID DESC
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Supplier sup = new Supplier(
                        rs.getInt("SupplierID"),
                        rs.getString("SupplierName"),
                        rs.getString("City"),
                        rs.getString("Email"),
                        rs.getString("Phone"),
                        rs.getString("ContactPerson")
                );

                list.add(new Purchase(
                        rs.getInt("PurchaseID"),
                        sup,
                        rs.getInt("StaffID"),
                        rs.getDate("PurchaseDate").toLocalDate(),
                        rs.getDouble("TotalCost")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
package dao;

import db.DBConnection;
import model.Supplier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {

	public static List<Supplier> getAllSuppliers() {
		List<Supplier> list = new ArrayList<>();
		String sql = "SELECT SupplierID, SupplierName, City, Email, Phone, ContactPerson FROM Supplier ORDER BY SupplierID";

		try (Connection con = DBConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				list.add(new Supplier(rs.getInt("SupplierID"), rs.getString("SupplierName"), rs.getString("City"),
						rs.getString("Email"), rs.getString("Phone"), rs.getString("ContactPerson")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static void insertSupplier(Supplier s) {
		String sql = "INSERT INTO Supplier (SupplierName, City, Email, Phone, ContactPerson) VALUES (?,?,?,?,?)";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, s.getSupplierName());
			ps.setString(2, s.getCity());
			ps.setString(3, s.getEmail());
			ps.setString(4, s.getPhone());
			ps.setString(5, s.getContactPerson());

			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void updateSupplier(Supplier s) {
		String sql = """
				    UPDATE Supplier
				    SET SupplierName=?, City=?, Email=?, Phone=?, ContactPerson=?
				    WHERE SupplierID=?
				""";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, s.getSupplierName());
			ps.setString(2, s.getCity());
			ps.setString(3, s.getEmail());
			ps.setString(4, s.getPhone());
			ps.setString(5, s.getContactPerson());
			ps.setInt(6, s.getSupplierID());

			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void deleteSupplier(int supplierId) {
		String sql = "DELETE FROM Supplier WHERE SupplierID=?";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, supplierId);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<Supplier> getSuppliersByCity(String city) {

		List<Supplier> list = new ArrayList<>();

		String sql = "SELECT * FROM Supplier WHERE City = ?";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, city);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Supplier s = new Supplier(rs.getInt("SupplierID"), rs.getString("SupplierName"), rs.getString("City"),
						rs.getString("Email"), rs.getString("Phone"), rs.getString("ContactPerson"));
				list.add(s);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

}
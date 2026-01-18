package dao;

import db.DBConnection;
import model.Customer;
import model.TopCustomer;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

	public static List<Customer> getAllCustomers() {

		List<Customer> list = new ArrayList<>();
		String sql = "SELECT * FROM Customer";

		try (Connection con = DBConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				Customer c = new Customer(rs.getInt("CustomerID"), rs.getInt("userId"), rs.getString("FullName"),
						rs.getString("Email"), rs.getString("Phone"), rs.getString("Address"), rs.getString("City"),
						rs.getDate("RegistrationDate").toLocalDate());
				list.add(c);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public static void insertCustomer(Customer c) {

		String sql = """
				    INSERT INTO Customer
				    (UserID, FullName, Email, Phone, Address, City, RegistrationDate)
				    VALUES (?, ?, ?, ?, ?, ?, ?)
				""";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, c.getUserID());
			ps.setString(2, c.getFullName());
			ps.setString(3, c.getEmail());
			ps.setString(4, c.getPhone());
			ps.setString(5, c.getAddress());
			ps.setString(6, c.getCity());
			ps.setDate(7, Date.valueOf(c.getRegistrationDate()));

			ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void updateCustomer(Customer c) {

		String sql = """
				    UPDATE Customer
				    SET FullName=?, Email=?, Phone=?, Address=?, City=?
				    WHERE CustomerID=?
				""";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, c.getFullName());
			ps.setString(2, c.getEmail());
			ps.setString(3, c.getPhone());
			ps.setString(4, c.getAddress());
			ps.setString(5, c.getCity());
			ps.setInt(6, c.getCustomerID());

			ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void deleteCustomer(int id) {

		String sql = "DELETE FROM Customer WHERE CustomerID=?";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, id);
			ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void deleteCustomerWithUser(int customerId, int userId) {

		String deleteCustomer = "DELETE FROM Customer WHERE CustomerID=?";
		String deleteUser = "DELETE FROM users WHERE id=?";

		try (Connection con = DBConnection.getConnection()) {
			con.setAutoCommit(false);

			try (PreparedStatement ps1 = con.prepareStatement(deleteCustomer);
					PreparedStatement ps2 = con.prepareStatement(deleteUser)) {

				ps1.setInt(1, customerId);
				ps1.executeUpdate();

				ps2.setInt(1, userId);
				ps2.executeUpdate();

				con.commit();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void insertCustomerWithUser(Customer c, int userId) {

		String sql = """
				INSERT INTO Customer
				(FullName, Email, Phone, Address, City, RegistrationDate)
				VALUES (?, ?, ?, ?, ?, ?)""";

		try (java.sql.Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, c.getFullName());
			ps.setString(2, c.getEmail());
			ps.setString(3, c.getPhone());
			ps.setString(4, c.getAddress());
			ps.setString(5, c.getCity());
			ps.setDate(6, java.sql.Date.valueOf(c.getRegistrationDate()));
			ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Customer getCustomerByUserId(int userId) {

		String sql = "SELECT * FROM Customer WHERE UserID = ?";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return new Customer(rs.getInt("CustomerID"), rs.getInt("UserID"), rs.getString("FullName"),
						rs.getString("Email"), rs.getString("Phone"), rs.getString("Address"), rs.getString("City"),
						rs.getDate("RegistrationDate").toLocalDate());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<TopCustomer> getTopCustomers() {
		List<TopCustomer> list = new ArrayList<>();
		String sql = """
				    SELECT c.FullName,
				           SUM(o.TotalAmount) AS TotalSpent
				    FROM Orders o
				    JOIN Customer c ON o.CustomerID = c.CustomerID
				    GROUP BY c.CustomerID, c.FullName
				    ORDER BY TotalSpent DESC
				    LIMIT 5
				""";

		try (Connection con = DBConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				String fullName = rs.getString("FullName");
				double totalSpent = rs.getDouble("TotalSpent");

				list.add(new TopCustomer(fullName, totalSpent));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

}
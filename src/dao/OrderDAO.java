package dao;

import java.sql.*;
import db.DBConnection;

import model.Order;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

	public static int createOrder(int customerId, double total) {

		String sql = """
				    INSERT INTO Orders
				    (CustomerID, OrderDate, TotalAmount, PaymentMethod, Status)
				    VALUES (?, CURDATE(), ?, 'CASH', 'NEW')
				""";

		try (Connection con = DBConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			ps.setInt(1, customerId);
			ps.setDouble(2, total);
			ps.executeUpdate();

			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next())
				return rs.getInt(1);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static List<Order> getAllOrders(LocalDate from, LocalDate to) {
		List<Order> list = new ArrayList<>();

		String sql = """
				    SELECT o.OrderID, o.CustomerID, o.OrderDate, o.TotalAmount,
				           c.FullName
				    FROM Orders o
				    JOIN Customer c ON o.CustomerID = c.CustomerID
				    WHERE (? IS NULL OR o.OrderDate >= ?)
				      AND (? IS NULL OR o.OrderDate <= ?)
				    ORDER BY o.OrderDate DESC
				""";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			if (from == null) {
				ps.setNull(1, Types.DATE);
				ps.setNull(2, Types.DATE);
			} else {
				ps.setDate(1, Date.valueOf(from));
				ps.setDate(2, Date.valueOf(from));
			}

			if (to == null) {
				ps.setNull(3, Types.DATE);
				ps.setNull(4, Types.DATE);
			} else {
				ps.setDate(3, Date.valueOf(to));
				ps.setDate(4, Date.valueOf(to));
			}

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				list.add(new Order(rs.getInt("OrderID"), rs.getInt("CustomerID"), rs.getString("FullName"),
						rs.getDate("OrderDate").toLocalDate(), rs.getDouble("TotalAmount")));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public static List<Order> getOrdersByCustomer(String fullName) {
		List<Order> list = new ArrayList<>();
		String sql = "SELECT o.OrderID, o.CustomerID, c.FullName AS CustomerName, o.OrderDate, o.TotalAmount "
				+ "FROM Orders o " + "JOIN Customer c ON o.CustomerID = c.CustomerID " + "WHERE c.FullName = ?";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, fullName);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Date sqlDate = rs.getDate("OrderDate");
				LocalDate orderDate = sqlDate != null ? sqlDate.toLocalDate() : null;

				Order order = new Order(rs.getInt("OrderID"), rs.getInt("CustomerID"), rs.getString("CustomerName"),
						orderDate, rs.getDouble("TotalAmount"));

				list.add(order);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}
}
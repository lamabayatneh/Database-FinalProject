package dao;

import java.sql.*;
import db.DBConnection;

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
}

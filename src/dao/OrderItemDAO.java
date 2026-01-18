package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import db.DBConnection;
import model.CartItem;

public class OrderItemDAO {

	public static void insertItem(int orderId, CartItem item) {

		String sql = """
				    INSERT INTO OrderItem
				    (OrderID, BookID, Quantity, Subtotal)
				    VALUES (?, ?, ?, ?)
				""";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, orderId);
			ps.setInt(2, item.getBook().getBookID());
			ps.setInt(3, item.getQuantity());
			ps.setDouble(4, item.getSubtotal());
			ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
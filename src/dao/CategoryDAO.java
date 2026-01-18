package dao;

import db.DBConnection;
import model.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

	public static List<Category> getAllCategories() {
		List<Category> list = new ArrayList<>();
		String sql = "SELECT * FROM Category ORDER BY CategoryName";

		try (Connection con = DBConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				list.add(new Category(rs.getInt("CategoryID"), rs.getString("CategoryName"),
						rs.getString("Description")));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static List<Category> getAllCategoriesWithBookCount() {
		List<Category> list = new ArrayList<>();

		String sql = """
				    SELECT c.CategoryID, c.CategoryName, c.Description,
				           COUNT(b.BookID) AS BookCount
				    FROM Category c
				    LEFT JOIN Book b ON b.CategoryID = c.CategoryID
				    GROUP BY c.CategoryID, c.CategoryName, c.Description
				    ORDER BY c.CategoryID
				""";

		try (Connection con = DBConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				Category cat = new Category(rs.getInt("CategoryID"), rs.getString("CategoryName"),
						rs.getString("Description"));
				cat.setBookCount(rs.getInt("BookCount"));
				list.add(cat);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public static void insertCategory(Category c) {
		String sql = "INSERT INTO Category (CategoryName, Description) VALUES (?, ?)";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, c.getCategoryName());
			ps.setString(2, c.getDescription());
			ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean deleteCategoryIfEmpty(int categoryId) {

		String checkSql = "SELECT COUNT(*) AS cnt FROM Book WHERE CategoryID = ?";
		String delSql = "DELETE FROM Category WHERE CategoryID = ?";

		try (Connection con = DBConnection.getConnection()) {

			try (PreparedStatement ps = con.prepareStatement(checkSql)) {
				ps.setInt(1, categoryId);
				ResultSet rs = ps.executeQuery();
				if (rs.next() && rs.getInt("cnt") > 0) {
					return false;
				}
			}

			try (PreparedStatement ps = con.prepareStatement(delSql)) {
				ps.setInt(1, categoryId);
				ps.executeUpdate();
			}

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean deleteCategory(int categoryId) {

		if (BookDAO.hasBooksInCategory(categoryId)) {
			return false;
		}

		String sql = "DELETE FROM Category WHERE CategoryID = ?";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, categoryId);
			ps.executeUpdate();
			return true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

}
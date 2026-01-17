package dao;

import db.DBConnection;
import model.Book;
import model.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.time.LocalDate;

public class BookDAO {

	public static List<Book> getAllBooks() {

		List<Book> list = new ArrayList<>();

		String sql = """
				    SELECT b.BookID, b.Title, b.Author, b.Price, b.Quantity,
				           b.AddedDate, b.ImagePath,
				           c.CategoryID, c.CategoryName, c.Description
				    FROM Book b
				    LEFT JOIN Category c ON b.CategoryID = c.CategoryID
				    ORDER BY b.BookID
				""";

		try (Connection con = DBConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {

				Category category = null;
				int catId = rs.getInt("CategoryID");
				if (!rs.wasNull()) {
					category = new Category(catId, rs.getString("CategoryName"), rs.getString("Description"));
				}

				Book book = new Book(rs.getInt("BookID"), rs.getString("Title"), rs.getString("Author"),
						rs.getDouble("Price"), rs.getInt("Quantity"), rs.getDate("AddedDate").toLocalDate(), category,
						rs.getString("ImagePath"));

				list.add(book);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}
	
	// في BookDAO
	public static List<Book> getUnsoldBooks() {
	    List<Book> list = new ArrayList<>();
	    String sql = """
	        SELECT b.BookID, b.Title, b.Author, b.Price, b.Quantity, b.AddedDate, b.ImagePath,
	               c.CategoryID, c.CategoryName, c.Description
	        FROM Book b
	        LEFT JOIN OrderItem oi ON b.BookID = oi.BookID
	        LEFT JOIN Category c ON b.CategoryID = c.CategoryID
	        WHERE oi.BookID IS NULL
	    """;

	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {

	        while (rs.next()) {
	            list.add(mapRow(rs));  // نفس mapRow اللي تستخدمها لكل الكتب
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return list;
	}


	public static List<Book> getBooksSortedByCategory() {
		List<Book> list = new ArrayList<>();
		String sql = "SELECT b.BookID, b.Title, b.Author, b.Quantity, b.Price, b.AddedDate, b.ImagePath, "
				+ "c.CategoryID, c.CategoryName, c.Description " + "FROM Book b "
				+ "JOIN Category c ON b.CategoryID = c.CategoryID " + "ORDER BY c.CategoryName";

		try (Connection con = DBConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				// إنشاء كائن الفئة
				Category cat = new Category(rs.getInt("CategoryID"), rs.getString("CategoryName"),
						rs.getString("Description"));

				// تحويل التاريخ من SQL إلى LocalDate
				Date sqlDate = rs.getDate("AddedDate");
				LocalDate addedDate = sqlDate != null ? ((java.sql.Date) sqlDate).toLocalDate() : null;

				// إنشاء كائن الكتاب
				Book book = new Book(rs.getInt("BookID"), rs.getString("Title"), rs.getString("Author"),
						rs.getDouble("Price"), rs.getInt("Quantity"), addedDate, cat, rs.getString("ImagePath"));

				list.add(book);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}

	public static List<Book> getRecentBooks(int limit) {
		List<Book> list = new ArrayList<>();

		String sql = """
				SELECT *
				FROM Book
				ORDER BY AddedDate DESC
				LIMIT ?
				""";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, limit);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(mapRow(rs));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	/* ================= LATEST BOOKS ================= */
	public static List<Book> getLatestBooks(int limit) {

		List<Book> books = new ArrayList<>();

		String sql = """
				SELECT *
				FROM Book
				ORDER BY AddedDate DESC
				LIMIT ?
				""";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, limit);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				books.add(mapRow(rs));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return books;
	}

	/* ================= SEARCH ================= */
	public static List<Book> searchBooks(String keyword) {

		List<Book> list = new ArrayList<>();

		String sql = """
				SELECT *
				FROM Book
				WHERE Title LIKE ?
				   OR Author LIKE ?
				""";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			String key = "%" + keyword + "%";
			ps.setString(1, key);
			ps.setString(2, key);

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				list.add(mapRow(rs));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/* ================= CATEGORY ================= */
	public static List<Book> getBooksByCategory(String categoryName) {
		List<Book> list = new ArrayList<>();

		String sql = """
				SELECT b.*
				FROM Book b
				JOIN Category c ON b.CategoryID = c.CategoryID
				WHERE c.CategoryName = ?
				""";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, categoryName);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(mapRow(rs));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public static void insertBookWithCategory(Book book, int categoryId) {

		String sql = """
				    INSERT INTO Book
				    (Title, Author, CategoryID, SupplierID, Price, Quantity, WarehouseID, AddedDate)
				    VALUES (?, ?, ?, 1, ?, ?, 1, ?)
				""";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, book.getTitle());
			ps.setString(2, book.getAuthor());
			ps.setInt(3, categoryId);
			ps.setDouble(4, book.getPrice());
			ps.setInt(5, book.getQuantity());
			ps.setDate(6, java.sql.Date.valueOf(book.getAddedDate()));

			ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void deleteBook(int bookID) {

		String sql = "DELETE FROM Book WHERE BookID = ?";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, bookID);
			ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void decreaseQuantity(int bookId, int qty) {

		String sql = """
				    UPDATE Book
				    SET Quantity = Quantity - ?
				    WHERE BookID = ?
				""";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, qty);
			ps.setInt(2, bookId);
			ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int getQuantity(int bookId) {

		String sql = "SELECT Quantity FROM Book WHERE BookID=?";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, bookId);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getInt("Quantity");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	private static Book mapRow(ResultSet rs) throws Exception {

	    int id = rs.getInt("BookID");
	    String title = rs.getString("Title");
	    String author = rs.getString("Author");
	    double price = rs.getDouble("Price");
	    int quantity = rs.getInt("Quantity");
	    

	    LocalDate date = null;
	    if (rs.getDate("AddedDate") != null) {
	        date = rs.getDate("AddedDate").toLocalDate();
	    }

	    String imagePath = null;
	    try {
	        imagePath = rs.getString("ImagePath");
	    } catch (Exception ignored) {}

	    // ✅ Category from JOIN
	    Category category = null;
	    try {
	        int catId = rs.getInt("CategoryID");
	        String catName = rs.getString("CategoryName");
	        String catDesc = rs.getString("Description");
	        category = new Category(catId, catName, catDesc);
	    } catch (Exception ignored) {}

	    // ✅ استخدمي هذا الكونستركتور بالضبط
	    return new Book(
	            id,
	            title,
	            author,
	            price,
	            quantity,
	            date,
	            category,
	            imagePath
	    );
	    

	}



	public static void insertBook(Book book) {

		if (book.getCategory() == null) {
			throw new IllegalArgumentException("Category must be selected");
		}

		String sql = """
				    INSERT INTO Book
				    (Title, Author, CategoryID, Price, Quantity, AddedDate, ImagePath)
				    VALUES (?, ?, ?, ?, ?, ?, ?)
				""";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, book.getTitle());
			ps.setString(2, book.getAuthor());
			ps.setInt(3, book.getCategory().getCategoryID());
			ps.setDouble(4, book.getPrice());
			ps.setInt(5, book.getQuantity());
			ps.setDate(6, java.sql.Date.valueOf(book.getAddedDate()));
			ps.setString(7, book.getImagePath());

			ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void updateBook(Book book) {

		String sql = """
				    UPDATE Book
				    SET Title = ?, Author = ?, CategoryID = ?, Price = ?, Quantity = ?, ImagePath = ?
				    WHERE BookID = ?
				""";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, book.getTitle());
			ps.setString(2, book.getAuthor());
			ps.setInt(3, book.getCategory().getCategoryID());
			ps.setDouble(4, book.getPrice());
			ps.setInt(5, book.getQuantity());
			ps.setString(6, book.getImagePath());
			ps.setInt(7, book.getBookID());

			ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean hasBooksInCategory(int categoryId) {

		String sql = "SELECT COUNT(*) FROM Book WHERE CategoryID = ?";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, categoryId);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getInt(1) > 0;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public static List<Book> getLowStockBooks(int threshold) {
		List<Book> list = new ArrayList<>();
		String sql = """
				    SELECT b.*, c.CategoryName, c.Description
				    FROM Book b
				    LEFT JOIN Category c ON b.CategoryID = c.CategoryID
				    WHERE b.Quantity < ?
				    ORDER BY b.Quantity ASC
				""";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, threshold);
			ResultSet rs = ps.executeQuery();
			while (rs.next())
				list.add(mapRow(rs)); // نفس mapRow اللي عندك
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static void addStock(Connection con, int bookId, int qtyToAdd) throws Exception {
		String sql = "UPDATE Book SET Quantity = Quantity + ? WHERE BookID = ?";
		try (var ps = con.prepareStatement(sql)) {
			ps.setInt(1, qtyToAdd);
			ps.setInt(2, bookId);
			ps.executeUpdate();
		}
	}

}
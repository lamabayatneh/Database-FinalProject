package dao;

import db.DBConnection;
import model.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class BookDAO {

    public static List<Book> getAllBooks() {

        List<Book> books = new ArrayList<>();

        String sql = "SELECT * FROM Book";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                int id = rs.getInt("BookID");
                String title = rs.getString("Title");
                String author = rs.getString("Author");
                double price = rs.getDouble("Price");
                int quantity = rs.getInt("Quantity");
                LocalDate date = rs.getDate("AddedDate").toLocalDate();

                Book book = new Book(id, title, author, price, quantity, date);
                books.add(book);
            }
            
            
            
            

        } catch (Exception e) {
            e.printStackTrace();
        }

        return books;
    }
    
    
    public static void insertBook(Book book) {

        String sql = "INSERT INTO Book (Title, Author, Price, Quantity, AddedDate) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setDouble(3, book.getPrice());
            ps.setInt(4, book.getQuantity());
            ps.setDate(5, java.sql.Date.valueOf(book.getAddedDate()));

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public static void deleteBook(int bookID) {

        String sql = "DELETE FROM Book WHERE BookID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, bookID);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public static void updateBook(Book book) {

        String sql = """
            UPDATE Book
            SET Title = ?, Author = ?, Price = ?, Quantity = ?
            WHERE BookID = ?
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setDouble(3, book.getPrice());
            ps.setInt(4, book.getQuantity());
            ps.setInt(5, book.getBookID());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}

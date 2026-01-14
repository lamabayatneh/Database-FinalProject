package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import db.DBConnection;
import model.User;

public class UserDAO {

	public static User login(String username, String password) {

		String sql = "SELECT * FROM users WHERE username=? AND password=?";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, username);
			ps.setString(2, password);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"),
						rs.getString("role"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static int insertAndReturnId(User user) {

		String sql = "INSERT INTO users(username,password,role) VALUES(?,?,?)";

		try (Connection con = DBConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			ps.setString(1, user.getUsername());
			ps.setString(2, user.getPassword());
			ps.setString(3, user.getRole());
			ps.executeUpdate();

			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next())
				return rs.getInt(1);

		} catch (Exception e) {
			return -1;
		}
		return -1;
	}

}

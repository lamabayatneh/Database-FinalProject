package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import db.DBConnection;
import model.Staff;

public class StaffDAO {
	
	public static List<Staff> getAllStaffSortedByName() {
	    List<Staff> list = new ArrayList<>();
	    String sql = "SELECT FullName, Position, Salary FROM Staff ORDER BY FullName ASC";

	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql);
	        ){

	    	 ResultSet rs = ps.executeQuery();
	        while (rs.next()) {
	            list.add(new Staff(
	                rs.getString("FullName"),
	                rs.getString("Position"),
	                rs.getDouble("Salary")
	            ));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return list;
	}

	

}

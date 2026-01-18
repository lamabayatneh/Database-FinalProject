package dao;

import model.Book;

public class TestBookDAO {
	public static void main(String[] args) {
		for (Book b : BookDAO.getAllBooks()) {
			System.out.println(b.getBookID() + " - " + b.getTitle() + " - " + b.getPrice());
		}
	}
}

package model;

import java.time.LocalDate;

public class Book {

	private int bookID;
	private String title;
	private String author;
	private double price;
	private int quantity;
	private LocalDate addedDate;
	private Category category;
	private String imagePath;

	public Book(int bookID, String title, String author, double price, int quantity, LocalDate addedDate) {
		this(bookID, title, author, price, quantity, addedDate, null);
	}

	public Book(int bookID, String title, String author, double price, int quantity, LocalDate addedDate,
			Category category, String imagePath) {

		this.bookID = bookID;
		this.title = title;
		this.author = author;
		this.price = price;
		this.quantity = quantity;
		this.addedDate = addedDate;
		this.category = category;
		this.imagePath = imagePath;
	}

	public Book(int bookID, String title, String author, double price, int quantity, LocalDate addedDate,
			String imagePath) {
		this.bookID = bookID;
		this.title = title;
		this.author = author;
		this.price = price;
		this.quantity = quantity;
		this.addedDate = addedDate;
		this.imagePath = imagePath;
	}

	public int getBookID() {
		return bookID;
	}

	public void setBookID(int bookID) {
		this.bookID = bookID;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public LocalDate getAddedDate() {
		return addedDate;
	}

	public void setAddedDate(LocalDate addedDate) {
		this.addedDate = addedDate;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	@Override
	public String toString() {
		return title == null ? "" : title;
	}

}
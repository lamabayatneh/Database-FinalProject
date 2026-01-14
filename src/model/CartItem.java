package model;

public class CartItem {

	private Book book;
	private int quantity;

	public CartItem(Book book) {
		this.book = book;
		this.quantity = 1;
	}

	public Book getBook() {
		return book;
	}

	public int getQuantity() {
		return quantity;
	}

	public void increase() {
		quantity++;
	}

	public void decrease() {
		if (quantity > 1)
			quantity--;
	}

	public double getSubtotal() {
		return book.getPrice() * quantity;
	}
}

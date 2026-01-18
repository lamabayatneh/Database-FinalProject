package application;

import model.Cart;
import model.Customer;
import model.User;

public class Session {

	public static User currentUser = null;
	public static Customer currentCustomer = null;

	public static final Cart cart = new Cart();

	public static boolean isLoggedIn() {
		return currentUser != null;
	}

	public static void logout() {
		currentUser = null;
		currentCustomer = null;
		cart.clear();
	}
}
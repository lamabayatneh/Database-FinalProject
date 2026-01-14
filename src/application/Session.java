package application;

import model.Cart;
import model.Customer;
import model.User;

public class Session {
	public static User currentUser;
	public static Customer currentCustomer;
	public static Cart cart = new Cart();
}

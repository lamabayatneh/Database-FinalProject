package application;

import dao.BookDAO;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Book;

public class BookUI {

	public static VBox createBookCard(Book book) {

		Label title = new Label(book.getTitle());
		title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

		Label author = new Label("✍ " + book.getAuthor());
		Label price = new Label("💲 " + book.getPrice());
		Label stock = new Label("📦 In Stock: " + book.getQuantity());

		Button addToCart = new Button("🛒 Add to Cart");
		addToCart.setStyle("""
				        -fx-background-color: #2ecc71;
				        -fx-text-fill: white;
				        -fx-background-radius: 6;
				""");

		if (book.getQuantity() <= 0) {
			addToCart.setDisable(true);
			stock.setText("❌ Out of Stock");
		}

		addToCart.setOnAction(e -> {

			int alreadyInCart = Session.cart.getQuantity(book);

			if (alreadyInCart >= book.getQuantity()) {
				new Alert(Alert.AlertType.WARNING, "❌ No more stock available").show();
				return;
			}

			Session.cart.addBook(book);

			new Alert(Alert.AlertType.INFORMATION, "✅ Added to cart").show();
		});

		VBox card = new VBox(10, title, author, price, addToCart, stock);
		card.setPadding(new Insets(15));
		card.setPrefWidth(200);
		card.setStyle("""
				        -fx-background-color: white;
				        -fx-border-color: #dddddd;
				        -fx-border-radius: 8;
				        -fx-background-radius: 8;
				""");

		return card;
	}

	public static void openAddBookWindow(ObservableList<Book> data) {

		Stage window = new Stage();
		window.setTitle("Add Book");

		TextField title = new TextField();
		TextField author = new TextField();
		TextField price = new TextField();
		TextField qty = new TextField();

		title.setPromptText("Title");
		author.setPromptText("Author");
		price.setPromptText("Price");
		qty.setPromptText("Quantity");

		Button save = new Button("Save");

		save.setOnAction(e -> {
			try {
				Book b = new Book(0, title.getText(), author.getText(), Double.parseDouble(price.getText()),
						Integer.parseInt(qty.getText()), java.time.LocalDate.now());

				BookDAO.insertBook(b);
				data.setAll(BookDAO.getAllBooks());
				window.close();

			} catch (Exception ex) {
				new Alert(Alert.AlertType.ERROR, "Invalid input").show();
			}
		});

		VBox layout = new VBox(10, title, author, price, qty, save);
		layout.setPadding(new Insets(15));

		window.setScene(new Scene(layout, 300, 300));
		window.show();
	}


	public static void openEditBookWindow(Book book, ObservableList<Book> data) {

		Stage window = new Stage();
		window.setTitle("Edit Book");

		TextField title = new TextField(book.getTitle());
		TextField author = new TextField(book.getAuthor());
		TextField price = new TextField(String.valueOf(book.getPrice()));
		TextField qty = new TextField(String.valueOf(book.getQuantity()));

		Button update = new Button("Update");

		update.setOnAction(e -> {
			try {
				book.setTitle(title.getText());
				book.setAuthor(author.getText());
				book.setPrice(Double.parseDouble(price.getText()));
				book.setQuantity(Integer.parseInt(qty.getText()));

				BookDAO.updateBook(book);
				data.setAll(BookDAO.getAllBooks());
				window.close();

			} catch (Exception ex) {
				new Alert(Alert.AlertType.ERROR, "Invalid input").show();
			}
		});

		VBox layout = new VBox(10, title, author, price, qty, update);
		layout.setPadding(new Insets(15));

		window.setScene(new Scene(layout, 300, 300));
		window.show();
	}
}

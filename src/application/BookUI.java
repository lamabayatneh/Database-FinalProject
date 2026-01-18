package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import model.Book;

public class BookUI {

	private static final String DEFAULT_COVER = "/images/default_book.png";

	private static Image safeLoadImage(String resourcePath) {
		try {
			var url = BookUI.class.getResource(resourcePath);
			if (url == null)
				return null;
			return new Image(url.toExternalForm(), true);
		} catch (Exception e) {
			return null;
		}
	}

	private static ImageView buildCover(Book book) {

		Image img = null;

		String path = (book != null) ? book.getImagePath() : null;
		if (path != null) {
			path = path.trim();
			if (!path.isEmpty()) {
				if (!path.startsWith("/"))
					path = "/" + path; 
				img = safeLoadImage(path);
			}
		}

		if (img == null) {
			img = safeLoadImage(DEFAULT_COVER);
		}

		ImageView iv = new ImageView(img);
		iv.setFitWidth(140);
		iv.setFitHeight(190);
		iv.setPreserveRatio(true);
		iv.getStyleClass().add("sb-book-cover");
		return iv;
	}

	public static VBox createBookCard(Book book) {

		ImageView cover = buildCover(book);

		Label title = new Label(book.getTitle());
		title.getStyleClass().add("sb-title");

		Label author = new Label("Author: " + book.getAuthor());
		Label price = new Label("Price: $" + String.format("%.2f", book.getPrice()));
		Label stock = new Label("In Stock: " + book.getQuantity());

		author.getStyleClass().add("sb-muted");
		price.getStyleClass().add("sb-muted");
		stock.getStyleClass().add("sb-muted");

		Button addToCart = new Button("Add to Cart");
		addToCart.getStyleClass().addAll("sb-pill", "sb-primary");

		if (book.getQuantity() <= 0) {
			addToCart.setDisable(true);
			stock.setText("Out of Stock");
		}

		addToCart.setOnAction(e -> {
			if (Session.cart == null) {
				new Alert(Alert.AlertType.ERROR, "Cart is not initialized").show();
				return;
			}
			Session.cart.addBook(book);
			new Alert(Alert.AlertType.INFORMATION, "Added to cart").show();
		});

		VBox card = new VBox(10, cover, title, author, price, stock, addToCart);

		card.setAlignment(Pos.TOP_CENTER);
		card.setPadding(new Insets(15));
		card.getStyleClass().add("sb-card");
		card.setPrefWidth(240);

		return card;
	}
}
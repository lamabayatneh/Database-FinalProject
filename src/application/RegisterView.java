package application;

import dao.CustomerDAO;
import dao.UserDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Customer;
import model.User;

import java.io.InputStream;
import java.time.LocalDate;

public class RegisterView {

	public void show(Stage stage) {

		ImageView logo = loadLogo(100);

		Label appName = new Label("SABASTIA BookShop");
		appName.getStyleClass().add("sb-logo-text");

		Label title = new Label("Create Account");
		title.getStyleClass().add("sb-title");

		Label subtitle = new Label("Create your account to continue.");
		subtitle.getStyleClass().add("sb-muted");

		/* ================= Fields ================= */

		TextField fullName = new TextField();
		fullName.getStyleClass().add("sb-text-field");

		TextField username = new TextField();
		username.getStyleClass().add("sb-text-field");

		TextField email = new TextField();
		email.getStyleClass().add("sb-text-field");

		PasswordField password = new PasswordField();
		password.getStyleClass().add("sb-text-field");

		TextField phone = new TextField();
		phone.getStyleClass().add("sb-text-field");

		TextField address = new TextField();
		address.getStyleClass().add("sb-text-field");

		TextField city = new TextField();
		city.getStyleClass().add("sb-text-field");

		/* ================= Labels (LEFT) ================= */

		Label lFullName = new Label("Full Name *");
		Label lUsername = new Label("Username *");
		Label lEmail = new Label("Email *");
		Label lPassword = new Label("Password *");
		Label lPhone = new Label("Phone *");
		Label lAddress = new Label("Address");
		Label lCity = new Label("City");

		for (Label l : new Label[] { lFullName, lUsername, lEmail, lPassword, lPhone, lAddress, lCity }) {
			l.getStyleClass().add("sb-muted");
			l.setMinWidth(110);
		}

		/* ================= Grid ================= */

		GridPane grid = new GridPane();
		grid.setHgap(14);
		grid.setVgap(12);
		grid.setPadding(new Insets(10));

		ColumnConstraints c1 = new ColumnConstraints();
		c1.setMinWidth(120);
		c1.setHgrow(Priority.NEVER);

		ColumnConstraints c2 = new ColumnConstraints();
		c2.setHgrow(Priority.ALWAYS);

		grid.getColumnConstraints().addAll(c1, c2);

		grid.addRow(0, lFullName, fullName);
		grid.addRow(1, lUsername, username);
		grid.addRow(2, lEmail, email);
		grid.addRow(3, lPassword, password);
		grid.addRow(4, lPhone, phone);
		grid.addRow(5, lAddress, address);
		grid.addRow(6, lCity, city);

		fullName.setMaxWidth(Double.MAX_VALUE);
		username.setMaxWidth(Double.MAX_VALUE);
		email.setMaxWidth(Double.MAX_VALUE);
		password.setMaxWidth(Double.MAX_VALUE);
		phone.setMaxWidth(Double.MAX_VALUE);
		address.setMaxWidth(Double.MAX_VALUE);
		city.setMaxWidth(Double.MAX_VALUE);

		Label status = new Label();
		status.getStyleClass().add("sb-status-error");

		/* ================= Buttons ================= */

		Button registerBtn = new Button("Register");
		registerBtn.getStyleClass().addAll("sb-pill", "sb-primary");

		Button backBtn = new Button("Back");
		backBtn.getStyleClass().addAll("sb-pill", "sb-accent");

		backBtn.setOnAction(e -> {
			try {
				new MainApp().start(stage);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});

		registerBtn.setOnAction(e -> {

			status.setText("");

			String fn = fullName.getText().trim();
			String un = username.getText().trim();
			String em = email.getText().trim();
			String pw = password.getText().trim();
			String ph = phone.getText().trim();

			if (fn.isEmpty() || un.isEmpty() || em.isEmpty() || pw.isEmpty() || ph.isEmpty()) {
				status.setText("Please fill all required fields (*)");
				return;
			}

			if (!em.contains("@") || !em.contains(".")) {
				status.setText("Please enter a valid email");
				return;
			}

			User user = new User(0, un, pw, "CUSTOMER");
			int userId = UserDAO.insertAndReturnId(user);

			if (userId == -1) {
				status.setText("Username already exists");
				return;
			}

			Customer customer = new Customer(0, userId, fn, em, ph, address.getText().trim(), city.getText().trim(),
					LocalDate.now());

			CustomerDAO.insertCustomer(customer);

			new Alert(Alert.AlertType.INFORMATION, "Account created successfully!\nYou can login now.").showAndWait();

			try {
				new MainApp().start(stage);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});

		HBox buttons = new HBox(12, registerBtn, backBtn);
		buttons.setAlignment(Pos.CENTER);

		/* ================= Layout ================= */

		VBox header = new VBox(6, logo, appName, title, subtitle);
		header.setAlignment(Pos.CENTER);

		VBox card = new VBox(16, header, grid, buttons, status);
		card.setPadding(new Insets(24));
		card.setMaxWidth(600);
		card.getStyleClass().add("sb-card");

		BorderPane root = new BorderPane(card);
		root.setPadding(new Insets(20));
		root.getStyleClass().add("sb-page");

		Scene scene = new Scene(root, 720, 760);
		attachCss(scene);

		stage.setTitle("Sabastia BookShop - Create Account");
		stage.setScene(scene);
		stage.show();
	}

	private ImageView loadLogo(double size) {
		InputStream is = getClass().getResourceAsStream("/logo.png");
		ImageView iv = new ImageView();
		if (is != null)
			iv.setImage(new Image(is));
		iv.setPreserveRatio(true);
		iv.setFitHeight(size);
		return iv;
	}

	private void attachCss(Scene scene) {
		try {
			scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
		} catch (Exception ignored) {
		}
	}
}
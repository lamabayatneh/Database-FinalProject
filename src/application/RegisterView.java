package application;

import dao.CustomerDAO;
import dao.UserDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Customer;
import model.User;

import java.time.LocalDate;

public class RegisterView {

	public void show(Stage stage) {

		Label title = new Label("📝 Create New Account");
		title.setStyle("-fx-font-size:22px; -fx-font-weight:bold;");

		TextField fullName = new TextField();
		fullName.setPromptText("Full Name");

		TextField email = new TextField();
		email.setPromptText("Email (Username)");

		PasswordField password = new PasswordField();
		password.setPromptText("Password");

		TextField phone = new TextField();
		phone.setPromptText("Phone");

		TextField address = new TextField();
		address.setPromptText("Address");

		TextField city = new TextField();
		city.setPromptText("City");

		Button registerBtn = new Button("Register");
		registerBtn.setPrefWidth(250);

		Label status = new Label();
		status.setStyle("-fx-text-fill:red;");

		registerBtn.setOnAction(e -> {

			if (fullName.getText().isEmpty() || email.getText().isEmpty() || password.getText().isEmpty()) {

				status.setText("⚠ Please fill required fields");
				return;
			}

			User user = new User(0, email.getText().trim(), password.getText().trim(), "CUSTOMER");

			int userId = UserDAO.insertAndReturnId(user);
			if (userId == -1) {
				status.setText("❌ Email already exists");
				return;
			}

			Customer customer = new Customer(0, userId, fullName.getText(), email.getText(), phone.getText(),
					address.getText(), city.getText(), LocalDate.now());

			CustomerDAO.insertCustomer(customer);

			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setHeaderText("Account Created");
			alert.setContentText("You can login now!");
			alert.showAndWait();

			new MainApp().showLogin(stage);
		});

		VBox card = new VBox(12, title, fullName, email, password, phone, address, city, registerBtn, status);
		card.setPadding(new Insets(30));
		card.setAlignment(Pos.CENTER);
		card.setMaxWidth(350);
		card.setStyle("""
				    -fx-background-color:white;
				    -fx-background-radius:12;
				    -fx-effect:dropshadow(gaussian,#ccc,15,0.3,0,5);
				""");

		BorderPane root = new BorderPane(card);
		BorderPane.setAlignment(card, Pos.CENTER);
		root.setStyle("-fx-background-color: linear-gradient(to bottom, #f5f7fa, #c3cfe2);");

		stage.setScene(new Scene(root, 450, 550));
		stage.setTitle("Register");
	}
}

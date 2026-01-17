package application;

import dao.CustomerDAO;
import dao.UserDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Customer;
import model.User;

import java.io.InputStream;
import java.time.LocalDate;

public class RegisterView {

    public void show(Stage stage) {

        ImageView logo = loadLogo(110);

        Label title = new Label("Create Account");
        title.getStyleClass().add("sb-title");

        Label subtitle = new Label("Create your account to continue.");
        subtitle.getStyleClass().add("sb-subtitle");

        TextField fullName = new TextField();
        fullName.setPromptText("Full Name");
        fullName.getStyleClass().add("sb-field");

        // ✅ NEW: Username (used for login)
        TextField username = new TextField();
        username.setPromptText("Username (used for login)");
        username.getStyleClass().add("sb-field");

        // ✅ NEW: Email (stored in Customer table)
        TextField email = new TextField();
        email.setPromptText("Email");
        email.getStyleClass().add("sb-field");

        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        password.getStyleClass().add("sb-field");

        TextField phone = new TextField();
        phone.setPromptText("Phone");
        phone.getStyleClass().add("sb-field");

        TextField address = new TextField();
        address.setPromptText("Address");
        address.getStyleClass().add("sb-field");

        TextField city = new TextField();
        city.setPromptText("City");
        city.getStyleClass().add("sb-field");

        Label status = new Label();
        status.getStyleClass().add("sb-status-error");

        Button registerBtn = new Button("Register");
        registerBtn.getStyleClass().addAll("sb-pill", "sb-primary");
        registerBtn.setMaxWidth(Double.MAX_VALUE);

        Button backBtn = new Button("Back");
        backBtn.getStyleClass().addAll("sb-pill", "sb-accent");
        backBtn.setMaxWidth(Double.MAX_VALUE);

        backBtn.setOnAction(event -> {
            try {
                new MainApp().start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            event.consume();
        });

        registerBtn.setOnAction(event -> {

            status.setText("");

            String fn = fullName.getText().trim();
            String un = username.getText().trim();
            String em = email.getText().trim();
            String pw = password.getText().trim();

            if (fn.isEmpty() || un.isEmpty() || em.isEmpty() || pw.isEmpty()) {
                status.setText("Please fill required fields");
                event.consume();
                return;
            }

            // (اختياري) تحقق بسيط من شكل الإيميل
            if (!em.contains("@") || !em.contains(".")) {
                status.setText("Please enter a valid email");
                event.consume();
                return;
            }

            // ✅ users.username = Username (مش الإيميل)
            User user = new User(0, un, pw, "CUSTOMER");

            int userId = UserDAO.insertAndReturnId(user);
            if (userId == -1) {
                status.setText("Username already exists");
                event.consume();
                return;
            }

            // ✅ Customer.Email = Email الحقيقي
            Customer customer = new Customer(
                    0,
                    userId,
                    fn,
                    em,
                    phone.getText().trim(),
                    address.getText().trim(),
                    city.getText().trim(),
                    LocalDate.now()
            );

            CustomerDAO.insertCustomer(customer);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Account Created");
            alert.setContentText("You can login now using your username: " + un);
            alert.showAndWait();

            try {
                new MainApp().start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            event.consume();
        });

        HBox buttons = new HBox(10, registerBtn, backBtn);
        buttons.setAlignment(Pos.CENTER);
        registerBtn.setPrefWidth(160);
        backBtn.setPrefWidth(160);

        VBox card = new VBox(
                12,
                logo,
                title,
                subtitle,
                fullName,
                username,   // ✅
                email,      // ✅
                password,
                phone,
                address,
                city,
                buttons,
                status
        );

        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(22));
        card.setMaxWidth(440);
        card.getStyleClass().add("sb-card");

        BorderPane root = new BorderPane(card);
        root.setPadding(new Insets(18));
        root.getStyleClass().add("sb-root");

        Scene scene = new Scene(root, 560, 740);
        attachCss(scene);

        stage.setTitle("Sabastia BookShop - Create Account");
        stage.setScene(scene);
        stage.show();
    }

    private ImageView loadLogo(double size) {
        // ✅ خليها logo.png اللي عندك في resources
        InputStream is = getClass().getResourceAsStream("/logo.png");

        ImageView iv = new ImageView();
        if (is != null) {
            iv.setImage(new Image(is));
        }
        iv.setPreserveRatio(true);
        iv.setFitHeight(size);
        return iv;
    }

    private void attachCss(Scene scene) {
        try {
            String css = getClass().getResource("/style.css").toExternalForm();
            if (!scene.getStylesheets().contains(css)) {
                scene.getStylesheets().add(css);
            }
        } catch (Exception ignored) {}
    }
}
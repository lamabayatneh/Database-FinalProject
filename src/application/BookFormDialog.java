package application;

import dao.CategoryDAO;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import model.Book;
import model.Category;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;

public class BookFormDialog extends Dialog<Book> {

	private final TextField titleField = new TextField();
	private final TextField authorField = new TextField();
	private final TextField priceField = new TextField();
	private final TextField quantityField = new TextField();
	private final DatePicker datePicker = new DatePicker();
	private final ComboBox<Category> categoryBox = new ComboBox<>();
	private final TextField imagePathField = new TextField();

	public BookFormDialog(Book book) {
		setTitle(book == null ? "Add Book" : "Edit Book");

		URL cssUrl = getClass().getResource("/style.css");
		if (cssUrl == null) {
			throw new RuntimeException("style.css NOT FOUND. Put it under src/main/resources/style.css");
		}
		getDialogPane().getStylesheets().add(cssUrl.toExternalForm());

		ButtonType saveBtnType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().addAll(saveBtnType, ButtonType.CANCEL);

		GridPane grid = new GridPane();
		grid.setPadding(new Insets(20));
		grid.setHgap(10);
		grid.setVgap(10);

		getDialogPane().getStyleClass().add("sb-page");
		grid.getStyleClass().add("sb-card");

		categoryBox.getItems().addAll(CategoryDAO.getAllCategories());

		Button browseBtn = new Button("Browse");
		browseBtn.getStyleClass().addAll("sb-pill", "sb-accent");
		browseBtn.setOnAction(e -> {
			FileChooser fc = new FileChooser();
			File file = fc.showOpenDialog(getOwner());
			if (file != null)
				imagePathField.setText(file.getAbsolutePath());
		});

		Label l1 = new Label("Title:");
		Label l2 = new Label("Author:");
		Label l3 = new Label("Price:");
		Label l4 = new Label("Quantity:");
		Label l5 = new Label("Added Date:");
		Label l6 = new Label("Category:");
		Label l7 = new Label("Image Path:");

		l1.getStyleClass().add("sb-muted");
		l2.getStyleClass().add("sb-muted");
		l3.getStyleClass().add("sb-muted");
		l4.getStyleClass().add("sb-muted");
		l5.getStyleClass().add("sb-muted");
		l6.getStyleClass().add("sb-muted");
		l7.getStyleClass().add("sb-muted");

		grid.addRow(0, l1, titleField);
		grid.addRow(1, l2, authorField);
		grid.addRow(2, l3, priceField);
		grid.addRow(3, l4, quantityField);
		grid.addRow(4, l5, datePicker);
		grid.addRow(5, l6, categoryBox);
		grid.addRow(6, l7, imagePathField, browseBtn);

		getDialogPane().setContent(grid);

		Node saveNode = getDialogPane().lookupButton(saveBtnType);
		if (saveNode instanceof Button saveBtn) {
			saveBtn.getStyleClass().addAll("sb-pill", "sb-primary");
		}

		Node cancelNode = getDialogPane().lookupButton(ButtonType.CANCEL);
		if (cancelNode instanceof Button cancelBtn) {
			cancelBtn.getStyleClass().addAll("sb-pill", "sb-accent");
		}

		Alert dbg = new Alert(Alert.AlertType.INFORMATION);
		dbg.setHeaderText("CSS Debug");
		String saveClasses = (saveNode != null) ? saveNode.getStyleClass().toString() : "null";
		dbg.setContentText("cssUrl = " + cssUrl + "\n" + "dialog stylesheets = "
				+ getDialogPane().getStylesheets().size() + "\n" + "save button classes = " + saveClasses);
		dbg.showAndWait();

		if (book != null) {
			titleField.setText(book.getTitle());
			authorField.setText(book.getAuthor());
			priceField.setText(String.valueOf(book.getPrice()));
			quantityField.setText(String.valueOf(book.getQuantity()));
			datePicker.setValue(book.getAddedDate());
			categoryBox.setValue(book.getCategory());
			imagePathField.setText(book.getImagePath());

		} else {
			datePicker.setValue(LocalDate.now());
		}

		setResultConverter(btn -> {
			if (btn == saveBtnType) {
				return new Book(0, titleField.getText(), authorField.getText(),
						Double.parseDouble(priceField.getText()), Integer.parseInt(quantityField.getText()),
						datePicker.getValue(), categoryBox.getValue(), imagePathField.getText());
			}
			return null;
		});
	}
}
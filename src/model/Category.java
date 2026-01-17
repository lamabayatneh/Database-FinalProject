package model;

public class Category {

    private int categoryID;
    private String categoryName;
    private String description;

    private int bookCount;



    public Category(int categoryID, String categoryName, String description) {
        this(categoryID, categoryName, description, 0);
    }

    public Category(int categoryID, String categoryName, String description, int bookCount) {
        this.categoryID = categoryID;
        this.categoryName = categoryName;
        this.description = description;
        this.bookCount = bookCount;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getDescription() {
        return description;
    }

    public int getBookCount() { return bookCount; }
    public void setBookCount(int bookCount) { this.bookCount = bookCount; }

    @Override
    public String toString() {
        return categoryName;
    }
}
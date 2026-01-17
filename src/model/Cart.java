package model;

import java.util.ArrayList;
import java.util.List;

public class Cart {

    private List<CartItem> items = new ArrayList<>();

    public void addBook(Book book) {
        for (CartItem item : items) {
            if (item.getBook().getBookID() == book.getBookID()) {
                item.increase();
                return;
            }
        }
        items.add(new CartItem(book));
    }

    public void removeItem(CartItem item) {
        items.remove(item);
    }

    public List<CartItem> getItems() {
        return items;
    }

    public double getTotal() {
        return items.stream().mapToDouble(CartItem::getSubtotal).sum();
    }

    public void clear() {
        items.clear();
    }

    public int getQuantity(Book book) {
        return items.stream().filter(i -> i.getBook().getBookID() == book.getBookID()).mapToInt(CartItem::getQuantity)
                .sum();
    }

}
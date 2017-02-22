package minor.dokterstas;

import java.util.ArrayList;
import java.util.List;


class Category {
    private int ID;
    private String name;
    private List<Item> items;

    Category(int ID, String name) {
        this.ID = ID;
        this.name = name;
        items = new ArrayList<>();
    }

    int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    void addItem(Item item) {
        items.add(item);
    }

    List<Item> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return name;
    }
}

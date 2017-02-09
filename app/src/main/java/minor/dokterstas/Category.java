package minor.dokterstas;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rober on 8-2-2017.
 */

public class Category {
    private int ID;
    private String name;
    private List<Item> items;

    public Category(int ID, String name)
    {
        this.ID = ID;
        this.name = name;
        items = new ArrayList<>();
    }

    public int getID()
    {
        return ID;
    }

    public String getName()
    {
        return name;
    }

    public void addItem(Item item)
    {
        items.add(item);
    }

    public List<Item> getItems()
    {
        return items;
    }
}
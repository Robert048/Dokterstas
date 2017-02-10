package minor.dokterstas;

/**
 * Created by rober on 8-2-2017.
 */

public class Item {
    private int ID;
    private String name;
    private String tht;
    private int voorraad;

    public Item(int ID, String name, String tht, int voorraad) {
        this.ID = ID;
        this.name = name;
        this.tht = tht;
        this.voorraad = voorraad;
    }

    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public String getTht()
    {
        return tht;
    }

    public int getVoorraad() {
        return voorraad;
    }
}

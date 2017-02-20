package minor.dokterstas;

public class Item {
    private int ID;
    private String name;
    private String tht;
    private int voorraad;
    private int type;

    public Item(int ID, String name, String tht, int voorraad, int type) {
        this.ID = ID;
        this.name = name;
        this.tht = tht;
        this.voorraad = voorraad;
        this.type = type;
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

    public int getType() {
        return type;
    }

    public String toString()
    {
        return name;
    }

}

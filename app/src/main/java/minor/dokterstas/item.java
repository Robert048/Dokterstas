package minor.dokterstas;

public class Item {
    private int ID;
    private String name;
    private String tht;
    private int voorraad;
    private int type; // 0=geen Voorraad en geen datum, 1=Voorraad, 2=houdbaarheidsdatum, 3=Voorraad en houdbaarheidsdatum

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

    public String getData()
    {
        String returnString = "";
        if(type == 0) returnString = "";
        else if(type == 1) returnString = voorraad + " op voorraad";
        else if(type == 2) returnString = tht;
        else if(type == 3) returnString = tht + "\n" + voorraad + " op voorraad";

        return returnString;
    }
}

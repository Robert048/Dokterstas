package minor.dokterstas;

class Item {
    private int ID;
    private String name;
    private String tht;
    private int voorraad;
    private int volume;
    private int type;
    /*
    0 = geen Voorraad, geen datum en geen volume,
    1 = Voorraad,
    2 = houdbaarheidsdatum,
    3 = Voorraad en houdbaarheidsdatum,
    4 = Volume,
    5 = Volume en voorraad,
    6 = volume en Houdbaarheidsdatum,
    7 = Volume, Voorraad en houdbaarheidsdatum
    */

    Item(int ID, String name, String tht, int voorraad, int volume, int type) {
        this.ID = ID;
        this.name = name;
        this.tht = tht;
        this.voorraad = voorraad;
        this.type = type;
        this.volume = volume;
    }

    int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    String getTht() {
        return tht;
    }

    int getVoorraad() {
        return voorraad;
    }

    int getType() {
        return type;
    }

    public String toString() {
        return name;
    }

    public String getData() {
        String returnString = "";
        if (type == 0) returnString = "";
        else if (type == 1) returnString = voorraad + " op voorraad";
        else if (type == 2) returnString = tht;
        else if (type == 3) returnString = tht + "\n" + voorraad + " op voorraad";
        else if (type == 4) returnString = volume + "mL";
        else if (type == 5) returnString = voorraad + " op voorraad" + "\n" + volume + "mL";
        else if (type == 6) returnString = tht + "\n" + volume + "mL";
        else if (type == 7) returnString = tht + "\n" + voorraad + " op voorraad" + "\n" + volume + "mL";

        return returnString;
    }
}

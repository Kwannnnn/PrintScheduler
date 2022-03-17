package models;

public class Printer {
    private int id;
    private String name;
    private String manufacturer;
    private int maxX;
    private int maxY;
    private int maxZ;
    private int maxColors = -1;
    private int currentSpool;
    private int spool2 = -1;
    private int spool3 = -1;
    private int spool4 = -1;
    private String currentPrint = "";

    public Printer(int id, String name, String manufacturer, int maxX, int maxY, int maxZ, int currentSpool){
        this.id = id;
        this.name = name;
        this.manufacturer = manufacturer;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.currentSpool = currentSpool;
    }

    public void setCurrentPrint(String print) {
        this.currentPrint = print;
    }

    public boolean printing() {
        return currentPrint.length() > 0;
    }

    public String activePrintString() {
        return id + ": " + name + " - " + currentPrint;
    }

    public Printer(int id, String name, String manufacturer, int maxX, int maxY, int maxZ, int currentSpool, int maxColors, int spool2, int spool3, int spool4){
        this(id, name, manufacturer, maxX, maxY, maxZ, currentSpool);
        this.maxColors = maxColors;
        this.spool2 = spool2;
        this.spool3 = spool3;
        this.spool4 = spool4;
    }

    public void setCurrentSpool(int spool) {
        currentSpool = spool;
    }
    public void setCurrentSpool(int spool, int spool2, int spool3, int spool4) {
        currentSpool = spool;
        this.spool2 = spool2;
        this.spool3 = spool3;
        this.spool4 = spool4;
    }

    @Override
    public String toString() {
        String toPrint =
                "ID: " + id + System.lineSeparator() +
                "Name: " + name + System.lineSeparator() +
                "Manufacturer: " + manufacturer + System.lineSeparator() +
                "maxX: " + maxX + System.lineSeparator() +
                "maxY: " + maxY + System.lineSeparator() +
                "maxZ: " + maxZ + System.lineSeparator() +
                "Current spool: " + currentSpool + System.lineSeparator();

        if(maxColors > 0) {
            toPrint += "maxColors: " + maxColors + System.lineSeparator() +
                    "spool2: " + spool2 + System.lineSeparator() +
                    "spool3: " + spool3 + System.lineSeparator() +
                    "spool4: " + spool4 + System.lineSeparator();
        }
        if(currentPrint.length()> 0) {
            toPrint += "Current Print Task: " + currentPrint + System.lineSeparator();
        }
        toPrint += "" + System.lineSeparator();
        return toPrint;
    }
}

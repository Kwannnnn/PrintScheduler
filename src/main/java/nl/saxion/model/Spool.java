package nl.saxion.model;

public class Spool {
    private final int id;
    private final String color;
    private final Filament filamentType;
    private double length;

    public Spool(int id, String color, Filament filamentType, double length) {
        this.id = id;
        this.color = color;
        this.filamentType = filamentType;
        this.length = length;
    }

    public int getId() {
        return this.id;
    }

    public double getLength() {
        return length;
    }

    public boolean spoolMatch(String color, Filament type) {
        if(color.equals(this.color) && type.toString().equals(this.getFilamentType().toString())) {
            return true;
        }
        return false;
    }
    /**
     * This method will try to reduce the length of the spool.
     *
     * @param byLength
     * @return boolean which tells you if it is possible or not.
     */
    public boolean reduceLength(double byLength) {
        boolean success = true;
        this.length -= byLength;
        if (this.length < 0) {
            this.length -= byLength;
            success = false;
        }
        return success;
    }

    public String getColor() {
        return color;
    }

    public Filament getFilamentType(){
        return filamentType;
    }

    @Override
    public String toString() {
        return "===== Spool " + id + " =====" + System.lineSeparator() +
                "color: " + color + System.lineSeparator() +
                "filamentType: " + filamentType + System.lineSeparator() +
                "length: " + length + System.lineSeparator();
    }
}

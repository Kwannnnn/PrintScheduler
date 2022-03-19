package nl.saxion.refactor.model;

public class Spool {
    private final int id;
    private final String color;
    private final FilamentType filamentType;
    private double length;

    public Spool(int id, String color, FilamentType filamentType, double length) {
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

    public boolean spoolMatch(String color, FilamentType type) {
        return color.equals(this.color) && type == this.filamentType;
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

    public FilamentType getFilamentType(){
        return this.filamentType;
    }

    @Override
    public String toString() {
        return "===== Spool " + this.id + " =====" + System.lineSeparator() +
                "color: " + this.color + System.lineSeparator() +
                "filamentType: " + this.filamentType + System.lineSeparator() +
                "length: " + this.length + System.lineSeparator();
    }
}

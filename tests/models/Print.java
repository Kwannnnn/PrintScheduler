package models;

import java.util.ArrayList;

public class Print {
    private String name;
    private String filename;
    private int height;
    private int width;
    private int length;
    private ArrayList<Integer> filamentLength =  new ArrayList<>();

    public String getName() {
        return name;
    }

    public Print(String name, String filename, int height, int width, int length, int filamentLength) {
        this.name = name;
        this.filename = filename;
        this.height = height;
        this.width = width;
        this.length = length;
        this.filamentLength.add(filamentLength);
    }

    public Print(String name, String filename, int height, int width, int length, int filamentLength, int length2) {
        this.name = name;
        this.filename = filename;
        this.height = height;
        this.width = width;
        this.length = length;
        this.filamentLength.add(filamentLength);
        this.filamentLength.add(length2);
    }

    @Override
    public String toString() {
        String print = "===== "+name+" =====" + System.lineSeparator() +
                "Filename: " +filename + System.lineSeparator() +
                "Height: " + height + System.lineSeparator() +
                "Width: " + width + System.lineSeparator() +
                "Length: " + length + System.lineSeparator() +
                "FilamentLength: " + filamentLength;
        return print;
    }
}

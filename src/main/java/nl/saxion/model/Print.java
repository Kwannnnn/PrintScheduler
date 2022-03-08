package nl.saxion.model;

import java.util.ArrayList;

public class Print {
    private String name;
    private String filename;
    private int height;
    private int width;
    private int length;
    private ArrayList<Integer> filamentLength;

    public Print(String name, String filename, int height, int width, int length, ArrayList<Integer> filamentLength) {
        this.name = name;
        this.filename = filename;
        this.height = height;
        this.width = width;
        this.length = length;
        this.filamentLength = filamentLength;
    }

    @Override
    public String toString() {
        return "===== " + name + " =====" + System.lineSeparator() +
                "Filename: " + filename + System.lineSeparator() +
                "Height: " + height + System.lineSeparator() +
                "Width: " + width + System.lineSeparator() +
                "Length: " + length + System.lineSeparator() +
                "FilamentLength: " + filamentLength;
    }

    public String getName() {
        return name;
    }

    public double getLength() {
        return length;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public ArrayList<Integer> getFilamentLength() {
        return filamentLength;
    }
}

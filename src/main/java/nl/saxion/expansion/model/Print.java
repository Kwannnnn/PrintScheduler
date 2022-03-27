package nl.saxion.expansion.model;

import java.util.List;

public class Print {
    private final String name;
    private final String filename;
    private final int height;
    private final int width;
    private final int length;
    private final List<Integer> filamentLength;

    public Print(String name, String filename, int height, int width, int length, List<Integer> filamentLength) {
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

    public List<Integer> getFilamentLength() {
        return filamentLength;
    }
}

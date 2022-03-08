package nl.saxion.io;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

public interface FileLoader<T> {
    ArrayList<T> loadFile() throws IOException, ParseException;
}

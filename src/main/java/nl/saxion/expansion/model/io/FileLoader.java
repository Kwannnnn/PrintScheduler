package nl.saxion.expansion.model.io;

import org.json.simple.parser.ParseException;

import java.io.IOException;

public interface FileLoader<T> {
    void loadFile() throws IOException, ParseException;
}

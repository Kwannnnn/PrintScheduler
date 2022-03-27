package nl.saxion.refactor.model.io;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.List;

public interface FileLoader<T> {
    List<T> loadFile() throws IOException, ParseException;
}

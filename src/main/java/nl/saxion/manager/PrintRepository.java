package nl.saxion.manager;

import nl.saxion.io.PrintJsonLoader;
import nl.saxion.model.Print;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

public class PrintRepository {
    private final ArrayList<Print> prints;

    public PrintRepository(String fileName) throws IOException, ParseException {
        this.prints = loadPrintsFromFile(fileName);
    }

    private ArrayList<Print> loadPrintsFromFile(String fileName) throws IOException, ParseException {
        return new PrintJsonLoader(fileName).loadFile();
    }

    public ArrayList<Print> getPrints() {
        return this.prints;
    }
}

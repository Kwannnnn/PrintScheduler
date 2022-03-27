package nl.saxion.expansion.model.io;

import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

public abstract class AbstractCsvLoader implements FileLoader {
    private final String filename;

    public AbstractCsvLoader(String filename) {
        this.filename = filename;
    }

    @Override
    public void loadFile() throws IOException, ParseException {
        var reader = new BufferedReader(new FileReader(getResourceFile()));

        String currentLine = reader.readLine(); // Skip header line
        while ((currentLine = reader.readLine()) != null) {
            var args = currentLine.split(",");
            parseObject(args);
        }
    }

    protected abstract void parseObject(String[] args);

    private String getResourceFile() throws FileNotFoundException {
        URL printResource = getClass().getResource("/" + this.filename);
        if (printResource == null) {
            throw new FileNotFoundException("Warning: Could not find " + this.filename + " file!");
        }

        return printResource.getFile();
    }
}

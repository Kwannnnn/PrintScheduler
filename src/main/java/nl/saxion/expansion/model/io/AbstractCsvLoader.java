package nl.saxion.expansion.model.io;

import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCsvLoader<T> implements FileLoader<T> {
    private final String filename;
    private final String delimiter;

    public AbstractCsvLoader(String filename, String delimiter) {
        this.filename = filename;
        this.delimiter = delimiter;
    }

    @Override
    public List<T> loadFile() throws IOException, ParseException {
        List<T> result = new ArrayList<>();
        var reader = new BufferedReader(new FileReader(getResourceFile()));

        String currentLine = reader.readLine(); // Skip header line
        while ((currentLine = reader.readLine()) != null) {
            var args = currentLine.split(this.delimiter);
            result.add(parseObject(args));
        }

        return result;
    }

    protected abstract T parseObject(String[] args);

    private String getResourceFile() throws FileNotFoundException {
        URL printResource = getClass().getResource("/" + this.filename);
        if (printResource == null) {
            throw new FileNotFoundException("Warning: Could not find " + this.filename + " file!");
        }

        return printResource.getFile();
    }
}

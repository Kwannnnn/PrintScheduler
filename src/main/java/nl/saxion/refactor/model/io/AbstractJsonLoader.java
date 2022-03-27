package nl.saxion.refactor.model.io;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractJsonLoader<T> implements FileLoader<T> {
    private final String filename;

    public AbstractJsonLoader(String filename) {
        this.filename = filename;
    }

    @Override
    public List<T> loadFile() throws IOException, ParseException {
        var jsonParser = new JSONParser();
        var reader = new FileReader(getResourceFile());
        var jsonData = (JSONArray) jsonParser.parse(reader);
        List<T> result = new ArrayList<>();
        for (var line : jsonData) {
            result.add(parseObject((JSONObject) line));
        }

        return result;
    }

    protected abstract T parseObject(JSONObject o);

    private String getResourceFile() throws FileNotFoundException {
        URL printResource = getClass().getResource("/" + this.filename);
        if (printResource == null) {
            throw new FileNotFoundException("Warning: Could not find " + this.filename + " file!");
        }

        return printResource.getFile();
    }
}

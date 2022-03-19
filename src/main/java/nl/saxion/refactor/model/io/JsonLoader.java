package nl.saxion.refactor.model.io;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

public abstract class JsonLoader<T> implements FileLoader<T> {
    private final String filename;

    public JsonLoader(String filename) {
        this.filename = filename;
    }

    @Override
    public void loadFile() throws IOException, ParseException {
        var jsonParser = new JSONParser();
        var reader = new FileReader(getResourceFile());
        var jsonData = (JSONArray) jsonParser.parse(reader);
        for (var line : jsonData) {
            parseObject((JSONObject) line);
        }
    }

    protected abstract void parseObject(JSONObject o);

    private String getResourceFile() throws FileNotFoundException {
        URL printResource = getClass().getResource("/" + this.filename);
        if (printResource == null) {
            throw new FileNotFoundException("Warning: Could not find " + this.filename + " file!");
        }

        return printResource.getFile();
    }
}

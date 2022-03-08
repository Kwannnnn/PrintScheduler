package nl.saxion.io;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public abstract class JsonLoader<T> implements FileLoader<T> {
    private final String filename;

    public JsonLoader(String filename) {
        this.filename = filename;
    }

    @Override
    public ArrayList<T> loadFile() throws IOException, ParseException {
        ArrayList<T> result = new ArrayList<>();
        var jsonParser = new JSONParser();
        var reader = new FileReader(getResourceFile());
        var jsonData = (JSONArray) jsonParser.parse(reader);
        for (var line : jsonData) {
            var object = parseObject((JSONObject) line);
            result.add(object);
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

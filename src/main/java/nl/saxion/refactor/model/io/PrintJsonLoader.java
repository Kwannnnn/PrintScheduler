package nl.saxion.refactor.model.io;

import nl.saxion.refactor.model.io.record.PrintFileRecord;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class PrintJsonLoader extends AbstractJsonLoader<PrintFileRecord> {

    public PrintJsonLoader(String filename) {
        super(filename);
    }

    @Override
    protected PrintFileRecord parseObject(JSONObject o) {
        String name = (String) o.get("name");
        String filename = (String) o.get("filename");
        int height = ((Long) o.get("height")).intValue();
        int width = ((Long) o.get("width")).intValue();
        int length = ((Long) o.get("length")).intValue();
        JSONArray fLength = (JSONArray) o.get("filamentLength");

        return new PrintFileRecord(name, filename, height, width, length, fLength);
    }
}

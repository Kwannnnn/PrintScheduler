package nl.saxion.expansion.model.io;

import nl.saxion.expansion.model.io.record.PrinterFileRecord;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class PrinterJsonLoader extends AbstractJsonLoader<PrinterFileRecord> {

    public PrinterJsonLoader(String filename) {
        super(filename);
    }

    @Override
    protected PrinterFileRecord parseObject(JSONObject o) {
        int id = ((Long) o.get("id")).intValue();
        int type = ((Long) o.get("type")).intValue();
        String name = (String) o.get("name");
        String manufacturer = (String) o.get("manufacturer");
        int maxX = ((Long) o.get("maxX")).intValue();
        int maxY = ((Long) o.get("maxY")).intValue();
        int maxZ = ((Long) o.get("maxZ")).intValue();
        int maxColors = ((Long) o.get("maxColors")).intValue();
        JSONArray currentSpools = (JSONArray) o.get("currentSpools");

        return new PrinterFileRecord(id, type, name, manufacturer, maxX, maxY, maxZ, maxColors, currentSpools);
    }
}

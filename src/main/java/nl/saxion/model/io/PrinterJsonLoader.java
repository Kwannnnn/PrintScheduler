package nl.saxion.model.io;

import nl.saxion.model.Printer;
import nl.saxion.model.manager.PrinterManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class PrinterJsonLoader extends JsonLoader<Printer> {
    private final PrinterManager printerManager;

    public PrinterJsonLoader(String filename, PrinterManager printerManager) {
        super(filename);
        this.printerManager = printerManager;
    }

    @Override
    protected void parseObject(JSONObject o) {
        int id = ((Long) o.get("id")).intValue();
        int type = ((Long) o.get("type")).intValue();
        String name = (String) o.get("name");
        String manufacturer = (String) o.get("manufacturer");
        int maxX = ((Long) o.get("maxX")).intValue();
        int maxY = ((Long) o.get("maxY")).intValue();
        int maxZ = ((Long) o.get("maxZ")).intValue();
        int maxColors = ((Long) o.get("maxColors")).intValue();
        JSONArray currentSpools = (JSONArray) o.get("currentSpools");

        this.printerManager.addPrinter(id, type, name, manufacturer, maxX, maxY, maxZ, maxColors, currentSpools);
    }
}

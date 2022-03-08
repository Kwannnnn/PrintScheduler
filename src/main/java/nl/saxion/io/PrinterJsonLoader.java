package nl.saxion.io;

import nl.saxion.model.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class PrinterJsonLoader extends JsonLoader<Printer> {
    private final PrinterFactory printerFactory;

    public PrinterJsonLoader(String filename, PrinterFactory printerFactory) {
        super(filename);
        this.printerFactory = printerFactory;
    }

    @Override
    protected Printer parseObject(JSONObject o) {
        int id = ((Long) o.get("id")).intValue();
        int type = ((Long) o.get("type")).intValue();
        String name = (String) o.get("name");
        String manufacturer = (String) o.get("manufacturer");
        int maxX = ((Long) o.get("maxX")).intValue();
        int maxY = ((Long) o.get("maxY")).intValue();
        int maxZ = ((Long) o.get("maxZ")).intValue();
        int maxColors = ((Long) o.get("maxColors")).intValue();
        JSONArray currentSpools = (JSONArray) o.get("currentSpools");

        return this.printerFactory.createPrinter(id, type, name, manufacturer, maxX, maxY, maxZ, maxColors, currentSpools);

//        return switch (type) {
//            case 1 -> new StandardFDM(id, name, manufacturer, maxX, maxY, maxZ);
//            case 2 -> new HousedPrinter(id, name, manufacturer, maxX, maxY, maxZ);
//            case 3 -> new MultiColor(id, name, manufacturer, maxX, maxY, maxZ, maxColors);
//            default -> throw new IllegalArgumentException("Invalid printer type! Printer with id" + id + " not loaded.");
//        };
    }
}

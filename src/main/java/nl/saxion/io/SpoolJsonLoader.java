package nl.saxion.io;

import nl.saxion.model.*;
import org.json.simple.JSONObject;

public class SpoolJsonLoader extends JsonLoader<Spool> {
    public SpoolJsonLoader(String filename) {
        super(filename);
    }

    @Override
    protected Spool parseObject(JSONObject o) {
        int id = ((Long) o.get("id")).intValue();
        String color = (String) o.get("color");
        String filamentType = (String) o.get("filamentType");
        double length = (Double) o.get("length");
        Filament type = switch (filamentType) {
            case "PLA" -> new PLAFilament();
            case "PETG" -> new PETGFilament();
            case "ABS" -> new ABSFilament();
            default -> throw new IllegalArgumentException("Not a valid filamentType! Spool with id " + id + " not loaded.");
        };

        return new Spool(id, color, type, length);
    }
}

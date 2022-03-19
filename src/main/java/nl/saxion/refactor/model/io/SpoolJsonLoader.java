package nl.saxion.refactor.model.io;

import nl.saxion.refactor.model.FilamentType;
import nl.saxion.refactor.model.Spool;
import nl.saxion.refactor.model.manager.SpoolManager;
import org.json.simple.JSONObject;

public class SpoolJsonLoader extends JsonLoader<Spool> {
    SpoolManager spoolManager;
    public SpoolJsonLoader(String filename, SpoolManager spoolManager) {
        super(filename);
        this.spoolManager = spoolManager;
    }

    @Override
    protected void parseObject(JSONObject o) {
        int id = ((Long) o.get("id")).intValue();
        String color = (String) o.get("color");
        String filamentType = (String) o.get("filamentType");
        double length = (Double) o.get("length");
        FilamentType type = switch (filamentType) {
            case "PLA" -> FilamentType.PLA;
            case "PETG" -> FilamentType.PETG;
            case "ABS" -> FilamentType.ABS;
            default -> throw new IllegalArgumentException("Not a valid filamentType! Spool with id " + id + " not loaded.");
        };

        this.spoolManager.addSpool(id, color, type, length);
    }
}

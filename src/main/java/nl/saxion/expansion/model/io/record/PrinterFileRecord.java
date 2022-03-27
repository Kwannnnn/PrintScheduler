package nl.saxion.expansion.model.io.record;

import org.json.simple.JSONArray;

public record PrinterFileRecord(int id,
                                int type,
                                String printerName,
                                String manufacturer,
                                int maxX,
                                int maxY,
                                int maxZ,
                                int maxColors,
                                JSONArray currentSpools) implements FileRecord {
}

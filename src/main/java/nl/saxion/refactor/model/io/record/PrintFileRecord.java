package nl.saxion.refactor.model.io.record;

import org.json.simple.JSONArray;

public record PrintFileRecord(String name,
                              String filename,
                              int height,
                              int width,
                              int length,
                              JSONArray filamentLength) implements FileRecord {
}

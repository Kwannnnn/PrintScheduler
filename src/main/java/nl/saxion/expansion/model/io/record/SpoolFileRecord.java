package nl.saxion.expansion.model.io.record;

public record SpoolFileRecord(int id,
                             String color,
                             String filamentName,
                             double length) implements FileRecord {
}

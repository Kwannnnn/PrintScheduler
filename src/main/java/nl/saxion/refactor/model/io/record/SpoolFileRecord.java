package nl.saxion.refactor.model.io.record;

public record SpoolFileRecord(int id,
                              String color,
                              String filamentName,
                              double length) implements FileRecord {
}

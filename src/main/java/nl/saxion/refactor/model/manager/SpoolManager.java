package nl.saxion.refactor.model.manager;

import nl.saxion.refactor.model.FilamentType;
import nl.saxion.refactor.model.Spool;
import nl.saxion.refactor.model.io.FileLoader;
import nl.saxion.refactor.model.io.record.SpoolFileRecord;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SpoolManager {
    private final List<Spool> spools;
    private final List<Spool> freeSpools;

    public SpoolManager(FileLoader<SpoolFileRecord> fileLoader)
            throws IOException, ParseException {
        this.freeSpools = new ArrayList<>();
        this.spools = new ArrayList<>();

        this.loadSpoolsFromRecords(fileLoader.loadFile());
    }

    public void addSpool(int id, String color, FilamentType filamentType, double length) {
        Spool spool = new Spool(id, color, filamentType, length);
        this.spools.add(spool);
        this.freeSpools.add(spool);
    }

    public List<Spool> getSpools() {
        return this.spools;
    }

    public List<Spool> getFreeSpools() {
        return this.freeSpools;
    }

    public Spool getSpoolByID(int id) {
        for(Spool s : this.spools) {
            if(s.getId() == id) {
                return s;
            }
        }
        return null;
    }

    private void loadSpoolsFromRecords(List<SpoolFileRecord> records) {
        for (SpoolFileRecord record : records) {
            FilamentType type = switch (record.filamentName()) {
                case "PLA" -> FilamentType.PLA;
                case "PETG" -> FilamentType.PETG;
                case "ABS" -> FilamentType.ABS;
                default -> throw new IllegalArgumentException("Not a valid filamentType! Spool with id " + record.id() + " not loaded.");
            };

            this.addSpool(record.id(), record.color(), type, record.length());
        }
    }
}

package nl.saxion.expansion.model.manager;

import nl.saxion.expansion.model.FilamentType;
import nl.saxion.expansion.model.Spool;
import nl.saxion.expansion.model.io.FileLoader;
import nl.saxion.expansion.model.io.record.SpoolFileRecord;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SpoolManager {
    private final List<Spool> spools;
    private final List<Spool> freeSpools; // TODO: Decide if this should be used at all.

    public SpoolManager(FileLoader<SpoolFileRecord> fileLoader) throws IOException, ParseException {
        this.freeSpools = new ArrayList<>();
        this.spools = new ArrayList<>();
        this.loadSpools(fileLoader);
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

    private void loadSpools(FileLoader<SpoolFileRecord> fileLoader) throws IOException, ParseException {
        List<SpoolFileRecord> spoolRecords = fileLoader.loadFile();

        for (SpoolFileRecord record : spoolRecords) {
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

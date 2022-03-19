package nl.saxion.model.newModel;

import nl.saxion.io.SpoolJsonLoader;
import nl.saxion.model.FilamentType;
import nl.saxion.model.Spool;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SpoolManager {
    private final List<Spool> spools;
    private final List<Spool> freeSpools; // TODO: Decide if this should be used at all.

    public SpoolManager(String filename) throws IOException, ParseException {
        this.freeSpools = new ArrayList<>();
        this.spools = new ArrayList<>();


            var SpoolJsonLoader = new SpoolJsonLoader(filename, this);
            SpoolJsonLoader.loadFile();

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
}

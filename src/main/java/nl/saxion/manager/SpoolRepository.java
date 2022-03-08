package nl.saxion.manager;

import nl.saxion.io.SpoolJsonLoader;
import nl.saxion.model.Spool;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

public class SpoolRepository {
    private final ArrayList<Spool> spools;
    private final ArrayList<Spool> freeSpools; // TODO: Decide if this should be used at all.

    public SpoolRepository(String fileName) throws IOException, ParseException {
        this.spools = loadSpoolsFromFile(fileName);
        this.freeSpools = new ArrayList<>(this.spools);
    }

    private ArrayList<Spool> loadSpoolsFromFile(String fileName) throws IOException, ParseException {
        return new SpoolJsonLoader(fileName).loadFile();
    }

    public ArrayList<Spool> getSpools() {
        return this.spools;
    }

    public Spool getSpoolByID(int id) {
        for(var s: this.spools) {
            if(s.getId() == id) {
                return s;
            }
        }
        return null;
    }

    public void addFreeSpool(Spool spool) {
        this.freeSpools.add(spool);
    }

    public ArrayList<Spool> getFreeSpools() {
        return freeSpools;
    }

    public void removeFreeSpool(Spool spool) {
        this.freeSpools.remove(spool);
    }
}

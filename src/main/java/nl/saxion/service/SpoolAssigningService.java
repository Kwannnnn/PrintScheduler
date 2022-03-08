package nl.saxion.service;

import nl.saxion.manager.SpoolRepository;
import nl.saxion.model.Printer;
import nl.saxion.model.Spool;
import org.json.simple.JSONArray;

import java.util.ArrayList;

public class SpoolAssigningService {
    private final SpoolRepository spoolsRepo;

    public SpoolAssigningService(SpoolRepository spoolsRepo) {
        this.spoolsRepo = spoolsRepo;
    }

    public void assignSpoolsToPrinter(Printer printer, JSONArray spools) {
        ArrayList<Spool> assignedSpools = new ArrayList<>();
        for (var spool : spools) {
            var spoolID = ((Long) spool).intValue();
            if (spoolID != -1) {
                var spoolToAssign = this.spoolsRepo.getSpoolByID(((Long) spool).intValue());
                assignedSpools.add(spoolToAssign);
                this.spoolsRepo.removeFreeSpool(spoolToAssign);
            }
        }
        printer.setCurrentSpools(assignedSpools);
    }
}

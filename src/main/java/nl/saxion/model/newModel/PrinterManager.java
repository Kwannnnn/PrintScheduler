package nl.saxion.model.newModel;

import nl.saxion.model.FilamentType;
import nl.saxion.model.Spool;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class PrinterManager {
    private final List<Printer> printers;
    private final List<Printer> freePrinters;

    public PrinterManager() {
        this.printers = new ArrayList<>();
        this.freePrinters = new ArrayList<>();
    }

    public void addPrinter(
            int id,
            int printerType,
            String printerName,
            String manufacturer,
            int maxX,
            int maxY,
            int maxZ,
            int maxColors,
            JSONArray currentSpools,
            SpoolManager spoolManager) {
        ArrayList<Spool> cspools = new ArrayList<>();
        for (var spool : currentSpools) {
            cspools.add(spoolManager.getSpoolByID(((Long) spool).intValue()));
        }

        Printer printer;
        switch (printerType) {
            case 1, 3 -> {
                printer = new StandardFDMPrinter(
                        id,
                        printerName,
                        manufacturer,
                        maxX,
                        maxY,
                        maxZ,
                        maxColors,
                        List.of(FilamentType.PLA, FilamentType.PETG)
                );

                ((StandardFDMPrinter) printer).setSpools(cspools);

            }
            case 2 -> {
                printer = new StandardFDMPrinter(
                        id,
                        printerName,
                        manufacturer,
                        maxX,
                        maxY,
                        maxZ,
                        maxColors,
                        List.of(FilamentType.PLA, FilamentType.PETG, FilamentType.ABS)
                );

                ((StandardFDMPrinter) printer).setSpools(cspools);

            }
            default -> throw new IllegalArgumentException("Invalid printer type '" + printerType + '"');
        }

        for(Spool spool: cspools) {
            spoolManager.getFreeSpools().remove(spool);
        }
        printers.add(printer);
        freePrinters.add(printer);
    }

    public List<Printer> getPrinters() {
        return this.printers;
    }

    public List<Printer> getFreePrinters() {
        return this.freePrinters;
    }
}

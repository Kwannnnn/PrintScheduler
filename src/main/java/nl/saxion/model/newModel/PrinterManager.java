package nl.saxion.model.newModel;

import nl.saxion.io.PrinterFactory;
import nl.saxion.io.PrinterJsonLoader;
import nl.saxion.model.Spool;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PrinterManager {
    private final List<Printer> printers;
    private final List<Printer> freePrinters;
    private final SpoolManager spoolManager;


    public PrinterManager(String filename, SpoolManager spoolManager) throws IOException, ParseException {
        this.printers = new ArrayList<>();
        this.freePrinters = new ArrayList<>();
        this.spoolManager = spoolManager;

        var printerJsonLoader = new PrinterJsonLoader(filename, this);
        printerJsonLoader.loadFile();

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
            JSONArray currentSpools) {

        ArrayList<Spool> cspools = new ArrayList<>();
        for (var spool : currentSpools) {
            cspools.add(spoolManager.getSpoolByID(((Long) spool).intValue()));
        }

        PrinterFactory printerFactory = new PrinterFactory();

        Printer printer = printerFactory.createPrinter(
                id,
                printerType,
                printerName,
                manufacturer,
                maxX,
                maxY,
                maxZ,
                maxColors,
                cspools);


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

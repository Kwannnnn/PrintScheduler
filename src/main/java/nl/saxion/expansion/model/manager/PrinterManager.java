package nl.saxion.expansion.model.manager;

import nl.saxion.expansion.model.Constants;
import nl.saxion.expansion.model.Printer;
import nl.saxion.expansion.model.Spool;
import nl.saxion.expansion.model.factory.PrinterFactory;
import nl.saxion.expansion.model.io.PrinterJsonLoader;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PrinterManager {
    private final List<Printer> printers;
    private final List<Printer> freePrinters;
    private final SpoolManager spoolManager;


    public PrinterManager(SpoolManager spoolManager) throws IOException, ParseException {
        this.printers = new ArrayList<>();
        this.freePrinters = new ArrayList<>();
        this.spoolManager = spoolManager;

        new PrinterJsonLoader(Constants.PRINTERS_FILENAME, this)
                .loadFile();
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
            var spoolId = ((Long) spool).intValue();
            if (spoolId > 0) {
                cspools.add(spoolManager.getSpoolByID(((Long) spool).intValue()));
            }
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
            this.spoolManager.getFreeSpools().remove(spool);
        }
        this.printers.add(printer);
        this.freePrinters.add(printer);
    }

    public List<Printer> getPrinters() {
        return this.printers;
    }

    public List<Printer> getFreePrinters() {
        return this.freePrinters;
    }

    public Optional<Printer> getPrinterById(Long id) {
        return this.printers
                .stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }
}

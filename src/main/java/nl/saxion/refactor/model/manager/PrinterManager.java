package nl.saxion.refactor.model.manager;

import nl.saxion.refactor.model.Printer;
import nl.saxion.refactor.model.Spool;
import nl.saxion.refactor.model.factory.PrinterFactory;
import nl.saxion.refactor.model.io.FileLoader;
import nl.saxion.refactor.model.io.record.PrinterFileRecord;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PrinterManager {
    private final List<Printer> printers;
    private final List<Printer> freePrinters;
    private final SpoolManager spoolManager;

    public PrinterManager(FileLoader<PrinterFileRecord> fileLoader,
                          SpoolManager spoolManager)
            throws IOException, ParseException {
        this.printers = new ArrayList<>();
        this.freePrinters = new ArrayList<>();
        this.spoolManager = spoolManager;

        loadPrintersFromRecords(fileLoader.loadFile());
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
            List<Spool> currentSpools) {

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
                currentSpools);


        for(Spool spool : currentSpools) {
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

    private void loadPrintersFromRecords(List<PrinterFileRecord> records) {
        for (PrinterFileRecord record : records) {
            List<Spool> currentSpools = new ArrayList<>();
            for (var spool : record.currentSpools()) {
                var spoolId = ((Long) spool).intValue();
                if (spoolId > 0) {
                    currentSpools.add(this.spoolManager.getSpoolByID(((Long) spool).intValue()));
                }
            }

            this.addPrinter(
                    record.id(),
                    record.type(),
                    record.printerName(),
                    record.manufacturer(),
                    record.maxX(),
                    record.maxY(),
                    record.maxZ(),
                    record.maxColors(),
                    currentSpools
            );
        }
    }
}

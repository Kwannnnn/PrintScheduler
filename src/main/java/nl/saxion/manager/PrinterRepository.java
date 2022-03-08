package nl.saxion.manager;

import nl.saxion.io.PrinterJsonLoader;
import nl.saxion.model.Printer;
import nl.saxion.model.PrinterFactory;
import nl.saxion.service.SpoolAssigningService;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

public class PrinterRepository {
    private final ArrayList<Printer> printers;
    private final ArrayList<Printer> freePrinters;

    public PrinterRepository(String fileName, SpoolAssigningService spoolAssigningService) throws IOException, ParseException {
        this.printers = loadPrintersFromFile(fileName, new PrinterFactory(spoolAssigningService));
        this.freePrinters = new ArrayList<>(this.printers);
    }

    private ArrayList<Printer> loadPrintersFromFile(String fileName, PrinterFactory printerFactory) throws IOException, ParseException {
        return new PrinterJsonLoader(fileName, printerFactory).loadFile();
    }

    public ArrayList<Printer> getPrinters() {
        return this.printers;
    }

    public void removeFreePrinter(Printer printer) {
        this.freePrinters.remove(printer);
    }
}


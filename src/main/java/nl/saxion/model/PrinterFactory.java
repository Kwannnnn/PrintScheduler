package nl.saxion.model;

import nl.saxion.service.SpoolAssigningService;
import org.json.simple.JSONArray;

public class PrinterFactory {
    private final SpoolAssigningService spoolAssigningService;

    public PrinterFactory(SpoolAssigningService spoolAssigningService) {
        this.spoolAssigningService = spoolAssigningService;
    }

    public Printer createPrinter(int id,
                                 int printerType,
                                 String printerName,
                                 String manufacturer,
                                 int maxX,
                                 int maxY,
                                 int maxZ,
                                 int maxColors,
                                 JSONArray currentSpools) {
        var printer = switch (printerType) {
            case 1 -> new StandardFDM(id, printerName, manufacturer, maxX, maxY, maxZ);
            case 2 -> new HousedPrinter(id, printerName, manufacturer, maxX, maxY, maxZ);
            case 3 -> new MultiColor(id, printerName, manufacturer, maxX, maxY, maxZ, maxColors);
            default -> throw new IllegalArgumentException("Invalid printer type! Printer with id" + id + " not loaded.");
        };
        this.spoolAssigningService.assignSpoolsToPrinter(printer, currentSpools);

        return printer;
    }
}

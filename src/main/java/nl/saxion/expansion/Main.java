package nl.saxion.expansion;

import nl.saxion.expansion.model.Constants;
import nl.saxion.expansion.model.SystemFacade;
import nl.saxion.expansion.model.io.PrintJsonLoader;
import nl.saxion.expansion.model.io.PrinterJsonLoader;
import nl.saxion.expansion.model.io.SpoolCsvLoader;
import nl.saxion.expansion.model.io.SpoolJsonLoader;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
//            var spoolJsonLoader = new SpoolJsonLoader(Constants.SPOOLS_JSON_FILENAME);
            var spoolCsvLoader = new SpoolCsvLoader(Constants.SPOOLS_CSV_FILENAME, Constants.CSV_DELIMITER);
            var printJsonLoader = new PrintJsonLoader(Constants.PRINTS_FILENAME);
            var printerJsonLoader = new PrinterJsonLoader(Constants.PRINTERS_FILENAME);

            var model = new SystemFacade(printJsonLoader, spoolCsvLoader, printerJsonLoader);
            var controller = new PrinterSystemController(model);
            var cli = new CLI(model, controller);
            cli.run();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }
}

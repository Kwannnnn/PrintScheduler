package nl.saxion.refactor;

import nl.saxion.refactor.model.SystemFacade;
import nl.saxion.refactor.model.io.PrintJsonLoader;
import nl.saxion.refactor.model.io.PrinterJsonLoader;
import nl.saxion.refactor.model.io.SpoolJsonLoader;
import org.json.simple.parser.ParseException;

import static nl.saxion.refactor.model.Constants.*;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            var spoolJsonLoader = new SpoolJsonLoader(SPOOLS_FILENAME);
            var printJsonLoader = new PrintJsonLoader(PRINTS_FILENAME);
            var printerJsonLoader = new PrinterJsonLoader(PRINTERS_FILENAME);

            var model = new SystemFacade(printJsonLoader, spoolJsonLoader, printerJsonLoader);
            var controller = new PrinterSystemController(model);
            var cli = new CLI(model, controller);
            cli.run();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }
}

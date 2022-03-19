package nl.saxion;

import nl.saxion.model.newModel.SystemFacade;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            var model = new SystemFacade();
            var controller = new PrinterSystemController(model);
            var cli = new CLI(model, controller);
            cli.run();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }
}

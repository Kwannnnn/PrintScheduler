package nl.saxion;

import nl.saxion.model.newModel.SystemFacade;

public class Main {
    public static void main(String[] args) {
        var model = new SystemFacade();
        var controller = new PrinterSystemController(model);
        var cli = new CLI(model, controller);
        cli.run();
    }
}

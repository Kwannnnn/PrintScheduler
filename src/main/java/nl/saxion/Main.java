package nl.saxion;

import nl.saxion.model.newModel.PrinterManager;

public class Main {
    public static void main(String[] args) {
        var model = new PrinterManager();
        var controller = new PrinterSystemController(model);
        var cli = new CLI(model, controller);
        cli.run();
    }
}

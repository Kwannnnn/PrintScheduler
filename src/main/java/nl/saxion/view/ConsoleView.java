package nl.saxion.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.PrintStream;

public class ConsoleView implements PropertyChangeListener {
    private final PrintStream out;

    public ConsoleView(PrintStream out) {
        this.out = out;
    }

    public void displayMenu() {
        this.out.println("Print Manager");
        this.out.println("=============");
        this.out.println("1) Add new Print Task");
        this.out.println("2) Register Printer Completion");
        this.out.println("3) Register Printer Failure");
        this.out.println("4) Change printing style");
        this.out.println("5) Start Print Queue");
        this.out.println("6) Show prints");
        this.out.println("7) Show printers");
        this.out.println("8) Show spools");
        this.out.println("9) Show pending print tasks");
        this.out.println("0) Exit");
    }

    public void displayPrompt(String message) {
        this.out.print(message);
    }

    public void displayLine(String message) {
        this.out.println(message);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        displayLine((String) evt.getNewValue());
    }
}

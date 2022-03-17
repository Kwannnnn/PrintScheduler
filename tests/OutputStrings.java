import models.Print;
import models.Printer;
import models.Spool;

import java.util.ArrayList;

public class OutputStrings {
    private ArrayList<Printer> printers = new ArrayList<>();
    private ArrayList<Print> prints = new ArrayList<>();
    private ArrayList<Spool> spools = new ArrayList<>();
    public OutputStrings() {
        printers.add(new Printer(1, "Enterprise", "Creality", 200, 200, 200, 1));
        printers.add(new Printer(2, "Serenity", "Max3D Prints", 300, 300, 300, 9));
        printers.add(new Printer(3, "Tardis", "Prusa", 200, 200, 200, 4, 4, 5, 6, 7));
        printers.add(new Printer(4, "Rocinante", "Max3D Prints", 300, 300, 300, 3));
        printers.add(new Printer(5, "Bebop", "Tiny Prints", 100, 100, 100, 2));

        prints.add(new Print("Dog", "dog.GCODE", 50, 130, 100, 900));
        prints.add(new Print("House", "house.GCODE", 110, 90, 90, 221, 300));
        prints.add(new Print("Frog", "frog.GCODE", 40, 55, 60, 631));
        prints.add(new Print("Knife", "knife.GCODE", 10, 100, 20, 501));
        prints.add(new Print("Spaceship", "spaceship.GCODE", 100, 120, 230, 1500));

        spools.add(new Spool(1, "Blue", "PLA", 2000.5));
        spools.add(new Spool(2, "Red", "PLA", 2500.0));
        spools.add(new Spool(3, "Green", "PLA", 2500.0));
        spools.add(new Spool(4, "Blue", "PETG", 2000.5));
        spools.add(new Spool(5, "Red", "PETG", 2500.0));
        spools.add(new Spool(6, "Green", "PETG", 2500.0));
        spools.add(new Spool(7, "Pink", "PETG", 2500.0));
        spools.add(new Spool(8, "Blue", "ABS", 2000.5));
        spools.add(new Spool(9, "Red", "ABS", 2500.0));
        spools.add(new Spool(10, "Green", "ABS", 2500.0));
        spools.add(new Spool(11, "Blue", "PLA", 2000.5));
        spools.add(new Spool(12, "Red", "PLA", 2500.0));
        spools.add(new Spool(13, "Green", "PLA", 2500.0));
        spools.add(new Spool(14, "Blue", "PETG", 2000.5));
        spools.add(new Spool(15, "Red", "PETG", 2500.0));
        spools.add(new Spool(16, "Green", "PETG", 2500.0));
        spools.add(new Spool(17, "Pink", "PETG", 2500.0));
        spools.add(new Spool(18, "Blue", "ABS", 2000.5));
        spools.add(new Spool(19, "Red", "ABS", 2500.0));
        spools.add(new Spool(20, "Green", "ABS", 2500.0));

    }

    public void addPrinter(Printer printer) {
        printers.add(printer);
    }

    public String menu() {
        return "Print Manager" + System.lineSeparator() +
                "=============" + System.lineSeparator() +
                "1) Add new Print Task" + System.lineSeparator() +
                "2) Register Printer Completion" + System.lineSeparator() +
                "3) Register Printer Failure" + System.lineSeparator() +
                "4) Change printing style" + System.lineSeparator() +
                "5) Start Print Queue" + System.lineSeparator() +
                "6) Show prints" + System.lineSeparator() +
                "7) Show printers" + System.lineSeparator() +
                "8) Show spools" + System.lineSeparator() +
                "9) Show pending print tasks" + System.lineSeparator() +
                "0) Exit" + System.lineSeparator() +
                "Choose an option: ";
    }

    public String prints() {
        String prints = "---------- Available prints ----------" + System.lineSeparator();
        for(Print p: this.prints) {
            prints += p + System.lineSeparator();
        }
        prints += "--------------------------------------";
        return prints;
    }

    public String getPrint(int index) {
        return prints.get(index).toString();
    }

    public String printers() {
        String printers = "--------- Available printers ---------" + System.lineSeparator();
        for(Printer p: this.printers) {
            printers += p;
        }
        printers += "--------------------------------------" + System.lineSeparator();

        return printers;
    }

    public String activePrinters(boolean failed) {
        String printers = "---------- Currently Running Printers ----------" + System.lineSeparator();
        for(Printer p: this.printers) {
            if(p.printing()) {
                printers += p.activePrintString() + System.lineSeparator();
            }
        }
        if(failed) {
            printers += "Printer ID that failed: ";
        } else {
            printers += "Printer that is done (ID): ";
        }

        return printers;
    }

    public void setCurrentPrintOnPrinter(int index, String print) {
        printers.get(index).setCurrentPrint(print);
    }

    public void reduceSpoolLength(int index, double length) {
        spools.get(index).reduceLength(length);
    }

    public String spools() {
        String spools = "---------- Spools ----------" + System.lineSeparator();
        for(Spool s: this.spools) {
            spools += s + System.lineSeparator();
        }
        spools += "----------------------------";
        return spools;
    }


    public String availablePrints() {

        String prints = "---------- Available prints ----------" + System.lineSeparator();
                int counter = 1;
        for(Print p: this.prints) {
            prints += counter + ": " + p.getName() + System.lineSeparator();
            counter++;
        }
        prints += "--------------------------------------" + System.lineSeparator() +
                "Print number: ";
        return prints;
    }

    public String filamentTypes() {
        return "---------- Filament Type ----------" + System.lineSeparator() +
                "1: PLA" + System.lineSeparator() +
                "2: PETG" + System.lineSeparator() +
                "3: ABS" + System.lineSeparator() +
                "Filament type number: ";
    }

    public String availableSpools(String filament) {
        String spools ="---------- Spools ----------" + System.lineSeparator();
        ArrayList<String> colors = new ArrayList<>();
        int counter = 1;
        for(Spool s: this.spools) {
            if(s.getFilamentType().equals(filament) && !colors.contains(s.getColor())) {
                spools += counter + ": " + s.getColor() + " (" + s.getFilamentType() + ")" + System.lineSeparator();
                colors.add(s.getColor());
                counter++;
            }

        }
        spools += "----------------------------" + System.lineSeparator() +
            "Color number: ";
        return spools;
    }

    public String addPrint(String filament) {
        return  availablePrints() +
                filamentTypes() +
                availableSpools(filament) +
                "Added task to queue" + System.lineSeparator() +
                menu();
    }


    public String addPrint(String filament, int nrColors) {
        String printReturn = availablePrints() +
                filamentTypes() +
                availableSpools(filament);
        for(int i = 1; i < nrColors; i++) {
            printReturn += "Color number: ";
        }
        printReturn += "Added task to queue" + System.lineSeparator() +
                menu();
        return printReturn;
    }
}

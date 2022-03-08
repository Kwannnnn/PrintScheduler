package nl.saxion;

import nl.saxion.manager.PrintRepository;
import nl.saxion.manager.PrinterRepository;
import nl.saxion.manager.SpoolRepository;
import nl.saxion.manager.TaskRepository;
import nl.saxion.model.*;
import nl.saxion.service.SpoolAssigningService;
import org.json.simple.parser.ParseException;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.*;

public class SystemFacade {
    private static final String SPOOLS_FILENAME = "spools.json";
    private static final String PRINTERS_FILENAME = "printers.json";
    private static final String PRINTS_FILENAME = "prints.json";

    private final PrinterRepository printerRepository;
    private final PrintRepository printRepository;
    private final SpoolRepository spoolRepository;
    private final TaskRepository taskManager;
    private PropertyChangeSupport support;

    private ArrayList<PrintTask> pendingPrintTasks = new ArrayList<>();
    private HashMap<Printer, PrintTask> runningPrintTasks = new HashMap();

    public SystemFacade() throws IOException, ParseException {
        this.spoolRepository = new SpoolRepository(SPOOLS_FILENAME);
        this.printRepository = new PrintRepository(PRINTS_FILENAME);
        this.printerRepository = new PrinterRepository(PRINTERS_FILENAME, new SpoolAssigningService(this.spoolRepository));
        this.taskManager = new TaskRepository();
        this.support = new PropertyChangeSupport(this);
    }

    public boolean containsSpool(final List<Spool> list, final String name){
        return list.stream().anyMatch(o -> o.getColor().equals(name));
    }

    public void selectPrintTask(Printer printer) {
        Spool[] spools = printer.getCurrentSpools();
        PrintTask chosenTask = null;
        // First we look if there's a task that matches the current spool on the printer.
        for(PrintTask printTask: pendingPrintTasks) {
            if(printer.printFits(printTask.getPrint())) {
                if (printer instanceof StandardFDM && !printTask.getFilamentType().toString().equals("ABS") && printTask.getColors().size() == 1) {
                    if (spools[0].spoolMatch(printTask.getColors().get(0), printTask.getFilamentType())) {
                        runningPrintTasks.put(printer, printTask);
                        this.printerRepository.removeFreePrinter(printer);
                        chosenTask = printTask;
                        break;
                    }
                    // The housed printer is the only one that can print ABS, but it can also print the others.
                } else if (printer instanceof HousedPrinter && printTask.getColors().size() == 1) {
                    if (spools[0].spoolMatch(printTask.getColors().get(0), printTask.getFilamentType())) {
                        runningPrintTasks.put(printer, printTask);
                        this.printerRepository.removeFreePrinter(printer);
                        chosenTask = printTask;
                        break;
                    }
                    // For multicolor the order of spools does matter, so they have to match.
                } else if (printer instanceof MultiColor && !printTask.getFilamentType().toString().equals("ABS") && printTask.getColors().size() <= ((MultiColor) printer).getMaxColors()) {
                    boolean printWorks = true;
                    for (int i = 0; i < spools.length && i < printTask.getColors().size(); i++) {
                        if (!spools[i].spoolMatch(printTask.getColors().get(i), printTask.getFilamentType())) {
                            printWorks = false;
                        }
                    }
                    if (printWorks) {
                        runningPrintTasks.put(printer, printTask);
                        this.printerRepository.removeFreePrinter(printer);
                        chosenTask = printTask;
                        break;
                    }
                }
            }
        }
        if(chosenTask != null) {
            pendingPrintTasks.remove(chosenTask);
            sendInstruction("Started task " + chosenTask + " on printer " + printer.getName());sendInstruction("Started task " + chosenTask + " on printer " + printer.getName());
        } else {
            // If we didn't find a print for the current spool we search for a print with the free spools.
            for(PrintTask printTask: pendingPrintTasks) {
                if(printer.printFits(printTask.getPrint()) && getPrinterCurrentTask(printer) == null) {
                    if (printer instanceof StandardFDM && !printTask.getFilamentType().toString().equals("ABS") && printTask.getColors().size() == 1) {
                        Spool chosenSpool = null;
                        for (Spool spool : this.spoolRepository.getFreeSpools()) {
                            if (spool.spoolMatch(printTask.getColors().get(0), printTask.getFilamentType())) {
                                chosenSpool = spool;
                            }
                        }
                        if (chosenSpool != null) {
                            runningPrintTasks.put(printer, printTask);
                            this.spoolRepository.addFreeSpool(printer.getCurrentSpools()[0]);
                            sendInstruction("Please place spool " + chosenSpool.getId() + " in printer " + printer.getName());
                            this.spoolRepository.removeFreeSpool(chosenSpool);
                            this.printerRepository.removeFreePrinter(printer);
                            chosenTask = printTask;
                        }
                    } else if (printer instanceof HousedPrinter && printTask.getColors().size() == 1) {
                        Spool chosenSpool = null;
                        for (Spool spool : this.spoolRepository.getFreeSpools()) {
                            if (spool.spoolMatch(printTask.getColors().get(0), printTask.getFilamentType())) {
                                chosenSpool = spool;
                            }
                        }
                        if (chosenSpool != null) {
                            runningPrintTasks.put(printer, printTask);
                            this.spoolRepository.addFreeSpool(printer.getCurrentSpools()[0]);
                            sendInstruction("Please place spool " + chosenSpool.getId() + " in printer " + printer.getName());
                            this.spoolRepository.removeFreeSpool(chosenSpool);
                            this.printerRepository.removeFreePrinter(printer);
                            chosenTask = printTask;
                        }
                    } else if (printer instanceof MultiColor && !printTask.getFilamentType().toString().equals("ABS") && printTask.getColors().size() <= ((MultiColor) printer).getMaxColors()) {
                        ArrayList<Spool> chosenSpools = new ArrayList<>();
                        for (int i = 0; i < printTask.getColors().size(); i++) {
                            for (Spool spool : this.spoolRepository.getFreeSpools()) {
                                if (spool.spoolMatch(printTask.getColors().get(i), printTask.getFilamentType()) && !containsSpool(chosenSpools, printTask.getColors().get(i))) {
                                    chosenSpools.add(spool);
                                }
                            }
                        }
                        // We assume that if they are the same length that there is a match.
                        if (chosenSpools.size() == printTask.getColors().size()) {
                            runningPrintTasks.put(printer, printTask);
                            for (Spool spool : printer.getCurrentSpools()) {
                                this.spoolRepository.addFreeSpool(spool);
                            }
                            printer.setCurrentSpools(chosenSpools);
                            for (Spool spool : chosenSpools) {
                                sendInstruction("Please place spool " + spool.getId() + " in printer " + printer.getName());
                                this.spoolRepository.removeFreeSpool(spool);
                            }
                            this.printerRepository.removeFreePrinter(printer);
                            chosenTask = printTask;
                        }
                    }
                }
            }
            if(chosenTask != null) {
                pendingPrintTasks.remove(chosenTask);
                sendInstruction("Started task " + chosenTask + " on printer " + printer.getName());
            }
        }
    }

    public PrintTask getPrinterCurrentTask(Printer printer) {
        if(!runningPrintTasks.containsKey(printer)) {
            return null;
        }
        return runningPrintTasks.get(printer);
    }

    public void addPrintTask(String printName, List<String> colors, Filament type) {
        Print print = findPrint(printName);
        if (print == null) {
            indicateError("Could not find print with name " + printName);
            return;
        }
        if (colors.size() == 0) {
            indicateError("Need at least one color, but none given");
            return;
        }
        for (String color : colors) {
            boolean found = false;
            for (Spool spool : this.spoolRepository.getSpools()) {
                if (spool.getColor().equals(color) && spool.getFilamentType().toString().equals(type.toString())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                indicateError("Color " + color + " (" + type +") not found");
                return;
            }
        }

        PrintTask task = new PrintTask(print, colors, type);
        pendingPrintTasks.add(task);
        sendInstruction("Added task to queue");

    }

    public Print findPrint(String printName) {
        for (Print p : this.printRepository.getPrints()) {
            if (p.getName().equals(printName)) {
                return p;
            }
        }
        return null;
    }

    public Print findPrint(int index) {
        if(index > this.printRepository.getPrints().size() -1) {
            return null;
        }
        return this.printRepository.getPrints().get(index);
    }

    public Spool getSpoolByID(int id) {
        for(Spool s : this.spoolRepository.getSpools()) {
            if(s.getId() == id) {
                return s;
            }
        }
        return null;
    }

    public void registerPrinterFailure(int printerId) {
        Map.Entry<Printer, PrintTask> foundEntry = null;
        for (Map.Entry<Printer, PrintTask> entry : runningPrintTasks.entrySet()) {
            if (entry.getKey().getId() == printerId) {
                foundEntry = entry;
                break;
            }
        }
        if (foundEntry == null) {
            indicateError("cannot find a running task on printer with ID " + printerId);
            return;
        }
        PrintTask task = foundEntry.getValue();
        pendingPrintTasks.add(task); // add the task back to the queue.
        runningPrintTasks.remove(foundEntry.getKey());

        sendInstruction("Task " + task + " removed from printer "
                + foundEntry.getKey().getName());

        Printer printer = foundEntry.getKey();
        Spool[] spools = printer.getCurrentSpools();
        for(int i=0; i<spools.length && i < task.getColors().size();i++) {
            spools[i].reduceLength(task.getPrint().getFilamentLength().get(i));
        }
        selectPrintTask(printer);
    }

    public void registerCompletion(int printerId) {
        Map.Entry<Printer, PrintTask> foundEntry = null;
        for (Map.Entry<Printer, PrintTask> entry : runningPrintTasks.entrySet()) {
            if (entry.getKey().getId() == printerId) {
                foundEntry = entry;
                break;
            }
        }
        if (foundEntry == null) {
            indicateError("cannot find a running task on printer with ID " + printerId);
            return;
        }
        PrintTask task = foundEntry.getValue();
        runningPrintTasks.remove(foundEntry.getKey());

        sendInstruction("Task " + task + " removed from printer "
                + foundEntry.getKey().getName());

        Printer printer = foundEntry.getKey();
        Spool[] spools = printer.getCurrentSpools();
        for(int i=0; i<spools.length && i < task.getColors().size();i++) {
            spools[i].reduceLength(task.getPrint().getFilamentLength().get(i));
        }
        selectPrintTask(printer);


    }

    private void indicateError(String s) {
        var message = String.format("Error: %s", s);
        this.support.firePropertyChange("error", "", message);
//        new Scanner(System.in).nextLine();
    }

    private void sendInstruction(String s) {
        this.support.firePropertyChange("instruction", "", s);
//        new Scanner(System.in).nextLine();
    }

    public void addPropertyChangeListener( PropertyChangeListener listener ) {
        this.support.addPropertyChangeListener( listener );
    }

    //region new impl
    public void showSpools() {
        var result = new StringBuilder("---------- Spools ----------\n");
        for (var spool : this.spoolRepository.getSpools()) {
            result.append(spool)
                    .append(System.lineSeparator());
        }
        result.append("----------------------------");

        this.support.firePropertyChange("spools", "", result.toString());
    }

    public void showPrinters() {
        var result = new StringBuilder("--------- Available printers ---------\n");
        for (var p : this.printerRepository.getPrinters()) {
            result.append(p);
            PrintTask currentTask = this.getPrinterCurrentTask(p);
            if(currentTask != null) {
                result.append("Current Print Task: ")
                        .append(currentTask)
                        .append(System.lineSeparator());
            }
            result.append(System.lineSeparator());
        }
        result.append("--------------------------------------");

        this.support.firePropertyChange("printers", "", result.toString());
    }

    public void showCurrentlyRunningPrinters() {
        var result = new StringBuilder("---------- Currently Running Printers ----------\n");
            for(Printer p: this.printerRepository.getPrinters()) {
                PrintTask printerCurrentTask= this.getPrinterCurrentTask(p);
                if(printerCurrentTask != null) {
                    result.append(p.getId())
                            .append(": ")
                            .append(p.getName())
                            .append(" - ")
                            .append(printerCurrentTask)
                            .append(System.lineSeparator());
                }
        }

        this.support.firePropertyChange("runningPrinters", "", result.toString());
    }

    public void showPrintNamesNumbered() {
        var result = new StringBuilder("---------- Available prints ----------\n");
        var prints = this.printRepository.getPrints();
        for (int i = 0; i < prints.size(); i++) {
            result.append(i + 1)
                    .append(": ")
                    .append(prints.get(i).getName())
                    .append(System.lineSeparator());
        }
        result.append("--------------------------------------");

        this.support.firePropertyChange("prints", "", result.toString());
    }

    public void showPrints() {
        var result = new StringBuilder("---------- Available prints ----------\n");
        for (var p : this.printRepository.getPrints()) {
            result.append(p)
                    .append(System.lineSeparator());
        }
        result.append("--------------------------------------");

        this.support.firePropertyChange("prints", "", result.toString());
    }

    public void showPendingPrintTasks() {
        var result = new StringBuilder("---------- Available prints ----------\n");
        for (var pt : pendingPrintTasks) {
            result.append(pt)
                    .append(System.lineSeparator());
        }
        result.append("--------------------------------------");

        this.support.firePropertyChange("printTasks", "", result.toString());
    }

    public void startPrintQueue() {
        for(Printer printer: this.printerRepository.getPrinters()) {
            selectPrintTask(printer);
        }
    }

    public boolean printWithIdExists(int printId) {
        return findPrint(printId) != null;

    }

    public void showFilamentTypesNumbered() {
        var result = new StringBuilder("---------- Filament Type ----------\n")
                .append("1: PLA")
                .append(System.lineSeparator())
                .append("2: PETG")
                .append(System.lineSeparator())
                .append("3: ABS");

        this.support.firePropertyChange("filamentTypes", "", result.toString());
    }

    public void showSpoolsOfFilamentType(int ftype) {
        Filament filament = switch (ftype) {
            case 1 -> new PLAFilament();
            case 2 -> new PETGFilament();
            case 3 -> new ABSFilament();
            default -> throw new RuntimeException("Not a valid filamentType, bailing out");
        };

        for (var spool :
                this.spoolRepository.getSpools()) {

        }
    }
    //endregion
}

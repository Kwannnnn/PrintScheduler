package nl.saxion;

import nl.saxion.model.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class PrintTaskManager {
    private final PropertyChangeSupport propertyChangeSupport;
    private final List<Print> prints;
    private final List<Spool> spools;
    private final List<Printer> printers;
    private ArrayList<Spool> freeSpools = new ArrayList<>(); // TODO: Decide if this should be used at all.
    private ArrayList<Printer> freePrinters = new ArrayList<>();
    private ArrayList<PrintTask> pendingPrintTasks = new ArrayList<>();
    private HashMap<Printer, PrintTask> runningPrintTasks = new HashMap<>();

    public PrintTaskManager() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.spools = new ArrayList<>();
        this.printers = new ArrayList<>();
        this.prints = new ArrayList<>();
        readPrintsFromFile();
        readSpoolsFromFile();
        readPrintersFromFile();
    }

    public void addPrintTask(int printId, List<String> colors, int type) {
        Print print = findPrintById(printId);
        if (print == null) {
            // TODO: printId ???
            fireAnError("Could not find print with name " + printId);
            return;
        }

        if (colors.size() == 0) {
            fireAnError("Need at least one color, but none given");
            return;
        }

        for (String color : colors) {
            boolean found = false;
            for (Spool spool : this.spools) {
                if (spool.getColor().equals(color) && spool.getFilamentType().getId() == type) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                fireAnError("Color " + color + " (" + type +") not found");
                return;
            }
        }

        FilamentType ftype = switch (type) {
            case 1 -> FilamentType.PLA;
            case 2 -> FilamentType.PETG;
            case 3 -> FilamentType.ABS;
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };

        PrintTask task = new PrintTask(print, colors, ftype);
        this.pendingPrintTasks.add(task);
        fireAnInstruction("Added task to queue");
    }

    private void selectPrintTask(Printer printer) {
        Spool[] spools = printer.getCurrentSpools();
        PrintTask chosenTask = null;
        // First we look if there's a task that matches the current spool on the printer.
        for(PrintTask printTask: pendingPrintTasks) {
            if(printer.printFits(printTask.getPrint())) {
                if (printer instanceof StandardFDM && printTask.getFilamentType() != FilamentType.ABS && printTask.getColors().size() == 1) {
                    if (spools[0].spoolMatch(printTask.getColors().get(0), printTask.getFilamentType())) {
                        runningPrintTasks.put(printer, printTask);
                        freePrinters.remove(printer);
                        chosenTask = printTask;
                        break;
                    }
                    // The housed printer is the only one that can print ABS, but it can also print the others.
                } else if (printer instanceof HousedPrinter && printTask.getColors().size() == 1) {
                    if (spools[0].spoolMatch(printTask.getColors().get(0), printTask.getFilamentType())) {
                        runningPrintTasks.put(printer, printTask);
                        freePrinters.remove(printer);
                        chosenTask = printTask;
                        break;
                    }
                    // For multicolor the order of spools does matter, so they have to match.
                } else if (printer instanceof MultiColor && printTask.getFilamentType() != FilamentType.ABS && printTask.getColors().size() <= ((MultiColor) printer).getMaxColors()) {
                    boolean printWorks = true;
                    for (int i = 0; i < spools.length && i < printTask.getColors().size(); i++) {
                        if (!spools[i].spoolMatch(printTask.getColors().get(i), printTask.getFilamentType())) {
                            printWorks = false;
                        }
                    }
                    if (printWorks) {
                        runningPrintTasks.put(printer, printTask);
                        freePrinters.remove(printer);
                        chosenTask = printTask;
                        break;
                    }
                }
            }
        }
        if(chosenTask != null) {
            pendingPrintTasks.remove(chosenTask);
            fireAnInstruction("Started task " + chosenTask + " on printer " + printer.getName());
        } else {
            // If we didn't find a print for the current spool we search for a print with the free spools.
            for(PrintTask printTask: pendingPrintTasks) {
                if(printer.printFits(printTask.getPrint()) && getPrinterCurrentTask(printer) == null) {
                    if (printer instanceof StandardFDM && printTask.getFilamentType() != FilamentType.ABS && printTask.getColors().size() == 1) {
                        Spool chosenSpool = null;
                        for (Spool spool : freeSpools) {
                            if (spool.spoolMatch(printTask.getColors().get(0), printTask.getFilamentType())) {
                                chosenSpool = spool;
                            }
                        }
                        if (chosenSpool != null) {
                            runningPrintTasks.put(printer, printTask);
                            freeSpools.add(printer.getCurrentSpools()[0]);
                            fireAnInstruction("Please place spool " + chosenSpool.getId() + " in printer " + printer.getName());
                            freeSpools.remove(chosenSpool);
                            freePrinters.remove(printer);
                            chosenTask = printTask;
                        }
                    } else if (printer instanceof HousedPrinter && printTask.getColors().size() == 1) {
                        Spool chosenSpool = null;
                        for (Spool spool : freeSpools) {
                            if (spool.spoolMatch(printTask.getColors().get(0), printTask.getFilamentType())) {
                                chosenSpool = spool;
                            }
                        }
                        if (chosenSpool != null) {
                            runningPrintTasks.put(printer, printTask);
                            freeSpools.add(printer.getCurrentSpools()[0]);
                            fireAnInstruction("Please place spool " + chosenSpool.getId() + " in printer " + printer.getName());
                            freeSpools.remove(chosenSpool);
                            freePrinters.remove(printer);
                            chosenTask = printTask;
                        }
                    } else if (printer instanceof MultiColor && printTask.getFilamentType() != FilamentType.ABS && printTask.getColors().size() <= ((MultiColor) printer).getMaxColors()) {
                        ArrayList<Spool> chosenSpools = new ArrayList<>();
                        for (int i = 0; i < printTask.getColors().size(); i++) {
                            for (Spool spool : freeSpools) {
                                if (spool.spoolMatch(printTask.getColors().get(i), printTask.getFilamentType()) && !containsSpool(chosenSpools, printTask.getColors().get(i))) {
                                    chosenSpools.add(spool);
                                }
                            }
                        }
                        // We assume that if they are the same length that there is a match.
                        if (chosenSpools.size() == printTask.getColors().size()) {
                            runningPrintTasks.put(printer, printTask);
                            for (Spool spool : printer.getCurrentSpools()) {
                                freeSpools.add(spool);
                            }
                            printer.setCurrentSpools(chosenSpools);
                            for (Spool spool : chosenSpools) {
                                fireAnInstruction("Please place spool " + spool.getId() + " in printer " + printer.getName());
                                freeSpools.remove(spool);
                            }
                            freePrinters.remove(printer);
                            chosenTask = printTask;
                        }
                    }
                }
            }
            if(chosenTask != null) {
                pendingPrintTasks.remove(chosenTask);
                fireAnInstruction("Started task " + chosenTask + " on printer " + printer.getName());
            }
        }
    }

    public void startInitialQueue() {
        for(Printer printer : printers) {
            selectPrintTask(printer);
        }
    }

    public boolean containsSpool(final List<Spool> list, final String name){
        return list.stream().anyMatch(o -> o.getColor().equals(name));
    }

    public List<String> getRunningPrinters() {
        List<String> result = new ArrayList<>();
        for(Printer p: printers) {
            PrintTask printerCurrentTask = getPrinterCurrentTask(p);
            if(printerCurrentTask != null) {
                result.add(p.getId() + ": " +p.getName() + " - " + printerCurrentTask);
            }
        }

        return result;
    }

    public PrintTask getPrinterCurrentTask(Printer printer) {
        if(!runningPrintTasks.containsKey(printer)) {
            return null;
        }
        return runningPrintTasks.get(printer);
    }

    public void addPrinter(int id, int printerType, String printerName, String manufacturer, int maxX, int maxY, int maxZ, int maxColors, JSONArray currentSpools) {
        if (printerType == 1) {
            StandardFDM printer = new StandardFDM(id, printerName, manufacturer, maxX, maxY, maxZ);
            Spool cspool = getSpoolByID(((Long) currentSpools.get(0)).intValue());
            printer.setCurrentSpool(cspool);
            freeSpools.remove(cspool);
            printers.add(printer);
            freePrinters.add(printer);
        } else if (printerType == 2) {
            HousedPrinter printer = new HousedPrinter(id, printerName, manufacturer, maxX, maxY, maxZ);
            Spool cspool = getSpoolByID(((Long) currentSpools.get(0)).intValue());
            printer.setCurrentSpool(cspool);
            freeSpools.remove(cspool);
            printers.add(printer);
            freePrinters.add(printer);
        } else if (printerType == 3) {
            MultiColor printer = new MultiColor(id, printerName, manufacturer, maxX, maxY, maxZ, maxColors);
            ArrayList<Spool> cspools = new ArrayList<>();
            cspools.add(getSpoolByID(((Long) currentSpools.get(0)).intValue()));
            cspools.add(getSpoolByID(((Long) currentSpools.get(1)).intValue()));
            cspools.add(getSpoolByID(((Long) currentSpools.get(2)).intValue()));
            cspools.add(getSpoolByID(((Long) currentSpools.get(3)).intValue()));
            printer.setCurrentSpools(cspools);
            for(Spool spool: cspools) {
                freeSpools.remove(spool);
            }
            printers.add(printer);
            freePrinters.add(printer);
        }
    }

    public Spool getSpoolByID(int id) {
        for(Spool s: spools) {
            if(s.getId() == id) {
                return s;
            }
        }
        return null;
    }

    public void addPrint(String name, String filename, int height, int width, int length, ArrayList<Integer> filamentLength) {
        Print p = new Print(name, filename, height, width, length, filamentLength);
        prints.add(p);
    }


    public void addSpool(Spool spool) {
        spools.add(spool);
        freeSpools.add(spool);
    }

    private void readSpoolsFromFile() {
        JSONParser jsonParser = new JSONParser();
        URL spoolsResource = getClass().getResource("/spools.json");
        if (spoolsResource == null) {
            System.err.println("Warning: Could not find spools.json file");
            return;
        }
        try (FileReader reader = new FileReader(spoolsResource.getFile())) {
            JSONArray spools = (JSONArray) jsonParser.parse(reader);
            for (Object p : spools) {
                JSONObject spool = (JSONObject) p;
                int id = ((Long) spool.get("id")).intValue();
                String color = (String) spool.get("color");
                String filamentType = (String) spool.get("filamentType");
                double length = (Double) spool.get("length");
                FilamentType type;
                switch (filamentType) {
                    case "PLA":
                        type = FilamentType.PLA;
                        break;
                    case "PETG":
                        type = FilamentType.PETG;
                        break;
                    case "ABS":
                        type = FilamentType.ABS;
                        break;
                    default:
                        System.out.println("Not a valid filamentType, bailing out");
                        return;
                }
                addSpool(new Spool(id, color, type, length));
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }


    private void readPrintsFromFile() {
        JSONParser jsonParser = new JSONParser();
        URL printResource = getClass().getResource("/prints.json");
        if (printResource == null) {
            System.err.println("Warning: Could not find prints.json file");
            return;
        }
        try (FileReader reader = new FileReader(printResource.getFile())) {
            JSONArray prints = (JSONArray) jsonParser.parse(reader);
            for (Object p : prints) {
                JSONObject print = (JSONObject) p;
                String name = (String) print.get("name");
                String filename = (String) print.get("filename");
                int height = ((Long) print.get("height")).intValue();
                int width = ((Long) print.get("width")).intValue();
                int length = ((Long) print.get("length")).intValue();
                //int filamentLength = ((Long) print.get("filamentLength")).intValue();
                JSONArray fLength = (JSONArray) print.get("filamentLength");
                ArrayList<Integer> filamentLength = new ArrayList();
                for(int i = 0; i < fLength.size(); i++) {
                    filamentLength.add(((Long)fLength.get(i)).intValue());
                }
                this.addPrint(name, filename, height, width, length, filamentLength);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void readPrintersFromFile() {
        JSONParser jsonParser = new JSONParser();
        URL printersResource = getClass().getResource("/printers.json");
        if (printersResource == null) {
            System.err.println("Warning: Could not find printers.json file");
            return;
        }
        try (FileReader reader = new FileReader(printersResource.getFile())) {
            JSONArray printers = (JSONArray) jsonParser.parse(reader);
            for (Object p : printers) {
                JSONObject printer = (JSONObject) p;
                int id = ((Long) printer.get("id")).intValue();
                int type = ((Long) printer.get("type")).intValue();
                String name = (String) printer.get("name");
                String manufacturer = (String) printer.get("manufacturer");
                int maxX = ((Long) printer.get("maxX")).intValue();
                int maxY = ((Long) printer.get("maxY")).intValue();
                int maxZ = ((Long) printer.get("maxZ")).intValue();
                int maxColors = ((Long) printer.get("maxColors")).intValue();
                // TODO: Add current Spool
                JSONArray currentSpools = (JSONArray) printer.get("currentSpools");
                this.addPrinter(id, type, name, manufacturer, maxX, maxY, maxZ, maxColors, currentSpools);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
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
            fireAnError("cannot find a running task on printer with ID " + printerId);
            return;
        }
        PrintTask task = foundEntry.getValue();
        pendingPrintTasks.add(task); // add the task back to the queue.
        runningPrintTasks.remove(foundEntry.getKey());

       fireAnInstruction("Task " + task + " removed from printer "
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
            fireAnError("cannot find a running task on printer with ID " + printerId);
            return;
        }
        PrintTask task = foundEntry.getValue();
        runningPrintTasks.remove(foundEntry.getKey());

        fireAnInstruction("Task " + task + " removed from printer "
                + foundEntry.getKey().getName());

        Printer printer = foundEntry.getKey();
        Spool[] spools = printer.getCurrentSpools();
        for(int i=0; i<spools.length && i < task.getColors().size();i++) {
            spools[i].reduceLength(task.getPrint().getFilamentLength().get(i));
        }

        selectPrintTask(printer);
    }

    private void fireAnError(String message) {
        this.propertyChangeSupport.firePropertyChange(
                "error",
                "",
                message
        );
    }

    private void fireAnInstruction(String message) {
        this.propertyChangeSupport.firePropertyChange(
                "instruction",
                "",
                message
        );
    }


    public Print findPrintById(int index) {
        if (index > this.prints.size() -1) {
            return null;
        }
        return this.prints.get(index);
    }

    public void addListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public List<String> getFilaments() {
        return Arrays.stream(FilamentType.values())
                .map(FilamentType::getName)
                .toList();
    }

    public List<String> getPrintNames() {
        return this.prints.stream()
                .map(Print::getName)
                .toList();
    }

    public List<String> getPrints() {
        return this.prints.stream()
                .map(Print::toString)
                .toList();
    }

    public List<String> getSpools() {
        return this.spools.stream()
                .map(Spool::toString)
                .toList();
    }

    public List<String> getPrinters() {
        List<String> result = new ArrayList<>();
        for (var p : printers) {
            StringBuilder printer = new StringBuilder(p.toString());
            PrintTask currentTask = getPrinterCurrentTask(p);
            if (currentTask != null) {
                printer.append("Current Print Task: ")
                        .append(currentTask)
                        .append(System.lineSeparator());
            }
            result.add(printer.toString());
        }

        return result;
    }

    public int getPrintColorsSize(int printChoice) {
        return prints.get(printChoice).getFilamentLength().size();
    }

    public List<String> getAvailableColors(int filamentChoice) {
        FilamentType filamentType = Arrays
                .stream(FilamentType.values())
                .filter(f -> f.getId() == filamentChoice)
                .findFirst()
                .orElse(null);

        assert filamentType != null;

        return spools.stream()
                .filter(s -> s.getFilamentType() == filamentType)
                .map(Spool::getColor)
                .distinct()
                .toList();
    }

    public List<String> getPendingPrintTasks() {
        return this.pendingPrintTasks
                .stream()
                .map(PrintTask::toString)
                .toList();
    }
}

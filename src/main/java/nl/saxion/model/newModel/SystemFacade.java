package nl.saxion.model.newModel;

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

public class SystemFacade {
    private final PropertyChangeSupport propertyChangeSupport;
    private final PrinterManager printerManager;
    private final PrintManager printManager;
    private final SpoolManager spoolManager;
    private final TaskManager taskManager;

    public SystemFacade() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.printerManager = new PrinterManager();
        this.printManager = new PrintManager();
        this.spoolManager = new SpoolManager();
        this.taskManager = new TaskManager();
        readPrintsFromFile();
        readSpoolsFromFile();
        readPrintersFromFile();
    }

    public void addListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void addPrintTask(int printId, List<String> colors, int type) {
        Print print = this.printManager.findPrintById(printId);
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
            for (Spool spool : this.spoolManager.getSpools()) {
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

        this.taskManager.addPrintTask(print, colors, ftype);
        fireAnInstruction("Added task to queue");
    }

    public void startInitialQueue() {
        /* TODO: This is extremely inefficient, because it will loop
         * through all printers even if there are no more print tasks
         * left in the queue.
         */
        for(Printer printer : this.printerManager.getPrinters()) {
            selectPrintTask(printer);
        }
    }

    public List<String> getRunningPrinters() {
        List<String> result = new ArrayList<>();
        for(Printer p : this.printerManager.getPrinters()) {
            PrintTask printerCurrentTask = this.taskManager.getPrinterCurrentTask(p);
            if(printerCurrentTask != null) {
                result.add(p.getId() + ": " +p.getName() + " - " + printerCurrentTask);
            }
        }

        return result;
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
                this.spoolManager.addSpool(id, color, type, length);
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
                this.printManager.addPrint(name, filename, height, width, length, filamentLength);
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
                this.printerManager.addPrinter(id, type, name, manufacturer, maxX, maxY, maxZ, maxColors, currentSpools, this.spoolManager);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public void registerPrinterFailure(int printerId) {
        Map.Entry<Printer, PrintTask> foundEntry = null;
        for (Map.Entry<Printer, PrintTask> entry : this.taskManager.getRunningPrintTasks().entrySet()) {
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
        // TODO: this is not good
        this.taskManager.getPendingPrintTasks().add(task); // add the task back to the queue.
        this.taskManager.removeRunningPrintTask(foundEntry.getKey());

        fireAnInstruction("Task " + task + " removed from printer "
                + foundEntry.getKey().getName());

        Printer printer = foundEntry.getKey();
        FilamentReducingVisitor filamentReducingVisitor = new FilamentReducingVisitor(task);
        printer.accept(filamentReducingVisitor);
        selectPrintTask(printer);
    }

    public void registerCompletion(int printerId) {
        Map.Entry<Printer, PrintTask> foundEntry = null;
        for (Map.Entry<Printer, PrintTask> entry : this.taskManager.getRunningPrintTasks().entrySet()) {
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
        this.taskManager.removeRunningPrintTask(foundEntry.getKey());

        fireAnInstruction("Task " + task + " removed from printer "
                + foundEntry.getKey().getName());

        Printer printer = foundEntry.getKey();
        FilamentReducingVisitor filamentReducingVisitor = new FilamentReducingVisitor(task);
        printer.accept(filamentReducingVisitor);

        selectPrintTask(printer);
    }

    public List<String> getFilaments() {
        return Arrays.stream(FilamentType.values())
                .map(FilamentType::getName)
                .toList();
    }

    public List<String> getPrintNames() {
        return this.printManager.getPrints().stream()
                .map(Print::getName)
                .toList();
    }

    public List<String> getPrints() {
        return this.printManager.getPrints().stream()
                .map(Print::toString)
                .toList();
    }

    public List<String> getSpools() {
        return this.spoolManager.getSpools().stream()
                .map(Spool::toString)
                .toList();
    }

    public List<String> getPrinters() {
        List<String> result = new ArrayList<>();
        for (var p : this.printerManager.getPrinters()) {
            StringBuilder printer = new StringBuilder(p.toString());
            PrintTask currentTask = this.taskManager.getPrinterCurrentTask(p);
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
        return this.printManager.getPrints().get(printChoice).getFilamentLength().size();
    }

    public List<String> getAvailableColors(int filamentChoice) {
        FilamentType filamentType = Arrays
                .stream(FilamentType.values())
                .filter(f -> f.getId() == filamentChoice)
                .findFirst()
                .orElse(null);

        assert filamentType != null;

        return this.spoolManager.getSpools().stream()
                .filter(s -> s.getFilamentType() == filamentType)
                .map(Spool::getColor)
                .distinct()
                .toList();
    }

    public List<String> getPendingPrintTasks() {
        return this.taskManager.getPendingPrintTasks()
                .stream()
                .map(PrintTask::toString)
                .toList();
    }

    private void selectPrintTask(Printer printer) {
        Optional<PrintTask> chosenTaskOptional =
                chooseTaskUsingCurrentSpools(printer)
                        .or(() -> chooseTaskUsingFreeSpools(printer));

        if (chosenTaskOptional.isPresent()) {
            PrintTask chosenTask = chosenTaskOptional.get();
            this.taskManager.removePendingPrintTask(chosenTask);
            fireAnInstruction("Started task " + chosenTask + " on printer " + printer.getName());
        }
    }

    private Optional<PrintTask> chooseTaskUsingCurrentSpools(Printer printer) {
        ChooseTaskVisitor chooseTaskVisitor = new ChooseTaskVisitor(
                this.taskManager.getPendingPrintTasks(),
                this.taskManager.getRunningPrintTasks(),
                this.printerManager.getFreePrinters());
        printer.accept(chooseTaskVisitor);

        return chooseTaskVisitor.getChosenPrintTask();
    }

    private Optional<PrintTask> chooseTaskUsingFreeSpools(Printer printer) {
        SpoolSwitchingVisitor spoolSwitchingVisitor = new SpoolSwitchingVisitor(
                this.taskManager.getPendingPrintTasks(),
                this.taskManager.getRunningPrintTasks(),
                this.printerManager.getFreePrinters(),
                this.spoolManager.getFreeSpools(),
                this.propertyChangeSupport);
        printer.accept(spoolSwitchingVisitor);

        return spoolSwitchingVisitor.getChosenPrintTask();
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
}

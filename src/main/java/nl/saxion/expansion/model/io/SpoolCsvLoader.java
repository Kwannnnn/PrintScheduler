package nl.saxion.expansion.model.io;

import nl.saxion.expansion.model.FilamentType;
import nl.saxion.expansion.model.manager.SpoolManager;

public class SpoolCsvLoader extends AbstractCsvLoader {
    private SpoolManager spoolManager;

    public SpoolCsvLoader(String filename, SpoolManager spoolManager) {
        super(filename);
        this.spoolManager = spoolManager;
    }

    @Override
    protected void parseObject(String[] args) {
        int id = Integer.parseInt(args[0]);
        String name = args[1];
        String filamentType = args[2];
        double length = Double.parseDouble(args[3]);

        FilamentType type = switch (filamentType) {
            case "PLA" -> FilamentType.PLA;
            case "PETG" -> FilamentType.PETG;
            case "ABS" -> FilamentType.ABS;
            default -> throw new IllegalArgumentException("Not a valid filamentType! Spool with id " + id + " not loaded.");
        };

        this.spoolManager.addSpool(
                id, name, type, length
        );
    }
}

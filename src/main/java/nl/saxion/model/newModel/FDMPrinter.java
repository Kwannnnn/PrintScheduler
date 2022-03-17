package nl.saxion.model.newModel;

import nl.saxion.model.FilamentType;
import nl.saxion.model.Spool;

import java.util.List;

public interface FDMPrinter extends Printer {
    int getMaxSpools();
    Spool[] getSpools();
    void setSpools(List<Spool> spools);
    List<FilamentType> getSupportedFilaments();
}

package nl.saxion.expansion.model.strategy;

import nl.saxion.expansion.model.PrintTask;
import nl.saxion.expansion.model.Printer;

import java.util.Optional;

public interface PrintingStrategy {
    Optional<PrintTask> choosePrintTask(Printer printer);
}

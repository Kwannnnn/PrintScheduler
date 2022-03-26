package nl.saxion.expansion.model.strategy;

import nl.saxion.expansion.model.PrintTask;
import nl.saxion.expansion.model.Printer;
import nl.saxion.expansion.model.visitor.EfficientSpoolVisitor;

import java.util.Optional;

public class EfficientSpoolUsageStrategy implements PrintingStrategy {
    private final EfficientSpoolVisitor efficientSpoolVisitor;

    public EfficientSpoolUsageStrategy(EfficientSpoolVisitor efficientSpoolVisitor) {
        this.efficientSpoolVisitor = efficientSpoolVisitor;
    }

    @Override
    public Optional<PrintTask> choosePrintTask(Printer printer) {
        printer.accept(this.efficientSpoolVisitor);

        return this.efficientSpoolVisitor.getChosenPrintTask();
    }

    @Override
    public String toString() {
        return "Efficient Spool Usage";
    }
}

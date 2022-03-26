package nl.saxion.expansion.model.strategy;

import nl.saxion.expansion.model.PrintTask;
import nl.saxion.expansion.model.Printer;
import nl.saxion.expansion.model.visitor.ChooseTaskVisitor;
import nl.saxion.expansion.model.visitor.SpoolSwitchingVisitor;

import java.util.Optional;

public class LessSpoolChangeStrategy implements PrintingStrategy {
    private final ChooseTaskVisitor chooseTaskVisitor;
    private final SpoolSwitchingVisitor spoolSwitchingVisitor;

    public LessSpoolChangeStrategy(ChooseTaskVisitor chooseTaskVisitor,
                                   SpoolSwitchingVisitor spoolSwitchingVisitor) {
        this.chooseTaskVisitor = chooseTaskVisitor;
        this.spoolSwitchingVisitor = spoolSwitchingVisitor;
    }

    @Override
    public Optional<PrintTask> choosePrintTask(Printer printer) {
        Optional<PrintTask> chosenPrintTask = chooseTaskUsingCurrentSpools(printer)
                .or(() -> chooseTaskUsingFreeSpools(printer));
        return chosenPrintTask;
    }

    private Optional<PrintTask> chooseTaskUsingCurrentSpools(Printer printer) {
        printer.accept(this.chooseTaskVisitor);

        return this.chooseTaskVisitor.getChosenPrintTask();
    }

    private Optional<PrintTask> chooseTaskUsingFreeSpools(Printer printer) {
        printer.accept(this.spoolSwitchingVisitor);

        return this.spoolSwitchingVisitor.getChosenPrintTask();
    }

    @Override
    public String toString() {
        return "Less Spool Changes";
    }
}

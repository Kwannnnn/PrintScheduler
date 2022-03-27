package nl.saxion.expansion.model.manager;

import nl.saxion.expansion.model.Constants;
import nl.saxion.expansion.model.Print;
import nl.saxion.expansion.model.io.FileLoader;
import nl.saxion.expansion.model.io.PrintJsonLoader;
import nl.saxion.expansion.model.io.record.PrintFileRecord;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PrintManager {
    private final List<Print> prints;

    public PrintManager(FileLoader<PrintFileRecord> fileLoader) throws IOException, ParseException {
        this.prints = new ArrayList<>();
        loadPrintsFromRecords(fileLoader.loadFile());
    }

    public void addPrint(String name, String filename, int height, int width, int length, List<Integer> filamentLength) {
        Print p = new Print(name, filename, height, width, length, filamentLength);
        this.prints.add(p);
    }

    public List<Print> getPrints() {
        return this.prints;
    }

    public Optional<Print> findPrintById(int index) {
        if (index > this.prints.size() -1) {
            return Optional.empty();
        }
        return Optional.of(this.prints.get(index));
    }

    private void loadPrintsFromRecords(List<PrintFileRecord> records) {
        for (PrintFileRecord record : records) {
            List<Integer> filamentLength = new ArrayList<>();
            for (var value : record.filamentLength()) {
                filamentLength.add(((Long) value).intValue());
            }

            this.addPrint(
                    record.name(),
                    record.filename(),
                    record.height(),
                    record.width(),
                    record.length(),
                    filamentLength);
        }
    }
}

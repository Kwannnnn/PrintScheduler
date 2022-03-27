package nl.saxion.refactor.model.manager;

import nl.saxion.refactor.model.Constants;
import nl.saxion.refactor.model.Print;
import nl.saxion.refactor.model.io.PrintJsonLoader;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PrintManager {
    private final List<Print> prints;

    public PrintManager() throws IOException, ParseException {
        this.prints = new ArrayList<>();

        new PrintJsonLoader(Constants.PRINTS_FILENAME, this)
                .loadFile();
    }

    public void addPrint(String name, String filename, int height, int width, int length, ArrayList<Integer> filamentLength) {
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
}

package nl.saxion.model.manager;

import nl.saxion.model.Constants;
import nl.saxion.model.io.PrintJsonLoader;
import nl.saxion.model.Print;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public Print findPrintById(int index) {
        if (index > this.prints.size() -1) {
            return null;
        }
        return this.prints.get(index);
    }
}

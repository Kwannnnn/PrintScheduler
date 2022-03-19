package nl.saxion.model.newModel;

import nl.saxion.model.Print;

import java.util.ArrayList;
import java.util.List;

public class PrintManager {
    private final List<Print> prints;

    public PrintManager() {
        this.prints = new ArrayList<>();
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

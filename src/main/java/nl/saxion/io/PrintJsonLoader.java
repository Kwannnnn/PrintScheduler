package nl.saxion.io;

import nl.saxion.model.Print;
import nl.saxion.model.newModel.PrintManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class PrintJsonLoader extends JsonLoader<Print> {
    PrintManager printManager;

    public PrintJsonLoader(String filename, PrintManager printManager) {
        super(filename);
        this.printManager = printManager;
    }

    @Override
    protected void parseObject(JSONObject o) {
        String name = (String) o.get("name");
        String filename = (String) o.get("filename");
        int height = ((Long) o.get("height")).intValue();
        int width = ((Long) o.get("width")).intValue();
        int length = ((Long) o.get("length")).intValue();
        //int filamentLength = ((Long) print.get("filamentLength")).intValue();
        JSONArray fLength = (JSONArray) o.get("filamentLength");
        ArrayList<Integer> filamentLength = new ArrayList<>();
        for (var value : fLength) {
            filamentLength.add(((Long) value).intValue());
        }

        this.printManager.addPrint(name, filename, height, width, length, filamentLength);
    }
}

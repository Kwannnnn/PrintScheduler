package nl.saxion.expansion.model.io;

import nl.saxion.expansion.model.io.record.SpoolFileRecord;
import org.json.simple.JSONObject;

public class SpoolJsonLoader extends AbstractJsonLoader<SpoolFileRecord> {

    public SpoolJsonLoader(String filename) {
        super(filename);
    }

    @Override
    protected SpoolFileRecord parseObject(JSONObject o) {
        int id = ((Long) o.get("id")).intValue();
        String color = (String) o.get("color");
        String filamentType = (String) o.get("filamentType");
        double length = (Double) o.get("length");

        return new SpoolFileRecord(id, color, filamentType, length);
    }
}

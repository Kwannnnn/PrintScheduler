package nl.saxion.expansion.model.io;

import nl.saxion.expansion.model.io.record.SpoolFileRecord;

public class SpoolCsvLoader extends AbstractCsvLoader<SpoolFileRecord> {

    public SpoolCsvLoader(String filename,
                          String delimiter) {
        super(filename, delimiter);
    }

    @Override
    protected SpoolFileRecord parseObject(String[] args) {
        int id = Integer.parseInt(args[0]);
        String color = args[1];
        String filamentType = args[2];
        double length = Double.parseDouble(args[3]);

        return new SpoolFileRecord(id, color, filamentType, length);
    }
}

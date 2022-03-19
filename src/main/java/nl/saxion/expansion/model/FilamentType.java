package nl.saxion.expansion.model;

public enum FilamentType {
    PLA(1,"PLA"),
    PETG(2,"PETG"),
    ABS(3,"ABS");

    private final int id;
    private final String name;

    FilamentType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return this.name;
    }
}

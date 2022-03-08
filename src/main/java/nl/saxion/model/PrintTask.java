package nl.saxion.model;

import java.util.List;

public class PrintTask {
    private Print print;
    private List<String> colors;
    private Filament filamentType;


    public PrintTask(Print print, List<String> colors, Filament filamentType){
        this.print = print;
        this.colors = colors;
        this.filamentType = filamentType;

    }

    public List<String> getColors() {
        return colors;
    }

    public Filament getFilamentType() {
        return filamentType;
    }

    public Print getPrint(){
        return print;
    }

    @Override
    public String toString() {
        return print.getName() +" " + filamentType + " " + colors.toString();
    }
}

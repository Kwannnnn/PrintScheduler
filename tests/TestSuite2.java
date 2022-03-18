import models.Printer;
import models.Spool;
import nl.saxion.Main;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class TestSuite2 {
    private final InputStream systemIn = System.in;
    private final PrintStream systemOut = System.out;

    private ByteArrayInputStream testIn;
    private ByteArrayOutputStream testOut;

    private OutputStrings outputStrings;


    @Before
    public void setUpOutput() {
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
    }

    @Before
    public void resetOutputStrings() {
        outputStrings = new OutputStrings();
        outputStrings.addPrinter(new Printer(6, "Red Dwarf", "Orion Prints", 300, 300, 300, -1,4, -1, -1, -1));
        outputStrings.addSpool(new Spool(21, "Blue", "PLA", 1000.0));
    }

    private void provideInput(String data) {
        testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }

    private String getOutput() {
        return testOut.toString();
    }

    @After
    public void restoreSystemInputOutput() {
        System.setIn(systemIn);
        System.setOut(systemOut);
    }

    /* New Tests */

    //The Multicolor House printer will be named Red Dwarf and will have to be added.
    @Test
    public void multicolorABSPrintAssignedToPrinter() {
        final String input = "1\n2\n3\n1\n2\n5\n7\n9\n0";
        provideInput(input);
        Main.main(new String[0]);

        outputStrings.setCurrentPrintOnPrinter(5, "House ABS [Blue, Red]");
        outputStrings.setCurrentSpoolOnPrinter(5, 8, 19, -1, -1);

        final String testString = outputStrings.menu() +
                outputStrings.addPrint("ABS", 2) +
                "Please place spool 8 in printer Red Dwarf" + System.lineSeparator() +
                "Please place spool 19 in printer Red Dwarf" + System.lineSeparator() +
                "Started task House ABS [Blue, Red] on printer Red Dwarf" + System.lineSeparator() +
                outputStrings.menu() +
                outputStrings.printers() +
                outputStrings.menu() +
                "--------- Pending Print Tasks ---------" + System.lineSeparator() +
                "--------------------------------------" +System.lineSeparator() +
                outputStrings.menu();

        assertEquals(testString, getOutput());
    }

    // Change Strategy, this test only proves the menu option can be selected.
    @Test
    public void ChangePrintingStrategy() {
        final String input = "4\n2\n0";
        provideInput(input);

        Main.main(new String[0]);

        final String testString = outputStrings.menu()
                + "Current strategy: Less Spool Changes" + System.lineSeparator() +
                "1: Less Spool Changes" + System.lineSeparator() +
                "2: Efficient Spool Usage" + System.lineSeparator() +
                "Choose strategy: " + System.lineSeparator() +
                outputStrings.menu();

        assertEquals(testString, getOutput());
    }

    // There is now a smaller blue spool which will be used.
    @Test
    public void SimpleChangePrintingStrategy() {
        final String input = "4\n2\n" +
                "1\n3\n1\n1\n" + // First add a frog
                "1\n1\n1\n1\n" + // then add the larger dog
                "5\n7" + // start queue and show dog was chosen
            "\n0";
        provideInput(input);

        outputStrings.setCurrentPrintOnPrinter(0, "Dog PLA [Blue]");
        outputStrings.setCurrentSpoolOnPrinter(0, 21);
        outputStrings.setCurrentPrintOnPrinter(1, "Frog PLA [Blue]");
        outputStrings.setCurrentSpoolOnPrinter(1, 1);

        Main.main(new String[0]);

        final String testString = outputStrings.menu()
                + "Current strategy: Less Spool Changes" + System.lineSeparator() +
                "1: Less Spool Changes" + System.lineSeparator() +
                "2: Efficient Spool Usage" + System.lineSeparator() +
                "Choose strategy: " + System.lineSeparator() +
                outputStrings.menu() +
                outputStrings.addPrint("PLA") +
                outputStrings.addPrint("PLA") +
                "Please place spool 21 in printer Enterprise" + System.lineSeparator() +
                "Started task Dog PLA [Blue] on printer Enterprise" + System.lineSeparator() +
                "Please place spool 1 in printer Serenity" + System.lineSeparator() +
                "Started task Frog PLA [Blue] on printer Serenity" + System.lineSeparator() +
                outputStrings.menu() +
                outputStrings.printers() +
                outputStrings.menu();

        assertEquals(testString, getOutput());
    }

    // Same Test except we also change the spool
    @Test
    public void SimpleChangePrintingStrategyWithSpoolChange() {
        final String input = "4\n2\n" +
                "1\n3\n1\n1\n" + // First add a frog
                "1\n1\n1\n2\n" + // then add the larger dog
                "5\n7\n" + // start queue and show dog was chosen
                "\n0";
        provideInput(input);

        outputStrings.setCurrentPrintOnPrinter(0, "Frog PLA [Blue]");
        outputStrings.setCurrentSpoolOnPrinter(0, 21);
        outputStrings.setCurrentPrintOnPrinter(1, "Dog PLA [Red]");
        outputStrings.setCurrentSpoolOnPrinter(1, 12);

        Main.main(new String[0]);

        final String testString = outputStrings.menu()
                + "Current strategy: Less Spool Changes" + System.lineSeparator() +
                "1: Less Spool Changes" + System.lineSeparator() +
                "2: Efficient Spool Usage" + System.lineSeparator() +
                "Choose strategy: " + System.lineSeparator() +
                outputStrings.menu() +
                outputStrings.addPrint("PLA") +
                outputStrings.addPrint("PLA") +
                "Please place spool 21 in printer Enterprise" + System.lineSeparator() +
                "Started task Frog PLA [Blue] on printer Enterprise" + System.lineSeparator() +
                "Please place spool 12 in printer Serenity" + System.lineSeparator() +
                "Started task Dog PLA [Red] on printer Serenity" + System.lineSeparator() +
                outputStrings.menu() +
                outputStrings.printers() +
                outputStrings.menu();

        assertEquals(testString, getOutput());
    }

    /* Original Tests Updated with new options */

    @Test
    public void StartExitProgram() {
        final String input = "0";
        provideInput(input);

        Main.main(new String[0]);

        final String testString = outputStrings.menu();

        assertEquals(testString, getOutput());
    }

    @Test
    public void DisplayPrints() {
        final String input = "6\n0";
        provideInput(input);

        Main.main(new String[0]);

        final String testString = outputStrings.menu() + outputStrings.prints() + System.lineSeparator() + outputStrings.menu();

        assertEquals(testString, getOutput());
    }

    @Test
    public void DisplayPrinters() {
        final String input = "7\n0";
        provideInput(input);

        Main.main(new String[0]);

        final String testString = outputStrings.menu() + outputStrings.printers() + outputStrings.menu();

        assertEquals(testString, getOutput());
    }

    @Test
    public void DisplaySpools() {
        final String input = "8\n0";
        provideInput(input);

        Main.main(new String[0]);

        final String testString = outputStrings.menu()
                + outputStrings.spools() + System.lineSeparator()
                + outputStrings.menu();

        assertEquals(testString, getOutput());
    }

    @Test
    public void AddSingleTaskToQueue() {
        final String input = "1\n1\n1\n1\n9\n0";
        provideInput(input);
        Main.main(new String[0]);

        final String testString = outputStrings.menu() +
                outputStrings.addPrint("PLA") +
                "--------- Pending Print Tasks ---------" + System.lineSeparator() +
                "Dog PLA [Blue]" + System.lineSeparator() +
                "--------------------------------------" +System.lineSeparator() +
                outputStrings.menu();

        assertEquals(testString, getOutput());
    }

    @Test
    public void AssignPrintToPrinter() {
        final String input = "1\n1\n1\n1\n5\n7\n9\n0";
        provideInput(input);
        Main.main(new String[0]);

        outputStrings.setCurrentPrintOnPrinter(0, "Dog PLA [Blue]");

        final String testString = outputStrings.menu() +
                outputStrings.addPrint("PLA") +
                "Started task Dog PLA [Blue] on printer Enterprise" + System.lineSeparator() +
                outputStrings.menu() +
                outputStrings.printers() +
                outputStrings.menu() +
                "--------- Pending Print Tasks ---------" + System.lineSeparator() +
                "--------------------------------------" +System.lineSeparator() +
                outputStrings.menu();

        assertEquals(testString, getOutput());
    }

    @Test
    public void AssignPrintFrogToPrinter() {
        final String input = "1\n3\n1\n1\n5\n7\n9\n0";
        provideInput(input);
        Main.main(new String[0]);

        outputStrings.setCurrentPrintOnPrinter(0, "Frog PLA [Blue]");

        final String testString = outputStrings.menu() +
                outputStrings.addPrint("PLA") +
                "Started task Frog PLA [Blue] on printer Enterprise" + System.lineSeparator() +
                outputStrings.menu() +
                outputStrings.printers() +
                outputStrings.menu() +
                "--------- Pending Print Tasks ---------" + System.lineSeparator() +
                "--------------------------------------" +System.lineSeparator() +
                outputStrings.menu();

        assertEquals(testString, getOutput());
    }

    @Test
    public void AssignPrintToPrinterFavorMatchingSpool() {
        final String input = "1\n1\n1\n2\n" + // Add the red color first.
                "1\n1\n1\n1" +
                "\n5\n7\n9\n0";
        provideInput(input);
        Main.main(new String[0]);

        outputStrings.setCurrentPrintOnPrinter(0, "Dog PLA [Blue]");
        outputStrings.setCurrentPrintOnPrinter(1, "Dog PLA [Red]"); // Print will of course be assigned to the next free printer.
        outputStrings.setCurrentSpoolOnPrinter(1, 12);

        final String testString = outputStrings.menu() +
                outputStrings.addPrint("PLA") +
                outputStrings.addPrint("PLA") +
                "Started task Dog PLA [Blue] on printer Enterprise" + System.lineSeparator() +
                "Please place spool 12 in printer Serenity" + System.lineSeparator() +
                "Started task Dog PLA [Red] on printer Serenity" + System.lineSeparator() +
                outputStrings.menu() +
                outputStrings.printers() +
                outputStrings.menu() +
                "--------- Pending Print Tasks ---------" + System.lineSeparator() +
                "--------------------------------------" +System.lineSeparator() +
                outputStrings.menu();

        assertEquals(testString, getOutput());
    }

    @Test
    public void AssignPrintToPrinterWithMultiplesNotMatchingSpool() {
        final String input = "1\n1\n1\n2\n" + // Add the red color first.
                "1\n1\n1\n2\n" +
                "1\n1\n2\n1\n" +
                "5\n7\n9\n0";
        provideInput(input);
        Main.main(new String[0]);

        outputStrings.setCurrentPrintOnPrinter(0, "Dog PLA [Red]");
        outputStrings.setCurrentPrintOnPrinter(1, "Dog PETG [Blue]"); // Print will of course be assigned to the next free printer.
        outputStrings.setCurrentSpoolOnPrinter(0, 12);
        outputStrings.setCurrentSpoolOnPrinter(1, 14);

        final String testString = outputStrings.menu() +
                outputStrings.addPrint("PLA") +
                outputStrings.addPrint("PLA") +
                outputStrings.addPrint("PETG") +
                "Please place spool 12 in printer Enterprise" + System.lineSeparator() +
                "Started task Dog PLA [Red] on printer Enterprise" + System.lineSeparator() +
                "Please place spool 14 in printer Serenity" + System.lineSeparator() +
                "Started task Dog PETG [Blue] on printer Serenity" + System.lineSeparator() +
                outputStrings.menu() +
                outputStrings.printers() +
                outputStrings.menu() +
                "--------- Pending Print Tasks ---------" + System.lineSeparator() +
                "Dog PLA [Red]" + System.lineSeparator() +
                "--------------------------------------" +System.lineSeparator() +
                outputStrings.menu();

        assertEquals(testString, getOutput());
    }

    @Test
    public void AssignMultiColorPrintToPrinter() {
        final String input = "1\n2\n2\n1\n2\n5\n7\n9\n0";
        provideInput(input);
        Main.main(new String[0]);

        String print = "House PETG [Blue, Red]";

        outputStrings.setCurrentPrintOnPrinter(2, print);

        final String testString = outputStrings.menu() +
                outputStrings.addPrint("PETG", 2) +
                "Started task "+print+" on printer Tardis" + System.lineSeparator() +
                outputStrings.menu() +
                outputStrings.printers() +
                outputStrings.menu() +
                "--------- Pending Print Tasks ---------" + System.lineSeparator() +
                "--------------------------------------" +System.lineSeparator() +
                outputStrings.menu();

        assertEquals(testString, getOutput());
    }

    @Test
    public void AssignLargePrintToPrinter() {
        final String input = "1\n5\n1\n1\n5\n7\n9\n0";
        provideInput(input);
        Main.main(new String[0]);

        String print = "Spaceship PLA [Blue]";

        outputStrings.setCurrentPrintOnPrinter(1, print);
        outputStrings.setCurrentSpoolOnPrinter(1, 11);

        final String testString = outputStrings.menu() +
                outputStrings.addPrint("PLA") +
                "Please place spool 11 in printer Serenity" + System.lineSeparator() +
                "Started task "+print+" on printer Serenity" + System.lineSeparator() +
                outputStrings.menu() +
                outputStrings.printers() +
                outputStrings.menu() +
                "--------- Pending Print Tasks ---------" + System.lineSeparator() +
                "--------------------------------------" +System.lineSeparator() +
                outputStrings.menu();

        assertEquals(testString, getOutput());
    }

    @Test
    public void AssignABSPrintToPrinter() {
        final String input = "1\n1\n3\n2\n5\n7\n9\n0";
        provideInput(input);
        Main.main(new String[0]);

        String print = "Dog ABS [Red]";

        outputStrings.setCurrentPrintOnPrinter(1, print);

        final String testString = outputStrings.menu() +
                outputStrings.addPrint("ABS") +
                "Started task "+print+" on printer Serenity" + System.lineSeparator() +
                outputStrings.menu() +
                outputStrings.printers() +
                outputStrings.menu() +
                "--------- Pending Print Tasks ---------" + System.lineSeparator() +
                "--------------------------------------" +System.lineSeparator() +
                outputStrings.menu();

        assertEquals(testString, getOutput());
    }

    @Test
    public void AssignPrintToPrinterAndChangeSpool() {
        final String input = "1\n1\n1\n2\n5\n7\n9\n0";
        provideInput(input);
        Main.main(new String[0]);

        outputStrings.setCurrentPrintOnPrinter(0, "Dog PLA [Red]");
        outputStrings.setCurrentSpoolOnPrinter(0, 12);

        final String testString = outputStrings.menu() +
                outputStrings.addPrint("PLA") +
                "Please place spool 12 in printer Enterprise" + System.lineSeparator() +
                "Started task Dog PLA [Red] on printer Enterprise" + System.lineSeparator() +
                outputStrings.menu() +
                outputStrings.printers() +
                outputStrings.menu() +
                "--------- Pending Print Tasks ---------" + System.lineSeparator() +
                "--------------------------------------" +System.lineSeparator() +
                outputStrings.menu();

        assertEquals(testString, getOutput());
    }

    @Test
    public void CompletedPrintReducesSpoolSize() {
        final String input = "1\n1\n1\n1\n5\n2\n1\n8\n0";
        provideInput(input);
        Main.main(new String[0]);

        outputStrings.setCurrentPrintOnPrinter(0, "Dog PLA [Blue]");

        outputStrings.reduceSpoolLength(0, 900.0);

        final String testString = outputStrings.menu() +
                outputStrings.addPrint("PLA") +
                "Started task Dog PLA [Blue] on printer Enterprise" + System.lineSeparator() +
                outputStrings.menu() +
                outputStrings.activePrinters(false) +
                "Task Dog PLA [Blue] removed from printer Enterprise" + System.lineSeparator() +
                outputStrings.menu() +
                outputStrings.spools() + System.lineSeparator() +
                outputStrings.menu();

        assertEquals(testString, getOutput());
    }

    @Test
    public void AssignPrintToEachPrinter() {
        final String input = "1\n1\n1\n1\n" +
                "1\n1\n3\n2\n" +
                "1\n2\n2\n1\n2\n" +
                "1\n5\n1\n3\n" +
                "1\n3\n1\n2\n" +
                "1\n2\n3\n1\n2\n" +
                "5\n7\n9\n0";
        provideInput(input);
        Main.main(new String[0]);

        outputStrings.setCurrentPrintOnPrinter(0, "Dog PLA [Blue]");
        outputStrings.setCurrentPrintOnPrinter(1, "Dog ABS [Red]");
        outputStrings.setCurrentPrintOnPrinter(2, "House PETG [Blue, Red]");
        outputStrings.setCurrentPrintOnPrinter(3, "Spaceship PLA [Green]");
        outputStrings.setCurrentPrintOnPrinter(4, "Frog PLA [Red]");
        outputStrings.setCurrentPrintOnPrinter(5, "House ABS [Blue, Red]");
        outputStrings.setCurrentSpoolOnPrinter(5, 8, 19, -1, -1);

        final String testString = outputStrings.menu() +
                outputStrings.addPrint("PLA") +
                outputStrings.addPrint("ABS") +
                outputStrings.addPrint("PETG", 2) +
                outputStrings.addPrint("PLA") +
                outputStrings.addPrint("PLA") +
                outputStrings.addPrint("ABS", 2) +
                "Started task Dog PLA [Blue] on printer Enterprise" + System.lineSeparator() +
                "Started task Dog ABS [Red] on printer Serenity" + System.lineSeparator() +
                "Started task House PETG [Blue, Red] on printer Tardis" + System.lineSeparator() +
                "Started task Spaceship PLA [Green] on printer Rocinante" + System.lineSeparator() +
                "Started task Frog PLA [Red] on printer Bebop" + System.lineSeparator() +
                "Please place spool 8 in printer Red Dwarf" + System.lineSeparator() +
                "Please place spool 19 in printer Red Dwarf" + System.lineSeparator() +
                "Started task House ABS [Blue, Red] on printer Red Dwarf" + System.lineSeparator() +
                outputStrings.menu() +
                outputStrings.printers() +
                outputStrings.menu() +
                "--------- Pending Print Tasks ---------" + System.lineSeparator() +
                "--------------------------------------" +System.lineSeparator() +
                outputStrings.menu();

        assertEquals(testString, getOutput());
    }


    @Test
    public void PrintCompletionAssignsNewPrintToPrinterWithSameSpool() {
        final String input = "1\n1\n1\n1\n" +
                "1\n1\n1\n1\n" +
                "1\n1\n1\n1\n" +
                "5\n2\n1\n0";
        provideInput(input);
        Main.main(new String[0]);

        String print = "Dog PLA [Blue]";

        outputStrings.setCurrentPrintOnPrinter(0, print);
        outputStrings.setCurrentPrintOnPrinter(1, print);
        outputStrings.setCurrentSpoolOnPrinter(1, 11);

        final String testString = outputStrings.menu() +
                outputStrings.addPrint("PLA") +
                outputStrings.addPrint("PLA") +
                outputStrings.addPrint("PLA") +
                "Started task "+print+" on printer Enterprise" + System.lineSeparator() +
                "Please place spool 11 in printer Serenity" + System.lineSeparator() +
                "Started task "+print+" on printer Serenity" + System.lineSeparator() +
                outputStrings.menu() +
                outputStrings.activePrinters(false) +
                "Task Dog PLA [Blue] removed from printer Enterprise" + System.lineSeparator() +
                "Started task Dog PLA [Blue] on printer Enterprise" + System.lineSeparator() +
                outputStrings.menu();

        assertEquals(testString, getOutput());
    }

    @Test
    public void PrintCompletionAssignsNewPrintToPrinterWithNewSpool() {
        final String input =
                "1\n1\n1\n1" +
                "\n1\n1\n1\n1" +
                "\n1\n1\n1\n1" +
                "\n1\n1\n1\n1" +
                "\n5\n2\n1\n0";
        provideInput(input);
        Main.main(new String[0]);

        String print = "Dog PLA [Blue]";

        outputStrings.setCurrentPrintOnPrinter(0, print);
        outputStrings.setCurrentPrintOnPrinter(1, print);
        outputStrings.setCurrentPrintOnPrinter(2, print);
        outputStrings.setCurrentSpoolOnPrinter(1, 11);
        outputStrings.setCurrentSpoolOnPrinter(2, 21);

        final String testString = outputStrings.menu() +
                outputStrings.addPrint("PLA") +
                outputStrings.addPrint("PLA") +
                outputStrings.addPrint("PLA") +
                outputStrings.addPrint("PLA") +
                "Started task "+print+" on printer Enterprise" + System.lineSeparator() +
                "Please place spool 11 in printer Serenity" + System.lineSeparator() +
                "Started task "+print+" on printer Serenity" + System.lineSeparator() +
                "Please place spool 21 in printer Tardis" + System.lineSeparator() +
                "Started task "+print+" on printer Tardis" + System.lineSeparator() +
                outputStrings.menu() +
                outputStrings.activePrinters(false) +
                "Task Dog PLA [Blue] removed from printer Enterprise" + System.lineSeparator() +
                "Started task Dog PLA [Blue] on printer Enterprise" + System.lineSeparator() +
                outputStrings.menu();

        assertEquals(testString, getOutput());
    }

    @Test
    public void FailedPrintRestartsSamePrint() {
        final String input = "1\n1\n1\n1\n5\n3\n1\n8\n0";
        provideInput(input);
        Main.main(new String[0]);

        outputStrings.setCurrentPrintOnPrinter(0, "Dog PLA [Blue]");

        outputStrings.reduceSpoolLength(0, 900.0);

        final String testString = outputStrings.menu() +
                outputStrings.addPrint("PLA") +
                "Started task Dog PLA [Blue] on printer Enterprise" + System.lineSeparator() +
                outputStrings.menu() +
                outputStrings.activePrinters(true) +
                "Task Dog PLA [Blue] removed from printer Enterprise" + System.lineSeparator() +
                "Started task Dog PLA [Blue] on printer Enterprise" + System.lineSeparator() +
                outputStrings.menu() +
                outputStrings.spools() + System.lineSeparator() +
                outputStrings.menu();

        assertEquals(testString, getOutput());
    }

    @Test
    public void DoubleFailedPrintStartsDifferentPrint() {
        final String input = "1\n1\n1\n2\n" +
                "1\n1\n3\n2\n" + // Fill all te printers.
                "1\n2\n2\n1\n2\n" +
                "1\n5\n1\n3\n" +
                "1\n3\n1\n2\n" +
                "1\n2\n3\n1\n2\n" + // Make sure Red Dwarf gets a print
                "1\n1\n1\n2\n" + // Add red another dog.
                "1\n3\n1\n2\n" + // Add a red Frog.
                 "5\n" + // start queue
                "3\n1\n" +
                "3\n1\n" +
                "9\n0"; // exit program
        provideInput(input);
        Main.main(new String[0]);

        outputStrings.setCurrentPrintOnPrinter(0, "Dog PLA [Red]");
        outputStrings.setCurrentPrintOnPrinter(1, "Dog ABS [Red]");
        outputStrings.setCurrentPrintOnPrinter(2, "House PETG [Blue, Red]");
        outputStrings.setCurrentPrintOnPrinter(3, "Spaceship PLA [Green]");
        outputStrings.setCurrentPrintOnPrinter(4, "Frog PLA [Red]");
        outputStrings.setCurrentPrintOnPrinter(5, "House ABS [Blue, Red]");
        outputStrings.setCurrentSpoolOnPrinter(5, 8, 19, -1, -1);

        final String testString = outputStrings.menu() +
                outputStrings.addPrint("PLA") +
                outputStrings.addPrint("ABS") +
                outputStrings.addPrint("PETG", 2) +
                outputStrings.addPrint("PLA") +
                outputStrings.addPrint("PLA") +
                outputStrings.addPrint("ABS", 2) +
                outputStrings.addPrint("PLA") +
                outputStrings.addPrint("PLA") +
                "Please place spool 12 in printer Enterprise" + System.lineSeparator() +
                "Started task Dog PLA [Red] on printer Enterprise" + System.lineSeparator() +
                "Started task Dog ABS [Red] on printer Serenity" + System.lineSeparator() +
                "Started task House PETG [Blue, Red] on printer Tardis" + System.lineSeparator() +
                "Started task Spaceship PLA [Green] on printer Rocinante" + System.lineSeparator() +
                "Started task Frog PLA [Red] on printer Bebop" + System.lineSeparator() +
                "Please place spool 8 in printer Red Dwarf" + System.lineSeparator() +
                "Please place spool 19 in printer Red Dwarf" + System.lineSeparator() +
                "Started task House ABS [Blue, Red] on printer Red Dwarf" + System.lineSeparator() +
                outputStrings.menu() +
                outputStrings.activePrinters(true) +
                "Task Dog PLA [Red] removed from printer Enterprise" + System.lineSeparator() +
                "Started task Dog PLA [Red] on printer Enterprise" + System.lineSeparator() +
                outputStrings.menu() +
                outputStrings.activePrinters(true) +
                "Task Dog PLA [Red] removed from printer Enterprise" + System.lineSeparator() +
                "Started task Frog PLA [Red] on printer Enterprise" + System.lineSeparator() +
                outputStrings.menu() +
                "--------- Pending Print Tasks ---------" + System.lineSeparator() +
                "Dog PLA [Red]" + System.lineSeparator() +
                "Dog PLA [Red]" + System.lineSeparator() +
                "--------------------------------------" + System.lineSeparator() +
                outputStrings.menu();


        assertEquals(testString, getOutput());
    }


}
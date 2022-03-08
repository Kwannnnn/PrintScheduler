import static org.junit.Assert.*;

import java.io.*;
import java.math.BigInteger;

import nl.saxion.Main;
import org.junit.*;

public class TestSuite {
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
                "5\n7\n9\n0";
        provideInput(input);
        Main.main(new String[0]);

        outputStrings.setCurrentPrintOnPrinter(0, "Dog PLA [Blue]");
        outputStrings.setCurrentPrintOnPrinter(1, "Dog ABS [Red]");
        outputStrings.setCurrentPrintOnPrinter(2, "House PETG [Blue, Red]");
        outputStrings.setCurrentPrintOnPrinter(3, "Spaceship PLA [Green]");
        outputStrings.setCurrentPrintOnPrinter(4, "Frog PLA [Red]");

        final String testString = outputStrings.menu() +
                outputStrings.addPrint("PLA") +
                outputStrings.addPrint("ABS") +
                outputStrings.addPrint("PETG", 2) +
                outputStrings.addPrint("PLA") +
                outputStrings.addPrint("PLA") +
                "Started task Dog PLA [Blue] on printer Enterprise" + System.lineSeparator() +
                "Started task Dog ABS [Red] on printer Serenity" + System.lineSeparator() +
                "Started task House PETG [Blue, Red] on printer Tardis" + System.lineSeparator() +
                "Started task Spaceship PLA [Green] on printer Rocinante" + System.lineSeparator() +
                "Started task Frog PLA [Red] on printer Bebop" + System.lineSeparator() +
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
                "\n5\n2\n1\n0";
        provideInput(input);
        Main.main(new String[0]);

        String print = "Dog PLA [Blue]";

        outputStrings.setCurrentPrintOnPrinter(0, print);
        outputStrings.setCurrentPrintOnPrinter(1, print);

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

    // TODO: Write test for print failure
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
        final String input = "1\n1\n1\n1\n" +
                "1\n1\n3\n2\n" + // Fill all te printers.
                "1\n2\n2\n1\n2\n" +
                "1\n5\n1\n3\n" +
                "1\n3\n1\n2\n" +
                "1\n1\n1\n1\n" + // Add another dog.
                "1\n3\n1\n1\n" + // Add a Frog.
                 "5\n" + // start queue
                "3\n1\n" +
                "3\n1\n" +
                "9\n0"; // exit program
        provideInput(input);
        Main.main(new String[0]);

        outputStrings.setCurrentPrintOnPrinter(0, "Dog PLA [Blue]");
        outputStrings.setCurrentPrintOnPrinter(1, "Dog ABS [Red]");
        outputStrings.setCurrentPrintOnPrinter(2, "House PETG [Blue, Red]");
        outputStrings.setCurrentPrintOnPrinter(3, "Spaceship PLA [Green]");
        outputStrings.setCurrentPrintOnPrinter(4, "Frog PLA [Red]");


        final String testString = outputStrings.menu() +
                outputStrings.addPrint("PLA") +
                outputStrings.addPrint("ABS") +
                outputStrings.addPrint("PETG", 2) +
                outputStrings.addPrint("PLA") +
                outputStrings.addPrint("PLA") +
                outputStrings.addPrint("PLA") +
                outputStrings.addPrint("PLA") +
                "Started task Dog PLA [Blue] on printer Enterprise" + System.lineSeparator() +
                "Started task Dog ABS [Red] on printer Serenity" + System.lineSeparator() +
                "Started task House PETG [Blue, Red] on printer Tardis" + System.lineSeparator() +
                "Started task Spaceship PLA [Green] on printer Rocinante" + System.lineSeparator() +
                "Started task Frog PLA [Red] on printer Bebop" + System.lineSeparator() +
                outputStrings.menu() +
                outputStrings.activePrinters(true) +
                "Task Dog PLA [Blue] removed from printer Enterprise" + System.lineSeparator() +
                "Started task Dog PLA [Blue] on printer Enterprise" + System.lineSeparator() +
                outputStrings.menu() +
                outputStrings.activePrinters(true) +
                "Task Dog PLA [Blue] removed from printer Enterprise" + System.lineSeparator() +
                "Started task Frog PLA [Blue] on printer Enterprise" + System.lineSeparator() +
                outputStrings.menu() +
                "--------- Pending Print Tasks ---------" + System.lineSeparator() +
                "Dog PLA [Blue]" + System.lineSeparator() +
                "Dog PLA [Blue]" + System.lineSeparator() +
                "--------------------------------------" + System.lineSeparator() +
                outputStrings.menu();


        assertEquals(testString, getOutput());
    }
}
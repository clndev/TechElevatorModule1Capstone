package com.techelevator;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class Item {


    // Using treeMaps because they allow for ordering
    // Use ONE map instead to store an Item, with all of these as properties
    private static File vendingMachineItems = new File("vendingmachine.csv");
    public static Map<String, String> mapOfItems = new TreeMap<>();
    public static Map<String, BigDecimal> mapOfItemPrices = new TreeMap<>();
    public static Map<String, String> mapOfItemNames = new TreeMap<>();
    public static Map<String, Integer> mapOfItemQuantities = new TreeMap<>();
    public static Map<String, String> mapOfItemTypes = new TreeMap<>();
    public static String[] arrayOfItems = new String[4];

    // This method uses a Scanner to read through vendingmachine.csv and return each line
    // as a String. The Strings have the pipe delimiter removed with .split() and are
    // then placed into an array. The array indexes are then used to populate the Maps
    // with the correct information. There's probably an easier way to do this without
    // having to use so many maps...

    public static void readSourceFile() {
        try (Scanner scanner = new Scanner(vendingMachineItems)) {
            while (scanner.hasNextLine()) {
                String lineOfSourceFile = scanner.nextLine();
                arrayOfItems = lineOfSourceFile.split("\\|");
                mapOfItems.put(arrayOfItems[0], lineOfSourceFile);
                mapOfItemNames.put(arrayOfItems[0], arrayOfItems[1]);
                mapOfItemPrices.put(arrayOfItems[0], new BigDecimal(arrayOfItems[2]));
                mapOfItemTypes.put(arrayOfItems[0], arrayOfItems[3]);
                mapOfItemQuantities.put(arrayOfItems[0], 5);
            }
            System.out.println(arrayOfItems);
        } catch (FileNotFoundException e) {
            System.out.println("That is not a valid file path. Please try again.");
        }
    }
}

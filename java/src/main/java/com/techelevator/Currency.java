package com.techelevator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Currency {

    // Instance Variables
    // I don't understand why they need to be static, but I know that I wasn't able to call
    // them in VendingMachineCLI unless they are static.
    // Using BigDecimal for the balance because it is more accurate than double or float
    // .setScale() allows us to determine the number of decimal places balance will have
    public static BigDecimal balance = new BigDecimal(0.00).setScale(2);
    private static int nickels;
    private static int dimes;
    private static int quarters;
    // Creating a variable for the balance as a double because you can't
    // use arithmetic expressions on a BigDecimal
    private static double balanceAsDouble;
    private static BigDecimal previousBalance = balance;
    private static int numberOfItemsSold = 0;

    // Getter for balance
    public static BigDecimal getBalance() {
        return Currency.balance;
    }

    // Methods
    // Selecting "(1) Feed Money" allows the customer to repeatedly feed
    // money into the machine in valid, whole dollar amounts—for example, \$1, \$2, \$5, or \$10
    // After determining the amount, need to write a line to the Log.txt file
    public static void feedMoney(String amount) {
        if (amount.equals("$1")) {
            balance = balance.add(new BigDecimal(1.00));
        }
        else if (amount.equals("$2")) {
            balance = balance.add(new BigDecimal(2.00));
        }
        else if (amount.equals("$5")) {
            balance = balance.add(new BigDecimal(5.00));
        }
        else if (amount.equals("$10")) {
            balance = balance.add(new BigDecimal(10.00));
        }
        addToLog("FEED MONEY:", previousBalance, balance);
        System.out.println("Your balance is: $" + balance);
    }

    // First, we need to get the local date and time using LocalDateTime.now()
    // Next, we use DateTimeFormatter.ofPattern() to format the local date and time to match
    // what is in the ReadMe example.
    // Then, we instantiate a string and assign it the value of our newly formatted date and time.
    // Finally, we use the PrintWriter class to add a new line to Log.txt containing the date and
    // time, the transaction type, the previous balance, and the current balance.
    public static void addToLog(String transaction, BigDecimal lastBalance, BigDecimal currentBalance) {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a");
        String formattedDateTime = dateTime.format(formatter);
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(new File("Log.txt"),
                                            true))) {
        writer.println(formattedDateTime + " " + transaction + " " + previousBalance + " "
                        + currentBalance);
        }
        catch (FileNotFoundException e) {
            System.out.println("Sorry, that file does not exist. Please try a different file path.");
        }
    }

    // This method will be used to give the customer their change
    // The change will be given in quarters, dimes, and nickels using the smallest
    // possible number of coins
    // Update the system's balance to $0.00 remaining
    public static void giveChange(BigDecimal balance) {
        BigDecimal previousBalance = balance;
        balanceAsDouble = (balance.doubleValue()) * 100;
        quarters = (int)(balanceAsDouble/25);
        balanceAsDouble -= quarters * 25;
        dimes = (int)(balanceAsDouble/10);
        balanceAsDouble -= dimes * 10;
        nickels = (int)(balanceAsDouble/5);
        balanceAsDouble -= nickels * 5;
        Currency.balance = balance.multiply(new BigDecimal(0.00));
        addToLog("GIVE CHANGE:", previousBalance, balance);
        previousBalance = previousBalance.multiply(new BigDecimal(0.00));
        System.out.println("Your change is: " + quarters + " quarter(s), " + dimes + " dime(s), "
                            + nickels + " nickel(s).");
        System.out.println("BALANCE: " + Currency.balance);

    }

    public static void purchaseItem(String itemSlotLocation) {
        // Check if itemSlotLocation matches a key in the mapOfItems
        // Check if the item is in stock
        // Check if balance is greater than the cost of the item
        // If all of the above apply, we would:
        // Adjust balance, adjust item stock, print out sound for corresponding item type
        /**
         * TODO - Maybe use switch statements instead of if/else?
         */
        if ( Item.mapOfItemQuantities.containsKey(itemSlotLocation) ) {
            if ( Item.mapOfItemQuantities.get(itemSlotLocation) == 0 ) {
                System.out.println("Sorry, that item is out of stock");
                System.out.println("Please make another selection");
            } else if ( balance.compareTo(Item.mapOfItemPrices.get(itemSlotLocation)) == -1 ) {
                System.out.println("Please insert more cash.");
            } else if ( balance.compareTo(Item.mapOfItemPrices.get(itemSlotLocation)) == 1 ||
                    balance.compareTo(Item.mapOfItemPrices.get(itemSlotLocation)) == 0) {
                BigDecimal oldBalance = balance;
                balance = balance.subtract(Item.mapOfItemPrices.get(itemSlotLocation));
                Item.mapOfItemQuantities.put( itemSlotLocation, Item.mapOfItemQuantities.get
                                            (itemSlotLocation) - 1 );
                addToLog(Item.mapOfItemNames.get(itemSlotLocation), oldBalance, balance);
                System.out.println("You purchased: " + Item.mapOfItemNames.get(itemSlotLocation) +
                                    " for $" + Item.mapOfItemPrices.get(itemSlotLocation) + "\n" +
                                    "Your balance is: $" + balance);
                if (itemSlotLocation.contains("A")) {
                    Chip.getSoundWhenDispensed();
                } else if (itemSlotLocation.contains("B")) {
                    Candy.getSoundWhenDispensed();
                } else if (itemSlotLocation.contains("C")) {
                    Drink.getSoundWhenDispensed();
                } else if (itemSlotLocation.contains("D")){
                    Gum.getSoundWhenDispensed();
                }
            }
        }
     if (!Item.mapOfItems.containsKey(itemSlotLocation)) {
         System.out.println("That code is not valid. Please enter a valid code.");
        }
    }

    public static void getSalesReport() {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a");
        String formattedDateTime = dateTime.format(formatter);
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(new File("SalesReport.txt"),
                false))) {
            writer.println(formattedDateTime);
            writer.println(Item.mapOfItems + "|" + numberOfItemsSold);
            writer.println();
            writer.println("TOTAL SALES: $");
        }
        catch (FileNotFoundException e) {
            System.out.println("Sorry, that file does not exist. Please try a different file path.");
        }
    }
}

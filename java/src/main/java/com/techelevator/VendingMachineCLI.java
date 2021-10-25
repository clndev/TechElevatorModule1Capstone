package com.techelevator;

import com.techelevator.view.Menu;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class VendingMachineCLI {

	private static final String MAIN_MENU_OPTION_DISPLAY_ITEMS = "Display Vending Machine Items";
	private static final String MAIN_MENU_OPTION_PURCHASE = "Purchase";
	private static final String MAIN_MENU_OPTION_EXIT = "Exit";
	private static final String[] MAIN_MENU_OPTIONS = {MAIN_MENU_OPTION_DISPLAY_ITEMS,
			MAIN_MENU_OPTION_PURCHASE, MAIN_MENU_OPTION_EXIT};

	private static final String PURCHASE_MENU_OPTION_FEED_MONEY = "Feed Money";
	private static final String PURCHASE_MENU_OPTION_SELECT_PRODUCT = "Select Product";
	private static final String PURCHASE_MENU_OPTION_FINISH_TRANSACTION = "Finish Transaction";
	private static final String[] PURCHASE_MENU_OPTIONS = {PURCHASE_MENU_OPTION_FEED_MONEY,
						PURCHASE_MENU_OPTION_SELECT_PRODUCT, PURCHASE_MENU_OPTION_FINISH_TRANSACTION};
	private static final String[] FEED_AMOUNT_OPTIONS = {"$1", "$2", "$5", "$10", "Exit"};
	private static final String[] RETURN = {"Return"};

	private List<String> vendingMachineInventory = new ArrayList<>();
	private Menu menu;

	public VendingMachineCLI(Menu menu) {
		this.menu = menu;
	}

	public void run() throws FileNotFoundException {

		while (true) {
			String choice = (String) menu.getChoiceFromOptions(MAIN_MENU_OPTIONS);

			if (choice.equals(MAIN_MENU_OPTION_DISPLAY_ITEMS)) {
				displayItems();
			} else if (choice.equals(MAIN_MENU_OPTION_PURCHASE)) {
				purchaseMainMenu();
			} else if (choice.equals(MAIN_MENU_OPTION_EXIT)) {
				// I previously had return; in here, but that was only working if you pressed 3) Exit
				// before making a purchase.
				// After the user presses 3) Finish Transaction in the purchase menu, they are
				// automatically sent back to this main menu.
				// When the code below said return; if the user were to press 3)
				// it would just send the user back to the purchase menu, and it would just go on and
				// on without providing a way to actually exit the program.
				System.exit(0);
			}
		}
	}

	private void displayItems() throws FileNotFoundException {
		File vendingMachineItems = new File("vendingmachine.csv");
		try (Scanner scanner = new Scanner(vendingMachineItems)) {
			while (scanner.hasNextLine()) {
				String lineOfSourceFile = scanner.nextLine();
				vendingMachineInventory.add(lineOfSourceFile);
			}
			for (String string : vendingMachineInventory) {
				System.out.println(string);
			}

		} catch (FileNotFoundException e) {}
		String choice = (String) menu.getChoiceFromOptions(RETURN);
	}

	private void purchaseMainMenu() throws FileNotFoundException {
		while (true) {
			String choice = (String) menu.getChoiceFromOptions(PURCHASE_MENU_OPTIONS);

			if (choice.equals(PURCHASE_MENU_OPTION_FEED_MONEY)) {
				// Allows the customer to repeatedly feed money into the machine in valid, whole
				// dollar amounts—for example, \$1, \$2, \$5, or \$10
				// Display the balance as how much money the customer has fed into the machine
				feedMoneyIntoMachine();

			} else if (choice.equals(PURCHASE_MENU_OPTION_SELECT_PRODUCT)) {
				// Display list of available items
				// Allow customer to enter code to select item
				// If the code does not exist, the customer is informed and returned to the Purchase menu
				// If a product is sold out, the customer is informed and returned to the Purchase menu
				// If a valid product is selected, it is dispensed to the customer
				// Dispensing a product prints the name, cost, remaining balance,
				// and getSoundWhenDispensed()
				// After the product is dispensed, the machine must update its balance accordingly
				// and return the customer to the Purchase menu.
				selectProductFromMachine();

			} else if (choice.equals(PURCHASE_MENU_OPTION_FINISH_TRANSACTION)) {
				// return change to customer using nickels, dimes, and quarters
				// update machine balance to $0
				// return customer to main menu
				Currency.giveChange(Currency.getBalance());
				run();
			}
		}
	}

	private void feedMoneyIntoMachine() {
		String amountSelected = "";
		while ( !amountSelected.contentEquals("Exit") ) {
			amountSelected = (String) menu.getChoiceFromOptions(FEED_AMOUNT_OPTIONS);
			System.out.println(amountSelected);
			Currency.feedMoney(amountSelected);
		}
	}

	private void selectProductFromMachine() throws FileNotFoundException {

		Item.readSourceFile();
		Set<String> keysFromMapOfItems = Item.mapOfItems.keySet();
		for (String key : keysFromMapOfItems) {
			System.out.println(Item.mapOfItems.get(key) + "|" + Item.mapOfItemQuantities.get(
												   key) + " Left");
		}
		System.out.println();
		System.out.println("Please select an item. To return to the previous screen, press 1.");
		Scanner userInput = new Scanner(System.in);
		String itemSlotLocation = userInput.nextLine();
		if (itemSlotLocation.equals("1")) {
			purchaseMainMenu();
		}
		else {
			Currency.purchaseItem(itemSlotLocation);
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
		Menu menu = new Menu(System.in, System.out);
		VendingMachineCLI cli = new VendingMachineCLI(menu);
		cli.run();
	}
}

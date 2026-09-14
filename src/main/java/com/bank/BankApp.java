package com.bank;

import java.util.Scanner;

/**
 * BankApp class contains the main method to run the console banking application.
 * It interacts with the user using a Scanner and presents an interactive menu.
 */
public class BankApp {
    public static void main(String[] args) {
        // Initialize Scanner to read input from console
        Scanner scanner = new Scanner(System.in);

        System.out.println("==========================================");
        System.out.println("       WELCOME TO SIMPLE BANK APP         ");
        System.out.println("==========================================");

        // Prompt user for account number and holder name
        System.out.print("Enter Account Number: ");
        String accountNumber = scanner.nextLine().trim();

        System.out.print("Enter Account Holder Name: ");
        String accountHolder = scanner.nextLine().trim();

        // Create a new BankAccount instance
        BankAccount account = new BankAccount(accountNumber, accountHolder);
        System.out.println("\nAccount successfully created for " + accountHolder + "!");

        boolean isRunning = true;

        // Loop to allow repeated banking operations until user chooses to exit
        while (isRunning) {
            // Display menu
            System.out.println("\n------------------------------------------");
            System.out.println("                BANK MENU                 ");
            System.out.println("------------------------------------------");
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Check Balance");
            System.out.println("4. Exit");
            System.out.print("Enter your choice (1-4): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    // Deposit operation
                    System.out.print("Enter amount to deposit: ");
                    try {
                        double depositAmount = Double.parseDouble(scanner.nextLine().trim());
                        account.deposit(depositAmount);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input! Please enter a valid numerical amount.");
                    }
                    break;

                case "2":
                    // Withdraw operation
                    System.out.print("Enter amount to withdraw: ");
                    try {
                        double withdrawAmount = Double.parseDouble(scanner.nextLine().trim());
                        account.withdraw(withdrawAmount);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input! Please enter a valid numerical amount.");
                    }
                    break;

                case "3":
                    // Check balance operation
                    account.checkBalance();
                    break;

                case "4":
                    // Exit application
                    System.out.println("\nThank you for banking with us. Have a great day!");
                    isRunning = false;
                    break;

                default:
                    // Handle invalid menu selections
                    System.out.println("Invalid choice! Please choose a valid option (1-4).");
                    break;
            }
        }

        // Close the scanner resource
        scanner.close();
    }
}
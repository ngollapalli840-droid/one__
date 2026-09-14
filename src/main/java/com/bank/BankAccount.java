package com.bank;

/**
 * BankAccount class represents a simple bank account.
 * It holds account information and provides methods for deposit,
 * withdrawal, and checking the current balance.
 */
public class BankAccount {
    // Member variables / fields
    private String accountNumber;
    private String accountHolder;
    private double balance;

    // Constructor to initialize an account with number, holder name, and initial balance
    public BankAccount(String accountNumber, String accountHolder, double initialBalance) {
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.balance = initialBalance;
    }

    // Overloaded constructor with default initial balance of 0.0
    public BankAccount(String accountNumber, String accountHolder) {
        this(accountNumber, accountHolder, 0.0);
    }

    // Method to deposit money into the account
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("Deposit successful! Deposited: $" + String.format("%.2f", amount));
            System.out.println("Updated Balance: $" + String.format("%.2f", balance));
        } else {
            System.out.println("Invalid deposit amount! Amount must be greater than 0.");
        }
    }

    // Method to withdraw money from the account
    public void withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Invalid withdrawal amount! Amount must be greater than 0.");
        } else if (amount > balance) {
            // Prevent withdrawal when balance is insufficient
            System.out.println("Insufficient balance! Withdrawal failed.");
            System.out.println("Current Balance: $" + String.format("%.2f", balance));
        } else {
            balance -= amount;
            System.out.println("Withdrawal successful! Withdrawn: $" + String.format("%.2f", amount));
            System.out.println("Updated Balance: $" + String.format("%.2f", balance));
        }
    }

    // Method to check and display the current balance and account info
    public void checkBalance() {
        System.out.println("\n--- Account Details ---");
        System.out.println("Account Number : " + accountNumber);
        System.out.println("Account Holder : " + accountHolder);
        System.out.println("Current Balance: $" + String.format("%.2f", balance));
    }

    // Getter methods
    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public double getBalance() {
        return balance;
    }
}
package com.study.app;

// ==========================================
// 1. DESIGN CLASS (Encapsulated Entity)
// ==========================================
class BankAccount {
    // Instance Variables (private for encapsulation)
    private int accountNumber;
    private String holderName;
    private double balance;

    // --- Constructor Overloading & Constructor Chaining ---
    // No-arg constructor
    public BankAccount() {
        this(0, "Unknown", 0.0); // Constructor chaining using this(...)
    }

    // 2-parameter constructor
    public BankAccount(int accountNumber, String holderName) {
        this(accountNumber, holderName, 0.0); // Chaining to the 3-arg constructor
    }

    // Master parameterized constructor
    public BankAccount(int accountNumber, String holderName, double balance) {
        // Variable shadowing resolved via this.
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        setBalance(balance); // Use setter to enforce validation invariant
    }

    // --- Encapsulation: Getters and Setters with Validation ---
    public int getAccountNumber() {
        return this.accountNumber;
    }

    public String getHolderName() {
        return this.holderName;
    }

    public void setHolderName(String holderName) {
        if (holderName != null && !holderName.trim().isEmpty()) {
            this.holderName = holderName;
        }
    }

    public double getBalance() {
        return this.balance;
    }

    public void setBalance(double balance) {
        if (balance >= 0) {
            this.balance = balance;
        } else {
            System.out.println("[Error] Balance cannot be negative! Rejected: " + balance);
        }
    }

    // --- Instance Methods ---
    public void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
        }
    }

    // --- Method Overloading (deposit with different parameters) ---
    // Overload 1: Deposit with currency conversion fee
    public void deposit(double amount, double fee) {
        if (amount > fee && fee >= 0) {
            this.balance += (amount - fee);
        }
    }

    // Overload 2: Deposit using check number note
    public void deposit(String checkNumber, double amount) {
        System.out.println("Processing check #" + checkNumber);
        deposit(amount); // Reusing logic
    }

    public void displayAccount() {
        System.out.println("Acc #" + accountNumber + " | Holder: " + holderName + " | Balance: $" + balance);
    }
}

// ==========================================
// 2. DRIVER / TESTER CLASS
// ==========================================
public class Main {

    // Demonstrates pass-by-value reference mutation vs reassignment
    public static void testReferencePassing(BankAccount accountRef, BankAccount anotherRef) {
        // 1. MUTATION: Changes the actual heap object state
        accountRef.setBalance(9999.0);

        // 2. REASSIGNMENT: Only points the local parameter variable to a new object.
        // Caller's original reference remains completely untouched!
        anotherRef = new BankAccount(999, "Ghost User", 50000.0);
    }

    public static void main(String[] args) {
        System.out.println("=== CSE 111 OOP Demonstration ===");

        System.out.println("\n--- 1. Constructors & Chaining ---");
        BankAccount acc1 = new BankAccount();
        BankAccount acc2 = new BankAccount(101, "Alice");
        BankAccount acc3 = new BankAccount(102, "Bob", 1500.0);

        acc1.displayAccount();
        acc2.displayAccount();
        acc3.displayAccount();

        System.out.println("\n--- 2. Encapsulation & Validation ---");
        acc2.setBalance(-200.0); // Rejected by validation rule
        acc2.setBalance(350.0);  // Accepted
        acc2.displayAccount();

        System.out.println("\n--- 3. Method Overloading ---");
        acc3.deposit(500.0);                  // standard deposit
        acc3.deposit(200.0, 15.0);            // overloaded: amount with fee
        acc3.deposit("CHK-7890", 300.0);      // overloaded: check + amount
        acc3.displayAccount();

        System.out.println("\n--- 4. Reference Passing & Aliasing ---");
        BankAccount orig1 = new BankAccount(201, "Charlie", 100.0);
        BankAccount orig2 = new BankAccount(202, "Dave", 200.0);

        System.out.println("Before method call:");
        orig1.displayAccount();
        orig2.displayAccount();

        testReferencePassing(orig1, orig2);

        System.out.println("After method call:");
        orig1.displayAccount(); // Balance mutated to $9999.0!
        orig2.displayAccount(); // Dave is still Dave with $200.0! (Reassignment had zero effect)

        System.out.println("\n--- 5. Aliasing Demonstration ---");
        BankAccount alias = orig1; // Both hold the exact same memory address
        alias.setHolderName("Charlie-Modified-Via-Alias");
        System.out.println("orig1 after alias mutation: " + orig1.getHolderName());
    }
}

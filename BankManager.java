/*Write a Java program to create a class called "Bank" with a collection of accounts and methods to add and remove accounts, and to deposit and withdraw money. Also define a class called "Account" to maintain account details of a particular customer*/

import java.util.*;
import java.io.*;

class Account {
    private double amount;
    private String name;
    private int accountNumber;
    private static int ac = 1;

    public Account(String name, double initialAmount) {
        this.name = name;
        this.amount = initialAmount;  // Set the initial amount entered by the user
        this.accountNumber = ac++;
    }

    public void withdrawal(double amt) {
        if (amt > amount) {
            System.out.println("Amount entered is greater than balance");
        } else {
            amount -= amt;
            System.out.println("Withdrawal Successful");
        }
    }

    public void deposition(double amt) {
        amount += amt;
        System.out.println("Deposition Successful");
    }

    public void accountDetails() {
        System.out.println("Account Holder: " + name + " Account Number: " + accountNumber + " Balance: " + amount);
    }

    public int getAccountNumber() {
        return accountNumber;
    }
}

class Bank {
    private ArrayList<Account> account;

    public Bank() {
        account = new ArrayList<Account>();
    }

    public void addAccount(Account a) {
        account.add(a);
    }

    public void removeAccount(Account a) {
        account.remove(a);
    }

    public void depositMoney(Account a, double amt) {
        a.deposition(amt);
    }

    public void withdrawMoney(Account a, double amt) {
        a.withdrawal(amt);
    }

    public void getAccountDetails(Account a) {
        a.accountDetails();
    }

    public ArrayList<Account> getAccounts() {
        return account;
    }
}

class BankManager {
    public static void main(String ar[]) {
        Scanner sc = new Scanner(System.in);
        Console console = System.console();

        int op, i = 0, tn;
        double amt, initialAmount;
        String name;

        Bank b = new Bank();
        Account[] account = new Account[10];

        while (true) {

            System.out.println("Enter the operation to be performed\n1.Create Account\n2.Withdraw Money\n3.Deposit Money\n4.Get Account Information\n5.Remove Account\n6.Exit ATM");
            op = sc.nextInt();

            switch (op) {
                case 1:
                    System.out.println("Enter your name\t");
		    name=sc.next();
                    System.out.println("Enter the initial amount for your account: ");
                    initialAmount = sc.nextDouble();  // Ask user for the initial deposit amount
                    account[i] = new Account(name, initialAmount);
                    b.addAccount(account[i]);
                    System.out.println("Account Created Successfully! Your Account Number: " + account[i].getAccountNumber() + "\n\n");
                    i++;
                    break;
                case 2:
                    System.out.println("Enter your account number");
                    tn = sc.nextInt();
                    System.out.println("Enter the amount to be withdrawn");
                    amt = sc.nextDouble();
                    b.withdrawMoney(account[tn - 1], amt);
                    System.out.println("Money Withdrawn!!\n\n");
                    break;
                case 3:
                    System.out.println("Enter your account number");
                    tn = sc.nextInt();
                    System.out.println("Enter the amount to be deposited");
                    amt = sc.nextDouble();
                    b.depositMoney(account[tn - 1], amt);
                    System.out.println("Money Deposited!!\n\n");
                    break;
                case 4:
                    System.out.println("Enter your account number");
                    tn = sc.nextInt();
                    b.getAccountDetails(account[tn - 1]);
                    System.out.println("\n\n");
                    break;
                case 5:
                    System.out.println("Enter your account number");
                    tn = sc.nextInt();
                    b.removeAccount(account[tn - 1]);
                    System.out.println("Account Removed\n\n");
                    break;
                case 6:
                    System.exit(0);
                default:
                    System.out.println("Enter right choice");
            }
        }
    }
}

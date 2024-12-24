package com.kshrd.Model;

import java.util.Scanner;
import java.util.regex.Pattern;

public class Validate {
    public static final String red = "\u001B[31m";
    public static final String green = "\u001B[32m";
    public static final String yellow = "\u001B[33m";

    public static final String blue = "\u001B[34m";
    public static final String reset = "\u001B[0m";
    String id, name, price, quantity, choice;
    Scanner sc = new Scanner(System.in);

    int inputID(String label) {
        Scanner sc = new Scanner(System.in);
        String value;
        while (true) {
            System.out.print(label);
            value = sc.next();
            boolean isValidValue = Pattern.matches("\\d+", value);
            if (!isValidValue)
                System.out.println(red + "You cannot input text here !" + reset);
            else
                return Integer.parseInt(value);
        }
    }

    public String validateId(String label) {
        while (true) {
            System.out.print(label);
            id = sc.nextLine();
            boolean vValue = Pattern.matches("^[0-9]+$", id);
            if (vValue)
                break;
            else
                System.out.println(red + "Invalid input. Allow input only numbers." + reset);
        }
        return id;
    }

    public String validateString(String label) {
        while (true) {
            System.out.print(label);
            name = sc.nextLine();
            boolean vValue = Pattern.matches("^[a-zA-Z ]*$", name);
            if (vValue)
                break;
            else
                System.out.println(red + "Invalid input. Allow input only letters." + reset);
        }
        return name;
    }

    public double validatePrice() {
        while (true) {
            System.out.print("Enter price: ");
            price = sc.nextLine();
            boolean vValue = Pattern.matches("^[0-9]+(\\.[0-9][0-9])?$", price);
            if (vValue)
                break;
            else
                System.out.println(red + "Invalid input. Allow input only numbers." + reset);
        }
        return Double.parseDouble(price);
    }

    public int validateQuantity() {
        while (true) {
            System.out.print("Enter quantity: ");
            quantity = sc.nextLine();
            boolean vValue = Pattern.matches("^[0-9]+$", quantity);
            if (vValue)
                break;
            else
                System.out.println(red + "Invalid input. Allow input only numbers." + reset);
        }
        return Integer.parseInt(quantity);
    }

    public String validateChoice() {
        while (true) {
            System.out.print(yellow + "=> Enter your choice: " + reset);
            choice = sc.nextLine();
            boolean vValue = Pattern.matches("\\d+", choice);
            if (vValue) {
                if (Integer.parseInt(choice) >= 0 && Integer.parseInt(choice) < 3)
                    break;
                else
                    System.out.println(red + "Out of choice. Enter number between [0-2]." + reset);
            } else
                System.out.println(red + "Invalid input. Allow input only letters." + reset);
        }
        return choice;
    }
}

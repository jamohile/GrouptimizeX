package ca.mohile;

import java.io.*;
import java.nio.file.FileSystem;
import java.util.*;

import jdk.nashorn.internal.ir.debug.JSONWriter;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Main {

    public static void main(String[] args) {

        Iteration iteration = new Iteration();
        iteration.configureFromJSONFile("sample2.json");
        iteration.optimize(3, 2);
    }

    /*
    Function to read in a state from
    CMD.
     */
    static void configureFromCMD() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("How many people?: ");
        int numPeople = scanner.nextInt();
        System.out.println("Okay, ready for " + numPeople + " people.");

        /*
        Get Names, create people.
         */
        for (int x = 1; x <= numPeople; x++) {
            System.out.println("What is person " + x + "'s name?: ");
            String name = scanner.next();
            Person.add(new Person(name));
        }
        /*
        get number of choices per person
         */
        System.out.println("How many choices per person?: ");
        int numChoices = scanner.nextInt();

        for (Person person : Person.people) {
            for (int choice = 0; choice < numChoices; choice++) {
                System.out.println("Choice " + (choice + 1) + " for " + person.name + ": ");
                String choiceName;
                do {
                    choiceName = scanner.next();
                } while (Person.getByName(choiceName) == null || choiceName.equals(person.name));
                Person choicePerson = Person.getByName(choiceName);
                person.addChoice(choicePerson, numChoices - choice);
            }
        }
        Person.calculateLikabilities();
        System.out.println("Awesome, grouptimize is ready to go.\n\n\n");

    }
}



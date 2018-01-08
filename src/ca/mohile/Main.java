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

       configureFromJSONFile("sample2.json");

       System.out.println(Person.likabilities);

        //configureFromCMD();

        /*
        Right now...just run every algorithm we have :).
         */
        TopDownIteration topDownIteration = new TopDownIteration(Person.people);
        topDownIteration.run();

        BottomUpIteration bottomUpIteration = new BottomUpIteration(Person.people);
        bottomUpIteration.run();

        EquivocationIteration equivocationIteration = new EquivocationIteration(Person.people);
        equivocationIteration.run();

        MiddleIteration middleIteration = new MiddleIteration(Person.people);
        middleIteration.run();

        SwingIteration swingIteration = new SwingIteration(Person.people);
        swingIteration.run();

        SwingIteration invertedSwingIteration = new SwingIteration(Person.people, true);
        invertedSwingIteration.run();

        System.out.println(topDownIteration);
        System.out.println(bottomUpIteration);
        System.out.println(equivocationIteration);
        System.out.println(middleIteration);
        System.out.println(swingIteration);
        System.out.println(invertedSwingIteration);
    }

    /*
    Takes a properly formatted JSON file
    and builds a network.
    WARN: NO SYNTAX CHECKING.
     */
    static void configureFromJSONFile(String fileName) {
        String file = "";
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(fileName));

            JSONArray people = (JSONArray) jsonObject.get("people");
            Iterator<JSONObject> peopleiterator = people.iterator();
            /*
            Set names.
             */
            while (peopleiterator.hasNext()) {
                JSONObject person = peopleiterator.next();
                Person.add(new Person((String) person.get("name")));
            }
            /*
            Set choices.
             */
            peopleiterator = people.iterator();
            while (peopleiterator.hasNext()) {
                JSONObject jsonPerson = peopleiterator.next();
                Person person = Person.getByName((String) jsonPerson.get("name"));

                JSONArray jsonChoices = (JSONArray) jsonPerson.get("choices");
                Iterator choiceIterator = jsonChoices.iterator();
                while (choiceIterator.hasNext()) {
                    JSONObject jsonChoice = (JSONObject) choiceIterator.next();
                    Person choice = Person.getByName((String) jsonChoice.get("name"));
                    int value = Math.toIntExact((Long) jsonChoice.get("value"));
                    person.addChoice(choice, value);
                }
            }
            Person.calculateLikabilities();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    /*
    Function to read in a state from
    CMD.
     */
    static void configureFromCMD(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("How many people?: ");
        int numPeople = scanner.nextInt();
        System.out.println("Okay, ready for " + numPeople + " people.");

        /*
        Get Names, create people.
         */
        for(int x = 1; x <= numPeople; x++){
            System.out.println("What is person " + x + "'s name?: ");
            String name = scanner.next();
            Person.add(new Person(name));
        }
        /*
        get number of choices per person
         */
        System.out.println("How many choices per person?: ");
        int numChoices = scanner.nextInt();

        for(Person person: Person.people){
            for(int choice = 0; choice < numChoices; choice ++){
                System.out.println("Choice " + (choice + 1) + " for " + person.name + ": ");
                String choiceName;
                do{
                    choiceName = scanner.next();
                }while(Person.getByName(choiceName) == null || choiceName.equals(person.name));
                Person choicePerson = Person.getByName(choiceName);
                person.addChoice(choicePerson, numChoices - choice);
            }
        }
        Person.calculateLikabilities();
        System.out.println("Awesome, grouptimize is ready to go.\n\n\n");

    }
}



package ca.mohile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

class Person implements Comparable<Person> {
    static ArrayList<Person> people = new ArrayList<>();
    static HashMap<Person, Integer> likabilities = new HashMap<>();
    static HashMap<String, Person> peopleByName = new HashMap<>();

    static BTS.BTSEval<Person> BTSEvaluator = person -> person.getLikability();

    static int id_counter = 0;

    String name;
    int id;
    /*
    Choices stores choices where
    key is strength of choice:
        0 - Lowest
     */
    HashMap<Person, Integer> choices;

    public Person(String name) {
        this.name = name;
        choices = new HashMap<>();
        likabilities.put(this, 0);
        peopleByName.put(this.name, this);
        id = id_counter;
        id_counter += 1;
    }

    void addChoice(Person choice, int strength) {
        this.choices.put(choice, strength);
    }

    int getStrength(Person other) {
        if (this.choices.containsKey(other)) {
            return (this.choices.get(other));
        } else {
            return 0;
        }
    }

    int getLikability() {
        return likabilities.get(this);
    }

    void setLikability(int likability) {
        likabilities.put(this, likability);
    }

    void incrementLikability(int increment) {
        this.setLikability(this.getLikability() + increment);
    }

    int getMutualStrength(Person other) {
        int thisToOther = this.getStrength(other);
        int otherToThis = other.getStrength(this);
        int strength = (2 * (thisToOther + otherToThis)) - ((thisToOther == 0 || otherToThis == 0) ? 1 : 0);
        return strength;
    }

    @Override
    public int compareTo(Person o) {
        return this.getLikability() - o.getLikability();
    }

    @Override
    public String toString() {
        return this.name;
    }

    String generatePairHash(Person person) {
        ArrayList pair = new ArrayList();
        pair.add(this);
        pair.add(person);
        pair.sort(new Comparator<Person>() {
            @Override
            public int compare(Person o1, Person o2) {
                return o1.id - o2.id;
            }
        });
        return pair.toString();
    }

    static void add(Person person) {
        people.add(person);
    }

    static void calculateLikabilities() {
        for (Person person : people) {
            person.choices.forEach((Person choice, Integer strength) -> {
                choice.incrementLikability(strength);
            });
        }
    }
    static Person getByName(String name){
        return peopleByName.get(name);
    }

}
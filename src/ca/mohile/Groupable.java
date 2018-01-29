package ca.mohile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiConsumer;

public abstract class Groupable {

    abstract int getSize();

    /*
    Returns choices ordered from highest to lowest
     */
    abstract HashMap<GPerson, Integer> getChoices();

    abstract int getStrength(GPerson groupable);
    @Override
    public String toString() {
        return "";
    }
}

class Group extends Groupable {
    ArrayList<Groupable> children;

    public Group(Groupable o, Groupable o2){
        children = new ArrayList<>();
        children.add(o);
        children.add(o2);
    }
    @Override
    int getSize() {
        int size = 0;
        for (Groupable child : children) {
            size += child.getSize();
        }
        return size;
    }

    @Override
    HashMap<GPerson, Integer> getChoices() {
        HashMap<GPerson, Integer> choices = new HashMap<>();
        /*
        Iterate through each child, getting their choices and aggergating them
         */
        for (Groupable child : children) {
            HashMap<GPerson, Integer> childChoices = child.getChoices();
            for (GPerson choice : childChoices.keySet()) {
                if (!children.contains(choice)) {
                    if (choices.containsKey(choice)) {
                        choices.put(choice, choices.get(choice) + childChoices.get(choice));
                    } else {
                        choices.put(choice, childChoices.get(choice));
                    }
                }
            }
        }
        return choices;
    }

    int getStrength(GPerson other) {
        int strength = 0;
        for (Groupable child : children) {
            if (child.getChoices().containsKey(other)) {
                strength += (child.getChoices().get(other));
            }
        }
        return strength;
    }

    @Override
    public String toString() {
        String output = "(";
        for(Groupable child : children){
            output += child.toString();
            if(children.indexOf(child) != children.size()-1){
                output += ", ";
            }
        }
        return output + ")";
    }

}

class GPerson extends Groupable {
    static HashMap<String, GPerson> NAME_INDEX = new HashMap<>();
    HashMap<GPerson, Integer> choices;
    String name;

    public GPerson(String name){
        choices = new HashMap<>();
        this.name = name;
        GPerson.NAME_INDEX.put(this.name, this);
    }
    static GPerson getPerson(String name){
        return NAME_INDEX.get(name);
    }
    void addChoice(GPerson person, Integer value){
        this.choices.put(person, value);
    }
    @Override
    int getSize() {
        return 1;
    }

    @Override
    public HashMap<GPerson, Integer> getChoices() {
        return this.choices;
    }

    @Override
    int getStrength(GPerson other) {
        if (this.choices.containsKey(other)) {
            return (this.choices.get(other));
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return super.toString() + this.name;
    }
}
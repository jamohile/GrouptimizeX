package ca.mohile;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class Iteration {
    HashMap<Groupable, Groupable> hierarchy = new HashMap<>();
    ArrayList<Groupable> people;
    ArrayList<Groupable> groups;

    public Iteration() {
        people = new ArrayList<>();
    }

    void add(GPerson person) {
        people.add(person);
    }

    void optimize(int targetGroups, int maxPerGroup) {
        if (targetGroups * maxPerGroup < this.people.size()) {
            System.out.println("Please adjust size parameters, this setup is not possible.");
        } else {
            groups = optimize(this.people, targetGroups, maxPerGroup);
            print();
        }
    }

    ArrayList<Groupable> optimize(ArrayList<Groupable> set, int targetGroups, int maxPerGroup) {
        /*
        Generate a set of popularities in this scope
         */
        HashMap<Groupable, Integer> popularities = generatePopularities(set);
        ArrayList<Groupable> groups = new ArrayList<>();
        int limit = set.size();

        /*
        Iterator defines order of choice
         */
        BottomUpIterator iterator = new BottomUpIterator(popularities);
        /*
        If there are too many groups, optimize to combine groups.
         */
        while (limit >= 0) {
            if (getUngrouped(groups, set, popularities).size() == 0) {
                break;
            }
            Groupable g = iterator.getNextPerson(getUngrouped(groups, set, popularities));
            Group group = choose(g, maxPerGroup);
            if (group != null) {
                groups.add(group);
                for (Groupable child : group.children) {
                    if (groups.contains(child)) {
                        groups.remove(child);
                    }
                }
            }
            limit -= 1;
        }
        //If true, this means this optimization yielded no new results.
        boolean noMoreSolutions = groups.size() == 0;
        //Add back existing groups
        for (Groupable group : getUngrouped(groups, set, popularities)) {
            groups.add(group);
        }
        if (groups.size() > targetGroups && !noMoreSolutions) {
            return optimize(groups, targetGroups, maxPerGroup);
        } else {
            return groups;
        }

    }

    /*
Takes a properly formatted JSON file
and builds a network.
WARN: NO SYNTAX CHECKING.
 */
    void configureFromJSONFile(String fileName) {
        String file = "";
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(fileName));

            JSONArray people = (JSONArray) jsonObject.get("people");
            java.util.Iterator<JSONObject> peopleiterator = people.iterator();
            /*
            Set names.
             */
            while (peopleiterator.hasNext()) {
                JSONObject person = peopleiterator.next();
                add(new GPerson((String) person.get("name")));
            }
            /*
            Set choices.
             */
            peopleiterator = people.iterator();
            while (peopleiterator.hasNext()) {
                JSONObject jsonPerson = peopleiterator.next();
                GPerson person = GPerson.getPerson((String) jsonPerson.get("name"));

                JSONArray jsonChoices = (JSONArray) jsonPerson.get("choices");
                java.util.Iterator choiceIterator = jsonChoices.iterator();
                while (choiceIterator.hasNext()) {
                    JSONObject jsonChoice = (JSONObject) choiceIterator.next();
                    GPerson choice = GPerson.getPerson((String) jsonChoice.get("name"));
                    int value = Math.toIntExact((Long) jsonChoice.get("value"));
                    person.addChoice(choice, value);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void print() {
        String output = "";
        for (Groupable g : groups) {
            output += g.toString() + "\n";
        }
        System.out.println(output);
    }

    ArrayList<Groupable> getUngrouped(ArrayList<Groupable> groups, ArrayList<Groupable> set, HashMap<Groupable, Integer> popularities) {
        ArrayList<Groupable> ungrouped = new ArrayList<>();
        for (Groupable g : set) {
            if (!groups.contains(getHighestParent(g))) {
                ungrouped.add(g);
            }
        }

        ungrouped.sort(new Comparator<Groupable>() {
            @Override
            public int compare(Groupable o1, Groupable o2) {
                /*
                Inverted, so favorable choices come to front.
                 */
                return popularities.get(o1) - popularities.get(o2);
            }
        });
        return ungrouped;
    }

    Group choose(Groupable groupable, int maxPerGroup) {
        ArrayList<GPerson> choices = new ArrayList<>();
        choices.addAll(groupable.getChoices().keySet());

        choices.sort(new Comparator<GPerson>() {
            @Override
            public int compare(GPerson o1, GPerson o2) {
                /*
                Inverted, so favorable choices come to front.
                 */
                return groupable.getStrength(o2) - groupable.getStrength(o1);
            }
        });

        for (GPerson choice : choices) {
            if (getHighestParent(choice).getSize() + groupable.getSize() <= maxPerGroup) {
                Group group = new Group(groupable, getHighestParent(choice));

                hierarchy.put(groupable, group);
                hierarchy.put(getHighestParent(choice), group);

                return group;
                //satisfaction += person.getMutualStrength(choice);
            }
        }
        return null;
    }

    HashMap<Groupable, Integer> generatePopularities(ArrayList<Groupable> set) {
        /*
        Return popularities aggregated to the current level of groupable abstraction
         */
        HashMap<Groupable, Integer> popularities = new HashMap<>();
        for (Groupable g : set) {
            popularities.put(g, 0);
        }
        for (Groupable groupable : set) {
            HashMap<GPerson, Integer> choices = groupable.getChoices();
            for (Groupable choice : choices.keySet()) {
                Groupable highestParent = getHighestParent(choice);
                popularities.put(highestParent, popularities.get(highestParent) + choices.get(choice));
            }
        }
        return popularities;
    }

    Groupable getHighestParent(Groupable g) {
        if (hasParent(g)) {
            return getHighestParent(getParent(g));
        } else {
            return g;
        }
    }

    /*
    Returns hierarchical parent if exists, otherwise returns self
     */
    Groupable getParent(Groupable g) {
        return hierarchy.get(g);
    }

    Boolean hasParent(Groupable g) {
        return hierarchy.containsKey(g);
    }
}

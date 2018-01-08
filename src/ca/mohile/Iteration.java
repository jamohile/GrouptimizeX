package ca.mohile;

import java.util.*;

abstract class Iteration {
    static ArrayList<Person> people;
    HashMap<Person, Person> pairs;
    int satisfaction = 0;
    int iterations = 0;

    String name="";


    public Iteration(ArrayList<Person> people, String name) {
        pairs = new HashMap<>();
        this.name = name;
        Iteration.people = people;
    }

    HashMap<Person, Person> run() {
        int numPeople = people.size();

        while (numPeople - pairs.size() >= 1 && iterations < numPeople) {
            /*
            Find next person, and make a choice.
             */
            choose(getNextPerson());
            iterations += 1;
        }
        return pairs;
    }

    void setPair(Person a, Person b) {
        pairs.put(a, b);
        pairs.put(b, a);
    }

    boolean hasPair(Person p) {
        return pairs.containsKey(p);
    }

    Person getPair(Person p) {
        return pairs.get(p);
    }

    void removePair(Person a, Person b) {
        pairs.remove(a);
        pairs.remove(b);
    }

    /*
    Returns a likability sorted array of unpaired people from pairs.
     */
    protected ArrayList<Person> getUnpaired() {
        ArrayList<Person> unpaired = new ArrayList<>();
        for (Person person : people) {
            if (!hasPair(person)) {
                unpaired.add(person);
            }
        }
        Collections.sort(unpaired);
        return unpaired;
    }

    private void choose(Person person) {
        ArrayList<Person> choices = new ArrayList<>();
        choices.addAll(person.choices.keySet());


        choices.sort(new Comparator<Person>() {
            @Override
            public int compare(Person o1, Person o2) {
                /*
                Inverted, so favorable choices come to front.
                 */
                return person.getStrength(o2) - person.getStrength(o1);
            }
        });

        for (Person choice : choices) {
            if (!hasPair(choice)) {
                setPair(person, choice);
                satisfaction += person.getMutualStrength(choice);
                break;
            }
        }
    }
    String getMeta(){
        return name + " (" + satisfaction + ") : \n";
    }
    abstract Person getNextPerson();

    @Override
    public String toString() {
        ArrayList pairCache = new ArrayList();
        String object = "Paired: " + pairs.size() + " / " + people.size() + "\n";
        for (Person person : pairs.keySet()) {
            Person other = pairs.get(person);
            if (!pairCache.contains(person.generatePairHash(other))) {
                String pair = "\t" + person.toString() + " : " + other.toString() + " (" + person.getMutualStrength(other) + ")\n";
                object += pair;
                pairCache.add(person.generatePairHash(other));
            }
        }
        ;
        return object;

    }
}

/*
    Different algorithms.
     */
class TopDownIteration extends Iteration {


    public TopDownIteration(ArrayList<Person> people) {
        super(people, "Top Down Iteration");
    }

    @Override
    Person getNextPerson() {
        ArrayList<Person> people = (this.getUnpaired());
            /*
            Top Down, so we are looking for highest possible likability.
             */
        return people.get(people.size() - 1);
    }

    @Override
    public String toString() {
        return getMeta() + super.toString();
    }
}

class BottomUpIteration extends Iteration {
    public BottomUpIteration(ArrayList<Person> people) {
        super(people, "Bottom Up Iteration");
    }

    @Override
    Person getNextPerson() {
        ArrayList<Person> people = this.getUnpaired();
        return people.get(0);
    }

    @Override
    public String toString() {
        return this.getMeta() + super.toString();
    }
}

/*
Alternates between selecting high and low extreme.
 */
class EquivocationIteration extends Iteration {
    /*
    This algorithm goes back and forth between highest and lowest.
     */
    boolean MODE_HIGH = false;

    public EquivocationIteration(ArrayList<Person> people) {
        this(people, true);
    }

    public EquivocationIteration(ArrayList<Person> people, boolean startHigh) {
        super(people, "Equivocation Iteration");
        /*
        Invert MODE_HIGH because algorithm switches it before using it.
         */
        MODE_HIGH = !startHigh;
    }

    @Override
    Person getNextPerson() {
        ArrayList<Person> people = this.getUnpaired();

        MODE_HIGH = !MODE_HIGH;
        if (MODE_HIGH) {
            return people.get(people.size() - 1);
        } else {
            return people.get(0);
        }
    }

    @Override
    public String toString() {
        return this.name + " (" + satisfaction + ") : \n" + super.toString();
    }
}

/*
Always finds person closest to mean likability.
 */
class MiddleIteration extends Iteration {

    public MiddleIteration(ArrayList<Person> people) {
        this(people, "Middle Iteration");
    }
    public MiddleIteration(ArrayList<Person> people, String name){super(people, name);}
    @Override
    public String toString() {
        return this.getMeta() + super.toString();
    }

    @Override
    Person getNextPerson() {
        ArrayList<Person> people = this.getUnpaired();
        BTS<Person> bts = new BTS<>(people, Person.BTSEvaluator);
        return bts.seek(getAverageUnpairedLikeability());
    }

    int getAverageLikability() {
        return getAverageLikability(Person.likabilities.values());
    }

    int getAverageLikability(Collection<Integer> likabilities) {
        int sum = 0;
        for (int likability : likabilities) {
            sum += likability;
        }
        return sum / likabilities.size();
    }

    int getAverageLikabilityForGroup(Collection<Person> people) {
        ArrayList<Integer> likeabilities = new ArrayList<>();
        for (Person person : getUnpaired()) {
            likeabilities.add(person.getLikability());
        }
        return getAverageLikability(likeabilities);
    }

    int getAverageUnpairedLikeability() {
        return getAverageLikabilityForGroup(getUnpaired());
    }
}

/*
Moves with or against the trend.
If average likability is less than or greater
than initial average, that is the trend.
 */
class SwingIteration extends MiddleIteration {
    int initialAverage;
    boolean inverted = false;

    public SwingIteration(ArrayList<Person> people) {
        this(people, false);
    }

    public SwingIteration(ArrayList<Person> people, boolean inverted) {
        super(people, "Swing Interation");
        this.inverted = inverted;
        initialAverage = getAverageLikabilityForGroup(people);
    }

    @Override
    Person getNextPerson() {
        ArrayList<Person> people = this.getUnpaired();
        int currentLikability = getAverageUnpairedLikeability();
        if ((currentLikability >= initialAverage && !inverted) || currentLikability <initialAverage && inverted) {
            return people.get(people.size() - 1);
        } else {
            return people.get(0);
        }
    }

    @Override
    public String toString() {
        return this.getMeta() + super.toString();
    }
}
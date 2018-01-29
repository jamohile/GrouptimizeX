package ca.mohile;

import java.util.*;


abstract class Iterator {
    static enum ITERATOR_TYPES {
        TOP_DOWN,
        BOTTOM_UP,
        EQUIVOCATION,
        MIDDLE,
        SWING,
        INV_SWING
    }

    HashMap<Groupable, Integer> popularities;
    int satisfaction = 0;
    int Iterators = 0;

    String name = "";


    public Iterator(HashMap<Groupable, Integer> popularities, String name) {
        this.popularities = popularities;
        this.name = name;
    }

    String getMeta() {
        return name + " (" + satisfaction + ") : \n";
    }

    BTS.BTSEval<Groupable> BTSEvaluator = groupable -> popularities.get(groupable);

    abstract Groupable getNextPerson(ArrayList<Groupable> ungrouped);

//    @Override
//    public String toString() {
//        ArrayList pairCache = new ArrayList();
//        Integer totalStrength = 0;
//        String object = "Paired: " + pairs.size() + " / " + people.size() + "\n";
//        for (Person person : pairs.keySet()) {
//            Person other = pairs.get(person);
//            if (!pairCache.contains(person.generatePairHash(other))) {
//                totalStrength += person.getMutualStrength(other);
//                String pair = "\t" + person.toString() + " : " + other.toString() + " (" + person.getMutualStrength(other) + ")\n";
//                object += pair;
//                pairCache.add(person.generatePairHash(other));
//            }
//        }
//        ;
//        object += "\n Average Pair Strength: " + (totalStrength / pairCache.size()) + "\n" + "Average Total Strength: " + 2* totalStrength / people.size();
//        return object;
//
//    }
}

/*
    Different algorithms.
     */
class TopDownIterator extends Iterator {


    public TopDownIterator(HashMap<Groupable, Integer> popularities) {
        super(popularities, "Top Down Iterator");
    }

    @Override
    Groupable getNextPerson(ArrayList<Groupable> ungrouped) {
            /*
            Top Down, so we are looking for highest possible likability.
             */
        return ungrouped.get(ungrouped.size() - 1);
    }

    @Override
    public String toString() {
        return getMeta() + super.toString();
    }
}

class BottomUpIterator extends Iterator {
    public BottomUpIterator(HashMap<Groupable, Integer> popularities) {
        super(popularities, "Bottom Up Iterator");
    }

    @Override
    Groupable getNextPerson(ArrayList<Groupable> ungrouped) {
        return ungrouped.get(0);
    }

    @Override
    public String toString() {
        return this.getMeta() + super.toString();
    }
}

/*
Alternates between selecting high and low extreme.
 */
class EquivocationIterator extends Iterator {
    /*
    This algorithm goes back and forth between highest and lowest.
     */
    boolean MODE_HIGH = false;

    public EquivocationIterator(HashMap<Groupable, Integer> popularities) {
        this(popularities, true);
    }

    public EquivocationIterator(HashMap<Groupable, Integer> popularities, boolean startHigh) {
        super(popularities, "Equivocation Iterator");
        /*
        Invert MODE_HIGH because algorithm switches it before using it.
         */
        MODE_HIGH = !startHigh;
    }

    @Override
    Groupable getNextPerson(ArrayList<Groupable> ungrouped) {
        MODE_HIGH = !MODE_HIGH;
        if (MODE_HIGH) {
            return ungrouped.get(ungrouped.size() - 1);
        } else {
            return ungrouped.get(0);
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
class MiddleIterator extends Iterator {

    public MiddleIterator(HashMap<Groupable, Integer> popularities) {
        this(popularities, "Middle Iterator");
    }

    public MiddleIterator(HashMap<Groupable, Integer> popularities, String name) {
        super(popularities, name);
    }

    @Override
    public String toString() {
        return this.getMeta() + super.toString();
    }

    @Override
    Groupable getNextPerson(ArrayList<Groupable> ungrouped) {
        BTS<Groupable> bts = new BTS<Groupable>(ungrouped, BTSEvaluator);
        return bts.seek(getAveragePopularityForGroup(ungrouped));
    }

    int getAveragePopularity(Collection<Integer> popularities) {
        int sum = 0;
        for (int popularity : popularities) {
            sum += popularity;
        }
        return sum / popularities.size();
    }

    int getAveragePopularityForGroup(Collection<Groupable> set) {
        ArrayList<Integer> popularities = new ArrayList<>();
        for (Groupable g : set) {
            popularities.add(this.popularities.get(g));
        }
        return getAveragePopularity(popularities);
    }
}

/*
Moves with or against the trend.
If average likability is less than or greater
than initial average, that is the trend.
 */
class SwingIterator extends MiddleIterator {
    int initialAverage;
    boolean inverted = false;

    public SwingIterator(HashMap<Groupable, Integer> popularities) {
        this(popularities, false);
    }

    public SwingIterator(HashMap<Groupable, Integer> popularities, boolean inverted) {
        super(popularities, inverted ? "Inverted Swing Iterator" : "Swing Iterator");
        this.inverted = inverted;
        initialAverage = getAveragePopularityForGroup(popularities.keySet());
    }

    @Override
    Groupable getNextPerson(ArrayList<Groupable> ungrouped) {
        int currentLikability = getAveragePopularityForGroup(ungrouped);
        if ((currentLikability >= initialAverage && !inverted) || currentLikability < initialAverage && inverted) {
            return ungrouped.get(ungrouped.size() - 1);
        } else {
            return ungrouped.get(0);
        }
    }

    @Override
    public String toString() {
        return this.getMeta() + super.toString();
    }
}
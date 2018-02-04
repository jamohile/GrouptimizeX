package ca.mohile;

import java.util.*;


abstract class RankedIterator {
    HashMap<Groupable, Integer> rankings;
    String name = "";


    public RankedIterator(HashMap<Groupable, Integer> rankings, String name) {
        this.rankings = rankings;
        this.name = name;
    }

    String getMeta() {
        return name + "\n";
    }

    BTS.BTSEval<Groupable> BTSEvaluator = groupable -> rankings.get(groupable);

    abstract Groupable getNext(ArrayList<Groupable> ungrouped);
}

/*
Different algorithms.
 */
class TopDownIterator extends RankedIterator {


    public TopDownIterator(HashMap<Groupable, Integer> rankings) {
        super(rankings, "Top Down Iterator");
    }

    @Override
    Groupable getNext(ArrayList<Groupable> ungrouped) {
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

class BottomUpIterator extends RankedIterator {
    public BottomUpIterator(HashMap<Groupable, Integer> rankings) {
        super(rankings, "Bottom Up Iterator");
    }

    @Override
    Groupable getNext(ArrayList<Groupable> ungrouped) {
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
class EquivocationIterator extends RankedIterator {
    /*
    This algorithm goes back and forth between highest and lowest.
     */
    boolean MODE_HIGH = false;

    public EquivocationIterator(HashMap<Groupable, Integer> rankings) {
        this(rankings, true);
    }

    public EquivocationIterator(HashMap<Groupable, Integer> rankings, boolean startHigh) {
        super(rankings, "Equivocation Iterator");
        /*
        Invert MODE_HIGH because algorithm switches it before using it.
         */
        MODE_HIGH = !startHigh;
    }

    @Override
    Groupable getNext(ArrayList<Groupable> ungrouped) {
        MODE_HIGH = !MODE_HIGH;
        if (MODE_HIGH) {
            return ungrouped.get(ungrouped.size() - 1);
        } else {
            return ungrouped.get(0);
        }
    }

    @Override
    public String toString() {
        return this.name + "\n" + super.toString();
    }
}

/*
Always finds person closest to mean likability.
 */
class MiddleIterator extends RankedIterator {

    public MiddleIterator(HashMap<Groupable, Integer> rankings) {
        this(rankings, "Middle Iterator");
    }

    public MiddleIterator(HashMap<Groupable, Integer> rankings, String name) {
        super(rankings, name);
    }

    @Override
    public String toString() {
        return this.getMeta() + super.toString();
    }

    @Override
    Groupable getNext(ArrayList<Groupable> ungrouped) {
        BTS<Groupable> bts = new BTS<Groupable>(ungrouped, BTSEvaluator);
        return bts.seek(getAverageRankingForGroup(ungrouped));
    }

    int getAverageRanking(Collection<Integer> rankings) {
        int sum = 0;
        for (int popularity : rankings) {
            sum += popularity;
        }
        return sum / rankings.size();
    }

    int getAverageRankingForGroup(Collection<Groupable> set) {
        ArrayList<Integer> rankings = new ArrayList<>();
        for (Groupable g : set) {
            rankings.add(this.rankings.get(g));
        }
        return getAverageRanking(rankings);
    }
}

/*
Moves with or against the trend.
If average likability is less than or greater
than initial average, that is the trend.
 */
class SwingIterator extends MiddleIterator {
    int initialAverageRanking;
    boolean inverted = false;

    public SwingIterator(HashMap<Groupable, Integer> rankings) {
        this(rankings, false);
    }

    public SwingIterator(HashMap<Groupable, Integer> rankings, boolean inverted) {
        super(rankings, inverted ? "Inverted Swing Iterator" : "Swing Iterator");
        this.inverted = inverted;
        initialAverageRanking = getAverageRankingForGroup(rankings.keySet());
    }

    @Override
    Groupable getNext(ArrayList<Groupable> ungrouped) {
        int currentAverageRanking = getAverageRankingForGroup(ungrouped);
        if ((currentAverageRanking >= initialAverageRanking && !inverted) || currentAverageRanking < initialAverageRanking && inverted) {
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
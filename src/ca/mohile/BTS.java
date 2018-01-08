package ca.mohile;

import java.util.ArrayList;

class BTS<T> {
    /*
    Allows any function f to evaluate x[].
    If goal is to search x[], simply use
    identity function.
     */
    interface BTSEval<T> {
        int evaluate(T t);
    }
    /*
    x[] is immutable.
     */
    final ArrayList<T> items;
    BTSEval<T> btsEval;

    public BTS(ArrayList<T> items, BTSEval<T> btsEval) {
        this.items = items;
        this.btsEval = btsEval;
    }

    /*
    Entry point for recursive seek function.
    Argument is target f(x).
     */
    public T seek(int target) {
        int target_index = seek(this.items, target);
        return this.items.get(dissolve(this.items, target_index));
    }

    /*
    Recursively search over x[] for target f(x).
     */
    private int seek(ArrayList<T> items, int target) {
        int halfway = ((int) Math.round((double) items.size() / 2)) - 1;
        int eval_halfway = this.btsEval.evaluate(items.get(halfway));

        if (items.size() == 1 || eval_halfway == target) {
            return halfway;
        } else if (eval_halfway > target) {
            return seek(new ArrayList<T>(items.subList(0, halfway)), target);
        } else {

            return seek(new ArrayList<T>(items.subList(halfway, items.size() - 1)), target);
        }

    }

    /*
    Return a single index representing the midpoint of that target value,
    "Dissolves" a potentially contiguous set.
     */
    int dissolve(ArrayList<T> items, int target_index) {
        int upper_bound = target_index;
        int lower_bound = target_index;
        /*
        The target value we are searching for.
        Given as a function of the list we have.
        (we are given X's, looking for a certain f(x))
         */
        int target_value = this.btsEval.evaluate(this.items.get(target_index));

        /*
        Creep up and down to find contiguous target values.
         */
        while (upper_bound < items.size() && this.btsEval.evaluate(items.get(upper_bound)) == target_value) {
            upper_bound += 1;
        }
        upper_bound -= 1;
        while (lower_bound >= 0 && this.btsEval.evaluate(items.get(lower_bound)) == target_value) {
            lower_bound -= 1;
        }
        lower_bound += 1;

        /*
        Return average of upper and lower bounds.
         */
        return (int) ((upper_bound + lower_bound) / 2);
    }

}

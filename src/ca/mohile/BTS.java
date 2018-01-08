package ca.mohile;

import java.util.ArrayList;

class BTS<T> {
    interface BTSEval<T> {
        int evaluate(T t);
    }

    final ArrayList<T> items;
    BTSEval<T> btsEval;

    public BTS(ArrayList<T> items, BTSEval<T> btsEval) {
        this.items = items;
        this.btsEval = btsEval;

    }

    public T seek(int target) {
        int target_index = seek(this.items, target);
        return this.items.get(dissolve(this.items, target_index));
    }


    private int seek(ArrayList<T> items, int target) {
        int halfway = ((int) Math.nextUp(items.size() / 2)) - 1;
        int eval_halfway = this.btsEval.evaluate(items.get(halfway));

        if (items.size() == 1 || eval_halfway == target) {
            return halfway;
        } else if (eval_halfway > target) {
            return seek((ArrayList) items.subList(0, halfway), target);
        } else {
            return seek((ArrayList) items.subList(halfway, items.size() - 1), target);
        }

    }

    /*
    Return a single index representing the midpoint of that target value
     */
    int dissolve(ArrayList<T> items, int target_index) {
        int upper_bound = target_index;
        int lower_bound = target_index;
        int target_value = this.btsEval.evaluate(this.items.get(target_index));

        while (upper_bound < items.size() && this.btsEval.evaluate(items.get(upper_bound)) == target_value) {
            upper_bound += 1;
        }
        upper_bound -= 1;
        while (lower_bound >= 0 && this.btsEval.evaluate(items.get(lower_bound)) == target_value) {
            lower_bound -= 1;
        }
        lower_bound += 1;

        return (int) ((upper_bound + lower_bound) / 2);
    }

}

package ca.mohile;

import org.json.simple.*;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Iteration iteration = new Iteration();
        iteration.configureFromJSONFile("anj's.json");
        iteration.optimize(3, 2);
    }

    /*
    Function to read in a state from
    CMD.
     */
}



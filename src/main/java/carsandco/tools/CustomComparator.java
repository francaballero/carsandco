package carsandco.tools;

import java.util.Comparator;

public class CustomComparator implements Comparator<Pair<Integer, Station>> {

    public int compare(Pair<Integer, Station> o1, Pair<Integer, Station> o2) {
        if (o1.getKey() < o2.getKey()) {
            return -1;
        } else if (o1.getValue().equals(o2.getValue())) {
            return 0;
        } else {
            return 1;
        }
    }
}

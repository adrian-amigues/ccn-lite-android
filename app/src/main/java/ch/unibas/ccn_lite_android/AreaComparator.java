package ch.unibas.ccn_lite_android;

import java.util.Comparator;

/**
 * Comparator for areas
 *
 * Created by adrian on 2016-10-28.
 */

public class AreaComparator implements Comparator<Area> {
    @Override
    public int compare(Area a1, Area a2) {
        try {
            int value1 = Integer.parseInt(a1.getDescription());
            int value2 = Integer.parseInt(a2.getDescription());
            return value1 - value2;
        } catch (Exception e) {
            return a1.getDescription().compareTo(a2.getDescription());
        }
    }
}
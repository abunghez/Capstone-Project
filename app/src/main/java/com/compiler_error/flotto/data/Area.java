package com.compiler_error.flotto.data;

import android.location.Location;

import java.util.ArrayList;

/**
 * Created by andrei on 05.05.2016.
 */
public class Area {
    Location center;
    int totalSum;
    ArrayList<Location> locations;
    double sumLati, sumLongi;

    public Area(Location c, int s) {
        center = c;
        totalSum = s;
        locations  = new ArrayList<Location>();
        locations.add(c);
        sumLati = c.getLatitude();
        sumLongi = c.getLongitude();
    }

    public void insert(Location l, int s) {
        totalSum+=s;

        sumLati += l.getLatitude();
        sumLongi += l.getLongitude();

        locations.add(l);

        center.setLatitude(sumLati / locations.size());
        center.setLongitude(sumLongi / locations.size());

    }

    public int getSum() {
        return totalSum;
    }

    public Location getLocation() {
        return center;
    }
}

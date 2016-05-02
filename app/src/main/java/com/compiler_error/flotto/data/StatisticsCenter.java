package com.compiler_error.flotto.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;

import java.util.ArrayList;

/**
 * Created by andrei on 21.03.2016.
 */
public class StatisticsCenter  {


    private Context mContext;
    private OnDataReadyListener mOdrl;
    private boolean dataReady;

    private Cursor mMaxSpent, mAvgSpent;
    private Cursor allReceipts;

    private Location maxLocation;
    private int maxLocationSum;

    public Location getMaxLocation() {
        return maxLocation;
    }

    public int getMaxLocationSum() {
        return maxLocationSum;
    }


    class Area {
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

    private ArrayList<Area> areas;

    public StatisticsCenter(Context context) {
        mContext = context;
        mOdrl = null;
        dataReady = false;
    }

    public synchronized void setOnDataReadyListener(OnDataReadyListener listener) {
        mOdrl = listener;
        if (dataReady) {
            mOdrl.onDataReady();
        }
    }

    public synchronized void updateStatistics() {

        AsyncTask<Void, Void, Void> updateTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                ContentResolver cr;

                /* cleanup */
                if (mMaxSpent != null)
                    mMaxSpent.close();

                if (mAvgSpent != null)
                    mAvgSpent.close();

                cr = mContext.getContentResolver();


                mMaxSpent = cr.query(FlottoDbContract.buildMaxSpent(), null, null, null, null);
                mAvgSpent = cr.query(FlottoDbContract.buildAvgDaily(), null, null, null, null);

                allReceipts = cr.query(FlottoDbContract.buildReceipts(), null, null, null, FlottoDbContract.ReceiptTableColumns.DATE_COL);

                if (allReceipts!=null) {
                    areas = new ArrayList<Area>();
                    maxLocationSum = 0;
                    if (allReceipts.moveToFirst()) {

                        do {
                            Location l;
                            double lati, longi;
                            int sum;

                            sum = allReceipts.getInt(allReceipts.getColumnIndex(FlottoDbContract.ReceiptTableColumns.SUM_COL));
                            lati = allReceipts.getDouble(allReceipts.getColumnIndex(FlottoDbContract.ReceiptTableColumns.LATI_COL));
                            longi = allReceipts.getDouble(allReceipts.getColumnIndex(FlottoDbContract.ReceiptTableColumns.LONGI_COL));

                            l = new Location("");
                            l.setLatitude(lati);
                            l.setLongitude(longi);

                            boolean found = false;
                            for (Area a:areas) {
                                if (a.center.distanceTo(l) < 1000) {
                                    /* found an area to insert this location into */
                                    a.insert(l, sum);
                                    found = true;
                                    if (a.getSum() > maxLocationSum) {
                                        maxLocationSum = a.getSum();
                                        maxLocation = a.getLocation();
                                    }
                                    break;
                                }
                            }

                            if (!found) {
                                /* create a new are with this location */
                                areas.add(new Area(l, sum));

                                if (sum > maxLocationSum) {
                                    maxLocationSum = sum;
                                    maxLocation = l;
                                }
                            }

                        } while (allReceipts.moveToNext());
                    }

                }
                return null;

            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (mOdrl != null) {
                    mOdrl.onDataReady();
                }
            }
        };

        updateTask.execute();




    }

    public Cursor getMaxSpent() {
        return mMaxSpent;
    }

    public Cursor getAvgSpent() {
        return mAvgSpent;
    }
}

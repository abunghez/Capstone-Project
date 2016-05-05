package com.compiler_error.flotto.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;

import java.util.ArrayList;

/**
 * Created by andrei on 21.03.2016.
 */
public class StatisticsCenter  {


    private static Context mContext;

    private static boolean dataReady;

    private static Cursor mMaxSpent, mAvgSpent;
    private static Cursor allReceipts;

    private static Location maxLocation;
    private static int maxLocationSum;

    public static Location getMaxLocation() {
        return maxLocation;
    }

    public static int getMaxLocationSum() {
        return maxLocationSum;
    }



    private static ArrayList<Area> areas;


    private static ArrayList<OnDataReadyListener> listeners;


    public static ArrayList<Area> getAreas() {
        return areas;
    }

    public static void initialize(Context context) {
        mContext = context;
        dataReady = false;
        areas = new ArrayList<Area>();
        listeners = new ArrayList<OnDataReadyListener>();
    }
    public synchronized static void addOnDataReadyListener(OnDataReadyListener listener) {

        listeners.add(listener);
        if (dataReady)
            listener.onDataReady();
    }

    public synchronized  static void removeOnDataReadyListener(OnDataReadyListener listener) {
        listeners.remove(listener);
    }

    public synchronized static void updateStatistics() {

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
                    areas.clear();
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
                dataReady = true;
                for (OnDataReadyListener lstn:listeners) {
                    lstn.onDataReady();
                }
            }
        };

        updateTask.execute();




    }

    public static Cursor getMaxSpent() {
        return mMaxSpent;
    }

    public static Cursor getAvgSpent() {
        return mAvgSpent;
    }
}

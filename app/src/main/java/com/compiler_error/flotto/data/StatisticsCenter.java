package com.compiler_error.flotto.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

/**
 * Created by andrei on 21.03.2016.
 */
public class StatisticsCenter  {


    private Context mContext;
    private OnDataReadyListener mOdrl;
    private boolean dataReady;

    private Cursor mMaxSpent, mAvgSpent;




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

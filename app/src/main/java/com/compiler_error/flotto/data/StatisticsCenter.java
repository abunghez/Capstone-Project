package com.compiler_error.flotto.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

/**
 * Created by andrei on 21.03.2016.
 */
public class StatisticsCenter  {


    private Context mContext;
    private OnDataReadyListener mOdrl;
    private boolean dataReady;

    private final static String AVG_SPENDING_QUERY=
            "SELECT AVERAGE(DAILY_SUM) from ("+
                    "SELECT SUM("+FlottoDbContract.ReceiptTableColumns.SUM_COL+ ") AS DAILY_SUM" +
                        "FROM "+ FlottoDbContract.RECEIPT_TABLE +
                        " GROUP BY" + FlottoDbContract.ReceiptTableColumns.DATE_COL+ ") ";




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
        ContentResolver cr;

        cr = mContext.getContentResolver();

        Cursor avgSpending;



    }
    public abstract class OnDataReadyListener {
        public abstract void onDataReady();
    }
}

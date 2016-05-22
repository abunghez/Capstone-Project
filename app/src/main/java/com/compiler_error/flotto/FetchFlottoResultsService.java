package com.compiler_error.flotto;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.NotificationCompat;
import android.util.Xml;

import com.compiler_error.flotto.data.FlottoDbContract;

import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by andrei on 22.05.2016.
 */
public class FetchFlottoResultsService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    private static final String RESULTS_URL ="http://compiler-error.com/flotto/results.xml";

    private static final int NOTIFICATION_ID = 0;
    public FetchFlottoResultsService(String name) {
        super(name);
    }

    public FetchFlottoResultsService() {
        super("FlottoResultsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        URL url;
        XmlPullParser parser = Xml.newPullParser();
        InputStream is;
        String winDate;
        String winSum;

        ArrayList<String> winDates;
        ArrayList<String> winSums;


        try {
            url = new URL(RESULTS_URL);
            is = url.openStream();
            parser.setInput(is, null);

            int eventType;
            eventType = parser.getEventType();

            winDates = new ArrayList<String>();
            winSums  = new ArrayList<String>();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && parser.getName().equals("flotto")) {
                    winDate = parser.getAttributeValue(null, "date");
                    winSum  = parser.getAttributeValue(null, "sum");
                    winDates.add(winDate);
                    winSums.add(winSum);
                }
                parser.next();
                eventType = parser.getEventType();
            }

            is.close();

        }catch (Exception e) {
            e.printStackTrace();
            return;
        }

        String selection;
        String[] selectionArgs;

        selection="";
        selectionArgs = new String[winDates.size() + winSums.size()];

        for (int i = 0; i < winDates.size();i++) {
            selection = selection + "(" + FlottoDbContract.ReceiptTableColumns.DATE_COL + "=? AND "
                    + FlottoDbContract.ReceiptTableColumns.SUM_COL + "=? ) OR ";
            selectionArgs[2 * i] = winDates.get(i);
            selectionArgs[2 * i + 1] = winSums.get(i);

        }
        selection = selection.substring(0, selection.lastIndexOf("OR"));

        ContentResolver cr = getContentResolver();

        Cursor c;

        c = cr.query(FlottoDbContract.buildReceipts(), null, selection,selectionArgs, null);

        if (c != null && c.getCount() != 0) {
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_menu_search)
                    .setContentTitle(getString(R.string.winning_notification_title))
                    .setContentText(getString(R.string.notification_sum) + ": "+winSums.get(0));

            Intent notificationIntent = new Intent(this, SearchActivity.class);

            notificationIntent.putExtra(SearchActivity.EXTRA_SELECTION, selection);
            notificationIntent.putExtra(SearchActivity.EXTRA_SELECTION_ARGS, selectionArgs);

            PendingIntent pi = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);


            builder.setContentIntent(pi)
                    .setAutoCancel(true);

            NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);


            nm.notify(NOTIFICATION_ID, builder.build());
        }


    }
}

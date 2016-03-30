package com.compiler_error.flotto.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by andrei on 16.03.2016.
 */
public class FlottoDbContract {

    public static final String CONTENT_AUTHORITY="com.compiler_error.flotto";
    public static final Uri BASE_CONTENT_URI=Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String RECEIPT_TABLE="receipt_table";

    public static final class ReceiptTableColumns implements BaseColumns {
        public static final String SUM_COL="sum";
        public static final String DATE_COL="date";
        public static final String FILE_COL="file";
        public static final String LATI_COL="latitude";
        public static final String LONGI_COL="longitude";

        public static final Uri buildReceipts() {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();
        }

        public static final String PATH="receipts";
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH;
    }

    public static Uri buildStatistics() {
        return BASE_CONTENT_URI.buildUpon().appendPath("statistics").build();
    }

    public static Uri buildMaxSpent() {
        return buildStatistics().buildUpon().appendPath("max_spent").build();
    }

    public static Uri buildAvgDaily() {
        return buildStatistics().buildUpon().appendPath("avg_daily").build();
    }

    public static Uri buildMaxLocation() {
        return buildStatistics().buildUpon().appendPath("max_location").build();
    }
}

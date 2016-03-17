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

        public static final Uri buildReceipts() {
            return BASE_CONTENT_URI.buildUpon().appendPath("receipts").build();
        }

        public static final String PATH="receipts";
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH;
    }
}

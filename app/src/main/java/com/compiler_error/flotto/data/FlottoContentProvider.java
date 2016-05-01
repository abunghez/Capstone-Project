package com.compiler_error.flotto.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by andrei on 17.03.2016.
 */
public class FlottoContentProvider extends ContentProvider {

    private static FlottoDbHelper mHelper;
    private static final int RECEIPTS=100;
    private static final int RECEIPTS_WITH_ID=101;
    private static final int RECEIPTS_WITH_DATE=102;
    private static final int RECEIPTS_WITH_SUM=103;
    private static final int RECEIPTS_WITH_DATE_AND_SUM=104;



    private static final int STATISTICS_MAX_SPENT=110;
    private static final int STATISTICS_AVG_SPENT_DAILY=111;

    private final static String QUERY_DAILY_SUMS=
                    "SELECT SUM("+FlottoDbContract.ReceiptTableColumns.SUM_COL+ ") AS DAILY_SUM," +
                    FlottoDbContract.ReceiptTableColumns.DATE_COL +" "+
                    "FROM "+ FlottoDbContract.RECEIPT_TABLE +
                    " GROUP BY " + FlottoDbContract.ReceiptTableColumns.DATE_COL;

    private final static String QUERY_MIN_DAY=
                    "SELECT MIN(julianday("+FlottoDbContract.ReceiptTableColumns.DATE_COL+")) AS MIN_DAY " +
                            "FROM " + FlottoDbContract.RECEIPT_TABLE;
    private final static String QUERY_MAX_DAY=
                    "SELECT MAX(julianday("+FlottoDbContract.ReceiptTableColumns.DATE_COL+")) AS MAX_DAY " +
                       "FROM " + FlottoDbContract.RECEIPT_TABLE;

    private final static String QUERY_MAX_SPENT_DAILY=
            "SELECT MAX(DAILY_SUM) as MAX_SPENT FROM ("+QUERY_DAILY_SUMS+")";



    private final static String QUERY_DAILY_AVG_1 =
            "SELECT SUM(" + FlottoDbContract.ReceiptTableColumns.SUM_COL+")/";

    private final static String QUERY_DAILY_AVG_2 = " AS AVG_SUM " +
                    "FROM "+ FlottoDbContract.RECEIPT_TABLE;
    private static final String RECEIPTS_BY_ID =
            FlottoDbContract.ReceiptTableColumns._ID + " = ?";
    private static final String RECEIPTS_BY_DATE =
            FlottoDbContract.ReceiptTableColumns.DATE_COL + " = ?";

    private static final String RECEIPTS_BY_SUM =
            FlottoDbContract.ReceiptTableColumns.SUM_COL + " = ?";

    private static final String RECEIPTS_BY_DATE_AND_SUM =
            FlottoDbContract.ReceiptTableColumns.DATE_COL+ " = ? AND " +
            FlottoDbContract.ReceiptTableColumns.SUM_COL + " = ?";
    private UriMatcher mUriMatcher = buildUriMatcher();

    static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        String authority = FlottoDbContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, null, 0);
        matcher.addURI(authority, "receipts", RECEIPTS);
        matcher.addURI(authority, "receipts/id", RECEIPTS_WITH_ID);
        matcher.addURI(authority, "receipts/date", RECEIPTS_WITH_DATE);
        matcher.addURI(authority, "receipts/sum", RECEIPTS_WITH_SUM);
        matcher.addURI(authority, "receipts/sum_date", RECEIPTS_WITH_DATE_AND_SUM);

        matcher.addURI(authority, "statistics/max_spent", STATISTICS_MAX_SPENT);
        matcher.addURI(authority, "statistics/avg_daily", STATISTICS_AVG_SPENT_DAILY);


        return matcher;
    }
    @Override
    public boolean onCreate() {

        mHelper = new FlottoDbHelper(getContext());
        return false;
    }


    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int match = mUriMatcher.match(uri);
        Cursor ret = null;
        switch (match) {
            case RECEIPTS:
                ret = mHelper.getReadableDatabase().query(
                  FlottoDbContract.RECEIPT_TABLE,
                        projection,
                        null, null, null, null, sortOrder);
                break;
            case RECEIPTS_WITH_ID:
                ret = mHelper.getReadableDatabase().query(
                        FlottoDbContract.RECEIPT_TABLE,
                        projection,
                        RECEIPTS_BY_ID,
                        selectionArgs,
                        null, null, sortOrder
                );
                break;

            case RECEIPTS_WITH_DATE:
                ret = mHelper.getReadableDatabase().query(
                  FlottoDbContract.RECEIPT_TABLE,
                        projection,
                        RECEIPTS_BY_DATE,
                        selectionArgs,
                        null, null, sortOrder
                );
                break;

            case RECEIPTS_WITH_SUM:
                ret = mHelper.getReadableDatabase().query(
                        FlottoDbContract.RECEIPT_TABLE,
                        projection,
                        RECEIPTS_BY_SUM,
                        selectionArgs,
                        null, null, sortOrder
                );
                break;

            case RECEIPTS_WITH_DATE_AND_SUM:
                ret = mHelper.getReadableDatabase().query(
                        FlottoDbContract.RECEIPT_TABLE,
                        projection,
                        RECEIPTS_BY_DATE_AND_SUM,
                        selectionArgs,
                        null, null, sortOrder
                );
                break;

            case STATISTICS_MAX_SPENT:
                ret = mHelper.getReadableDatabase().rawQuery(QUERY_MAX_SPENT_DAILY, null);
                break;

            case STATISTICS_AVG_SPENT_DAILY: {
                int min_day, max_day;

                ret = mHelper.getReadableDatabase().rawQuery(QUERY_MIN_DAY,null);
                if (ret != null && ret.moveToFirst()) {
                    min_day = ret.getInt(ret.getColumnIndex("MIN_DAY"));
                    ret.close();
                } else return null;

                ret = mHelper.getReadableDatabase().rawQuery(QUERY_MAX_DAY, null);
                if (ret != null && ret.moveToFirst()) {
                    max_day = ret.getInt(ret.getColumnIndex("MAX_DAY"));
                    ret.close();
                } else return null;

                if (max_day < min_day) return null;

                ret = mHelper.getReadableDatabase().rawQuery(QUERY_DAILY_AVG_1 + (max_day - min_day+1) +
                        QUERY_DAILY_AVG_2, null);

           }



        }
        return ret;
    }


    @Nullable
    @Override
    public String getType(Uri uri) {
        int match = mUriMatcher.match(uri);

        switch (match) {

            case RECEIPTS:
            case RECEIPTS_WITH_DATE:
            case RECEIPTS_WITH_SUM:
            case RECEIPTS_WITH_DATE_AND_SUM:
                return FlottoDbContract.ReceiptTableColumns.CONTENT_DIR_TYPE;
            case RECEIPTS_WITH_ID:
                return FlottoDbContract.ReceiptTableColumns.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri:"+uri);
        }


    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri retUri = null;

        SQLiteDatabase db = mHelper.getWritableDatabase();

        switch (mUriMatcher.match(uri)) {
            case RECEIPTS:
                db.beginTransaction();
                db.insertWithOnConflict(FlottoDbContract.RECEIPT_TABLE,
                        null, values, SQLiteDatabase.CONFLICT_REPLACE);
                retUri = uri.buildUpon()
                        .appendPath("id")
                        .appendPath(values.getAsString(FlottoDbContract.ReceiptTableColumns._ID))
                        .build();

                db.setTransactionSuccessful();
                db.endTransaction();
                break;
            default:
                break;
        }
        return retUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }


}

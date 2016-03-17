package com.compiler_error.flotto;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.compiler_error.flotto.data.FlottoDbContract;
import com.compiler_error.flotto.data.FlottoDbHelper;

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

        String authority = FlottoDbContract.BASE_CONTENT_URI.toString();

        matcher.addURI(authority, null, RECEIPTS);
        matcher.addURI(authority, "id", RECEIPTS_WITH_ID);
        matcher.addURI(authority, "date", RECEIPTS_WITH_DATE);
        matcher.addURI(authority, "sum", RECEIPTS_WITH_SUM);
        matcher.addURI(authority, "sum_date", RECEIPTS_WITH_DATE_AND_SUM);

        return matcher;
    }
    @Override
    public boolean onCreate() {
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

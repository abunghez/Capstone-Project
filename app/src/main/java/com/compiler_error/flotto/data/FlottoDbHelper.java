package com.compiler_error.flotto.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by andrei on 17.03.2016.
 */
public class FlottoDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME="Flotto.db";
    private static final int DATABASE_VERSION=1;
    public FlottoDbHelper(Context context) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String createReceiptsTable="CREATE TABLE "+FlottoDbContract.RECEIPT_TABLE + " ("
                + FlottoDbContract.ReceiptTableColumns._ID + " INTEGER PRIMARY KEY,"
                + FlottoDbContract.ReceiptTableColumns.SUM_COL + " INTEGER NOT NULL,"
                + FlottoDbContract.ReceiptTableColumns.DATE_COL + " TEXT NOT NULL,"
                + FlottoDbContract.ReceiptTableColumns.FILE_COL + " TEXT,"
                + FlottoDbContract.ReceiptTableColumns.LATI_COL + " REAL,"
                + FlottoDbContract.ReceiptTableColumns.LONGI_COL+ " REAL"
                + ");";

        db.execSQL(createReceiptsTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FlottoDbContract.RECEIPT_TABLE);
    }
}

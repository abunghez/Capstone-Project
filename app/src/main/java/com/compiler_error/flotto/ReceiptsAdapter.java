package com.compiler_error.flotto;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.compiler_error.flotto.data.FlottoDbContract;

/**
 * Created by andrei on 17.03.2016.
 */
public class ReceiptsAdapter extends CursorAdapter {

    public ReceiptsAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View ret = LayoutInflater.from(context).inflate(R.layout.receipt_layout, parent, false);

        return ret;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView sumTextView, dateTextView;

        dateTextView = (TextView) view.findViewById(R.id.dateTextView);
        sumTextView = (TextView) view.findViewById(R.id.sumTextView);

        dateTextView.setText(cursor.getString(cursor.getColumnIndex(FlottoDbContract.ReceiptTableColumns.DATE_COL)));
        sumTextView.setText(cursor.getString(cursor.getColumnIndex(FlottoDbContract.ReceiptTableColumns.SUM_COL)));
    }
}

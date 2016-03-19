package com.compiler_error.flotto;

import android.content.Context;
import android.database.Cursor;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.compiler_error.flotto.data.FlottoDbContract;
import com.squareup.picasso.Picasso;

/**
 * Created by andrei on 17.03.2016.
 */
public class ReceiptsAdapter extends CursorAdapter {

    Context mContext;
    public ReceiptsAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        mContext = context;
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View ret = LayoutInflater.from(context).inflate(R.layout.receipt_layout, parent, false);

        return ret;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView sumTextView, dateTextView;
        ImageView thumbImageView;

        dateTextView = (TextView) view.findViewById(R.id.dateTextView);
        sumTextView = (TextView)  view.findViewById(R.id.sumTextView);
        thumbImageView =(ImageView) view.findViewById(R.id.thumbImageView);

        dateTextView.setText(cursor.getString(cursor.getColumnIndex(FlottoDbContract.ReceiptTableColumns.DATE_COL)));
        sumTextView.setText(cursor.getString(cursor.getColumnIndex(FlottoDbContract.ReceiptTableColumns.SUM_COL)));
        /*Picasso.with(mContext)
                .load(cursor.getString(cursor.getColumnIndex(FlottoDbContract.ReceiptTableColumns.FILE_COL)))
                .into(thumbImageView);
        */

    }

}

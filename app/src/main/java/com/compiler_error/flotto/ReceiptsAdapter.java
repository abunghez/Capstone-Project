package com.compiler_error.flotto;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.compiler_error.flotto.data.FlottoDbContract;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

/**
 * Created by andrei on 17.03.2016.
 */
public class ReceiptsAdapter extends RecyclerView.Adapter<ReceiptsAdapter.ViewHolder> {


    Cursor mCursor;
    public ReceiptsAdapter() {
        super();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;

        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.receipt_layout, parent,false);

        ViewHolder vh;
        vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mCursor != null) {
            int sum, tsize;
            mCursor.moveToPosition(position);

            sum = mCursor.getInt(mCursor.getColumnIndex(FlottoDbContract.ReceiptTableColumns.SUM_COL));

            if (sum < 50)
                tsize = 16;
            else tsize = (int) ((sum - 50)*16./949+16);


            holder.sumTextView.setTextSize(tsize);
            holder.dateTextView.setText(mCursor
                    .getString(mCursor.getColumnIndex(FlottoDbContract.ReceiptTableColumns.DATE_COL)));;
            holder.sumTextView.setText(mCursor
                    .getString(mCursor.getColumnIndex(FlottoDbContract.ReceiptTableColumns.SUM_COL)));
            Picasso.with(holder.thumbView.getContext())
                    .load(mCursor
                            .getString(mCursor.getColumnIndex(FlottoDbContract.ReceiptTableColumns.FILE_COL)))
                    .into(holder.thumbView);


        }
    }

    @Override
    public int getItemCount() {
        if (mCursor != null)
            return mCursor.getCount();
        return 0;
    }

    public void swapCursor(Cursor data) {

        Cursor old = mCursor;
        if (old != data) {
            if (old != null) old.close();
            mCursor = data;
            this.notifyDataSetChanged();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView sumTextView, dateTextView;
        public ImageView thumbView;

        public ViewHolder(View v) {
            super(v);
            sumTextView=(TextView) v.findViewById(R.id.sumTextView);
            dateTextView=(TextView) v.findViewById(R.id.dateTextView);
            thumbView=(ImageView) v.findViewById(R.id.thumbImageView);
        }
    }


}

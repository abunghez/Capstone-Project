package com.compiler_error.flotto;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.CardView;
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
    Context mContext;
    public ReceiptsAdapter(Context context) {
        super();
        mContext = context;
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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (mCursor != null) {
            final int sum, tsize;
            mCursor.moveToPosition(position);

            sum = mCursor.getInt(mCursor.getColumnIndex(FlottoDbContract.ReceiptTableColumns.SUM_COL));

            if (sum < 50)
                tsize = 16;
            else if (sum < 999)
                tsize = (int) ((sum - 50)*16./949+16);
            else tsize = 32;

            final String date, path;

            date = mCursor
                    .getString(mCursor.getColumnIndex(FlottoDbContract.ReceiptTableColumns.DATE_COL));

            path = mCursor
                    .getString(mCursor.getColumnIndex(FlottoDbContract.ReceiptTableColumns.FILE_COL));
            holder.sumTextView.setTextSize(tsize);
            holder.dateTextView.setText(date);;
            holder.sumTextView.setText(String.valueOf(sum));

            Picasso.with(holder.thumbView.getContext())
                    .load(path)
                    .into(holder.thumbView);
            holder.path = path;
            holder.id = mCursor.getInt(mCursor.getColumnIndex(FlottoDbContract.ReceiptTableColumns._ID));

            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = NewReceiptActivity.packNewReceiptIntent(v.getContext(),
                            holder.id,
                            Integer.parseInt(holder.sumTextView.getText().toString()),
                            holder.dateTextView.getText().toString(),
                            holder.path);
                    mContext.startActivity(intent);
                }
            });

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
        public View container;
        public int id;
        public String path;

        public ViewHolder(View v) {
            super(v);
            container = v;
            sumTextView=(TextView) v.findViewById(R.id.sumTextView);
            dateTextView=(TextView) v.findViewById(R.id.dateTextView);
            thumbView=(ImageView) v.findViewById(R.id.thumbImageView);
            id = AddReceiptFragment.INVALID_ID;
        }
    }


}

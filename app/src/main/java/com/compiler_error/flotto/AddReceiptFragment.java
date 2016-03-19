package com.compiler_error.flotto;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.compiler_error.flotto.data.FlottoDbContract;
import com.squareup.picasso.Picasso;

/**
 * Created by andrei on 18.03.2016.
 */
public class AddReceiptFragment extends Fragment {

    public static final String PHOTO_PATH_KEY="RECEIPT_PHOTO_PATH";
    Button mInsert;
    EditText mDate, mSum;
    ImageView mImageView;
    String mFilePath;
    public AddReceiptFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v;
        v = inflater.inflate(R.layout.fragment_add_receipt, container, false);
        mDate = (EditText) v.findViewById(R.id.receiptDatePicker);
        mSum = (EditText) v.findViewById(R.id.sumEditText);

        mInsert = (Button) v.findViewById(R.id.receiptAddButton);

        mInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date;
                int sum;

                date = mDate.getText().toString();
                sum = Integer.parseInt(mSum.getText().toString());

                ContentValues data;

                data = new ContentValues();
                data.put(FlottoDbContract.ReceiptTableColumns.DATE_COL, date);
                data.put(FlottoDbContract.ReceiptTableColumns.SUM_COL, sum);
                data.put(FlottoDbContract.ReceiptTableColumns.FILE_COL, mFilePath);

                getActivity().getContentResolver().insert(
                        FlottoDbContract.ReceiptTableColumns.buildReceipts(),
                        data
                );
                getActivity().finish();

            }
        });

        mImageView = (ImageView)v.findViewById(R.id.receiptImageView);
        Bundle fragmentArgs = getArguments();
        mFilePath = fragmentArgs.getString(PHOTO_PATH_KEY);

        if (mFilePath != null) {
            Picasso.with(getActivity()).load(mFilePath).into(mImageView);
        }

        return v;
    }


}

package com.compiler_error.flotto;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.compiler_error.flotto.data.FlottoDbContract;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by andrei on 18.03.2016.
 */
public class AddReceiptFragment extends Fragment {

    public static final String PHOTO_PATH_KEY="RECEIPT_PHOTO_PATH";
    public static final String SUM_KEY="SUM";
    public static final String DATE_KEY="DATE";
    public static final String ID_KEY="ID";
    public static final String LOCATION_KEY="LOCATION";
    Button mInsert;
    EditText mDate, mSum;
    ImageView mImageView;
    String mFilePath;
    public static final int INVALID_ID=-1;
    int mId = INVALID_ID;
    Location mLocation;
    TextView mLocationLabel;

    public AddReceiptFragment() {
        super();
    }

    AddressResultReceiver mReceiver;

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            String locationString = resultData.getString(FetchAddressIntentService.Constants.RESULT_DATA_KEY);
            mLocationLabel.setText(locationString);
        }
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v;

        setRetainInstance(true);

        v = inflater.inflate(R.layout.fragment_add_receipt, container, false);
        mDate = (EditText) v.findViewById(R.id.receiptDatePicker);
        mSum = (EditText) v.findViewById(R.id.sumEditText);


        mDate.setFocusable(false);
        mDate.setOnClickListener(new View.OnClickListener() {
             class DatePickerFragment extends DialogFragment implements
                    DatePickerDialog.OnDateSetListener {

                 @Override
                 public Dialog onCreateDialog(Bundle savedInstanceState) {
                     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                     Date date;
                     int year, month, day;
                     try {
                         date = sdf.parse(mDate.getText().toString());
                     } catch (ParseException e) {
                         e.printStackTrace();
                         date = new Date();
                     }

                     Calendar cal = Calendar.getInstance();
                     cal.setTime(date);

                     year = cal.get(Calendar.YEAR);
                     month = cal.get(Calendar.MONTH);
                     day = cal.get(Calendar.DAY_OF_MONTH);

                     return new DatePickerDialog(getActivity(),this,year,month,day);
                 }

                 @Override
                 public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                     mDate.setText(year+"-"+String.format("%02d",monthOfYear+1)+"-"
                             +String.format("%02d",dayOfMonth));
                 }
             };
            @Override
            public void onClick(View v) {
                DialogFragment df = new DatePickerFragment();
                df.show(getActivity().getFragmentManager(), "datePicker");

            }
        });

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
                if (mLocation != null) {
                    data.put(FlottoDbContract.ReceiptTableColumns.LATI_COL, mLocation.getLatitude());
                    data.put(FlottoDbContract.ReceiptTableColumns.LONGI_COL, mLocation.getLongitude());
                } else {
                    data.put(FlottoDbContract.ReceiptTableColumns.LATI_COL, -1.);
                    data.put(FlottoDbContract.ReceiptTableColumns.LONGI_COL, -1.);
                }

                if (mId >= 0) {
                    data.put(FlottoDbContract.ReceiptTableColumns._ID, mId);
                }
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

        mSum.setText(String.valueOf(fragmentArgs.getInt(SUM_KEY, 0)));
        mDate.setText(fragmentArgs.getString(DATE_KEY));
        if (mFilePath != null) {
            Picasso.with(getActivity()).load(mFilePath).into(mImageView);
        }


        mId = fragmentArgs.getInt(ID_KEY, INVALID_ID);


        mLocation = fragmentArgs.getParcelable(LOCATION_KEY);

        mLocationLabel = (TextView) v.findViewById(R.id.locationLabel);

        if (mLocation != null) {
       /* get address from location */
            Intent i = new Intent(getActivity(), FetchAddressIntentService.class);
            mReceiver = new AddressResultReceiver(new Handler());
            i.putExtra(FetchAddressIntentService.Constants.RECEIVER, mReceiver);
            i.putExtra(FetchAddressIntentService.Constants.LOCATION_DATA_EXTRA, mLocation);
            getActivity().startService(i);
        }
        return v;
    }

    static public Bundle packNewReceiptFragmentArgs(int id, int sum, String date, String path) {
        Bundle fragmentArgs = new Bundle();

        fragmentArgs.putInt(ID_KEY, id);
        fragmentArgs.putInt(SUM_KEY, sum);
        if (date != null) fragmentArgs.putString(DATE_KEY, date);
        if (path != null) fragmentArgs.putString(PHOTO_PATH_KEY, path);

        return fragmentArgs;

    }

    static public Bundle packNewReceiptFragmentArgs(int id, int sum, String date, String path,
                                                    Location loc) {
        Bundle fragmentArgs;
        fragmentArgs = packNewReceiptFragmentArgs(id, sum, date, path);
        if (loc != null) {
            fragmentArgs.putParcelable(LOCATION_KEY, loc);
        }
        return fragmentArgs;
    }

}

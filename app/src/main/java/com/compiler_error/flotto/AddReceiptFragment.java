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
import android.widget.Toast;

import com.compiler_error.flotto.data.FlottoDbContract;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
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
    Button mInsert, mDelete;
    EditText mDate, mSum;
    ImageView mImageView;
    String mFilePath;
    public static final int INVALID_ID=-1;
    int mId = INVALID_ID;
    Location mLocation;
    TextView mLocationLabel;

    InterstitialAd mInterstitialAd;
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
             class DatePickerFragment extends ReceiptDateDialogFragment {
                 @Override
                 public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                     mDate.setText(year+"-"+String.format("%02d",monthOfYear+1)+"-"
                             +String.format("%02d",dayOfMonth));
                 }
             };
            @Override
            public void onClick(View v) {
                DatePickerFragment df = new DatePickerFragment();
                Bundle arg = new Bundle();
                arg.putString(df.KEY_DATE, mDate.getText().toString());
                df.setArguments(arg);
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
                try {
                    sum = Integer.parseInt(mSum.getText().toString());
                } catch (Exception e) {
                    Toast.makeText(getActivity(), R.string.invalid_number_msg, Toast.LENGTH_SHORT)
                    .show();
                    getActivity().finish();
                    return;
                }

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

                /* 25% chance of displaying an add here */
                if (Math.random() < .25) {
                    if (mInterstitialAd.isLoaded())
                        mInterstitialAd.show();

                }
                getActivity().finish();

            }
        });



        mImageView = (ImageView)v.findViewById(R.id.receiptImageView);
        Bundle fragmentArgs = getArguments();
        mFilePath = fragmentArgs.getString(PHOTO_PATH_KEY);

        mSum.setText(String.valueOf(fragmentArgs.getInt(SUM_KEY, 0)));
        mSum.setContentDescription(getString(R.string.sum_cd) + ": "+mSum.getText().toString());

        mDate.setText(fragmentArgs.getString(DATE_KEY));
        mDate.setContentDescription(getString(R.string.date_cd) + ": "+mDate.getText().toString());
        if (mFilePath != null) {
            Picasso.with(getActivity()).load(mFilePath).into(mImageView);
        }


        mId = fragmentArgs.getInt(ID_KEY, INVALID_ID);

        mDelete = (Button) v.findViewById(R.id.deleteButton);
        if (mId != INVALID_ID) {
            mDelete.setVisibility(View.VISIBLE);
        }
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] selectionArgs = new String[1];
                if (mId != INVALID_ID) {
                    selectionArgs[0] = String.valueOf(mId);

                    getActivity().getContentResolver().delete(FlottoDbContract.buildReceipts(),
                            FlottoDbContract.ReceiptTableColumns._ID + "=?", selectionArgs);
                }

                if (mInterstitialAd.isLoaded())
                    mInterstitialAd.show();
                getActivity().finish();
            }
        });
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

        mInterstitialAd = new InterstitialAd(getActivity());
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        requestNewInterstitial();
        return v;
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("A5DAADE46E8E03504CFD2C5D0B8B9129")
                .build();

        mInterstitialAd.loadAd(adRequest);
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

package com.compiler_error.flotto;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;

import java.util.ArrayList;

/**
 * Created by andrei on 19.03.2016.
 */
public class NewReceiptActivity extends AppCompatActivity{
    public final static String EXTRA_IMAGE_PATH="com.compiler_error.flotto.newreceipt.imagepath";
    public final static String EXTRA_SUM="com.compiler_error.flotto.newreceipt.sum";
    public final static String EXTRA_DATE="com.compiler_error.flotto.newreceipt.date";
    public final static String EXTRA_ID="com.compiler_error.flotto.newreceipt.id";
    public final static String EXTRA_LOCATION="com.compiler_error.flotto.newreceipt.location";
    AddReceiptFragment mReceiptFragment;
    String mImagePath;
    Location mLocation;
    int mId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent;
        Bundle fragmentArgs;
        int sum;
        String date;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_receipt);
        intent = getIntent();

        mImagePath = intent.getStringExtra(EXTRA_IMAGE_PATH);
        sum = intent.getIntExtra(EXTRA_SUM, 0);
        date = intent.getStringExtra(EXTRA_DATE);
        mId = intent.getIntExtra(EXTRA_ID, AddReceiptFragment.INVALID_ID);

        Bundle locBundle = intent.getBundleExtra(EXTRA_LOCATION);
        if (locBundle != null) {
            mLocation = locBundle.getParcelable(EXTRA_LOCATION);
        }

        mReceiptFragment = (AddReceiptFragment) getSupportFragmentManager()
                .findFragmentById(R.id.newReceiptFragmentHolder);

        if (mReceiptFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(mReceiptFragment).commit();
        }

        mReceiptFragment = new AddReceiptFragment();


        fragmentArgs = AddReceiptFragment.packNewReceiptFragmentArgs(
                mId,
                sum,
                date,
                mImagePath,
                mLocation
                );
        mReceiptFragment.setArguments(fragmentArgs);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.newReceiptFragmentHolder, mReceiptFragment)
                .commit();



    }

    static public Intent packNewReceiptIntent(Context context, int id, int sum, String date, String path, Location loc) {
        Intent intent = new Intent(context, NewReceiptActivity.class);

        intent.putExtra(NewReceiptActivity.EXTRA_IMAGE_PATH, path);
        intent.putExtra(NewReceiptActivity.EXTRA_SUM, sum);
        intent.putExtra(NewReceiptActivity.EXTRA_DATE, date);
        intent.putExtra(NewReceiptActivity.EXTRA_ID, id);

        Bundle locBundle = new Bundle();
        locBundle.putParcelable(NewReceiptActivity.EXTRA_LOCATION, loc);
        intent.putExtra(NewReceiptActivity.EXTRA_LOCATION, locBundle);


        return intent;

    }
}

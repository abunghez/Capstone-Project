package com.compiler_error.flotto;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.compiler_error.flotto.data.OnDataReadyListener;
import com.compiler_error.flotto.data.StatisticsCenter;

public class StatsActivity extends AppCompatActivity {

    CardView mMaxSpentCard, mAvgSpentCard, mMaxLocationCard;
    ProgressBar mProgressBar;
    OnDataReadyListener mOdrl;

    AddressResultReceiver mReceiver;

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            String locationString = resultData.getString(FetchAddressIntentService.Constants.RESULT_DATA_KEY);
            ((TextView)mMaxLocationCard.findViewById(R.id.statTextViewLocation)).setText(locationString);
        }
    }
    void allCardsGone() {
        mMaxSpentCard.setVisibility(View.GONE);
        mAvgSpentCard.setVisibility(View.GONE);
        mMaxLocationCard.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    void allCardsVisible() {
        mMaxSpentCard.setVisibility(View.VISIBLE);
        mAvgSpentCard.setVisibility(View.VISIBLE);
        mMaxLocationCard.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mMaxSpentCard = (CardView) findViewById(R.id.cardMaxSpent);
        mAvgSpentCard = (CardView) findViewById(R.id.cardAvgSpent);
        mMaxLocationCard = (CardView) findViewById(R.id.cardMaxLocation);
        mProgressBar = (ProgressBar) findViewById(R.id.statSpinner);
        allCardsGone();

        mOdrl = new OnDataReadyListener() {


            @Override
            public void onDataReady() {
                Cursor  maxSpent = StatisticsCenter.getMaxSpent();

                int val, valIndex;

                if (maxSpent != null && maxSpent.moveToFirst()) {
                    valIndex = maxSpent.getColumnIndex("MAX_SPENT");
                    val = maxSpent.getInt(valIndex);
                    updateCard(mMaxSpentCard, getString(R.string.max_spent_description), String.valueOf(val));

                }

                Cursor avgSpent = StatisticsCenter.getAvgSpent();

                if (avgSpent != null && avgSpent.moveToFirst()) {
                    valIndex = avgSpent.getColumnIndex("AVG_SUM");
                    val = avgSpent.getInt(valIndex);
                    updateCard(mAvgSpentCard, getString(R.string.daily_avg_description), String.valueOf(val));
                }


                updateCard(mMaxLocationCard, getString(R.string.max_spent_location), String.valueOf(StatisticsCenter.getMaxLocationSum()));

                if (StatisticsCenter.getMaxLocation() != null) {
                    mReceiver = new AddressResultReceiver(new Handler());
                    Intent intent = new Intent(StatsActivity.this, FetchAddressIntentService.class);
                    intent.putExtra(FetchAddressIntentService.Constants.RECEIVER, mReceiver);

                    intent.putExtra(FetchAddressIntentService.Constants.LOCATION_DATA_EXTRA, StatisticsCenter.getMaxLocation());
                    StatsActivity.this.startService(intent);
                }
                allCardsVisible();

            }
        };
        StatisticsCenter.addOnDataReadyListener(mOdrl);

    }

    private void updateCard(CardView card, String description, String value) {
        TextView descTextView, valTextView;

        descTextView = (TextView)card.findViewById(R.id.statDescriptionTextView);
        valTextView = (TextView)card.findViewById(R.id.statValueTextView);

        descTextView.setText(description);
        valTextView.setText(value);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mOdrl != null) {
            StatisticsCenter.removeOnDataReadyListener(mOdrl);
        }
    }
}

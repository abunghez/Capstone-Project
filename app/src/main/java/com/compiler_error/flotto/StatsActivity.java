package com.compiler_error.flotto;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.compiler_error.flotto.data.OnDataReadyListener;
import com.compiler_error.flotto.data.StatisticsCenter;

public class StatsActivity extends AppCompatActivity {

    StatisticsCenter mStats;
    CardView mMaxSpentCard, mAvgSpentCard;
    ProgressBar mProgressBar;

    void allCardsGone() {
        mMaxSpentCard.setVisibility(View.GONE);
        mAvgSpentCard.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    void allCardsVisible() {
        mMaxSpentCard.setVisibility(View.VISIBLE);
        mAvgSpentCard.setVisibility(View.VISIBLE);
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
        mProgressBar = (ProgressBar) findViewById(R.id.statSpinner);
        allCardsGone();
        mStats = new StatisticsCenter(this);

        mStats.setOnDataReadyListener(new OnDataReadyListener() {


            @Override
            public void onDataReady() {
                Cursor  maxSpent = mStats.getMaxSpent();

                int val, valIndex;

                if (maxSpent != null && maxSpent.moveToFirst()) {
                    valIndex = maxSpent.getColumnIndex("MAX_SPENT");
                    val = maxSpent.getInt(valIndex);
                    updateCard(mMaxSpentCard, getString(R.string.max_spent_description), String.valueOf(val));

                }

                Cursor avgSpent = mStats.getAvgSpent();

                if (avgSpent != null && avgSpent.moveToFirst()) {
                    valIndex = avgSpent.getColumnIndex("AVG_SUM");
                    val = avgSpent.getInt(valIndex);
                    updateCard(mAvgSpentCard, getString(R.string.daily_avg_description), String.valueOf(val));
                }

                allCardsVisible();

            }
        });

        mStats.updateStatistics();
    }

    private void updateCard(CardView card, String description, String value) {
        TextView descTextView, valTextView;

        descTextView = (TextView)card.findViewById(R.id.statDescriptionTextView);
        valTextView = (TextView)card.findViewById(R.id.statValueTextView);

        descTextView.setText(description);
        valTextView.setText(value);
    }

}

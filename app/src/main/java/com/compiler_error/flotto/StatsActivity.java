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
import android.widget.TextView;

import com.compiler_error.flotto.data.OnDataReadyListener;
import com.compiler_error.flotto.data.StatisticsCenter;

public class StatsActivity extends AppCompatActivity {

    StatisticsCenter mStats;
    CardView mMaxSpentCard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mMaxSpentCard = (CardView) findViewById(R.id.cardMaxSpent);

        mStats = new StatisticsCenter(this);

        mStats.setOnDataReadyListener(new OnDataReadyListener() {


            @Override
            public void onDataReady() {
                Cursor  maxSpent = mStats.getMaxSpent();

                int ms, msIndex;

                if (maxSpent != null && maxSpent.moveToFirst()) {
                    msIndex = maxSpent.getColumnIndex("MAX_SPENT");
                    ms = maxSpent.getInt(msIndex);
                    updateCard(mMaxSpentCard, getString(R.string.max_spent_description), String.valueOf(ms));
                }

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

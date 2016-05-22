package com.compiler_error.flotto;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SearchActivity extends AppCompatActivity {

    SearchFragment mSearchFragment;

    public static final String EXTRA_SELECTION="com.compiler_error.flotto.SearchActivity.SELECTION";
    public static final String EXTRA_SELECTION_ARGS=
            "com.compiler_error.flotto.SearchActivity.SELECTION_ARGS";
    private String extraSelection;
    private String[] extraSelectionArgs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent intent = getIntent();

        extraSelection = intent.getStringExtra(EXTRA_SELECTION);
        extraSelectionArgs = intent.getStringArrayExtra(EXTRA_SELECTION_ARGS);

        FragmentManager fm = getSupportFragmentManager();
        mSearchFragment = (SearchFragment)fm.findFragmentById(R.id.search_params_holder);

        if (mSearchFragment == null) {
            mSearchFragment = new SearchFragment();
            fm.beginTransaction().add(R.id.search_params_holder, mSearchFragment)
                    .commit();
        }

        if (extraSelection != null && extraSelectionArgs != null) {
            SearchResultsFragment srf;

            srf = (SearchResultsFragment) fm.findFragmentById(R.id.search_results_holder);
            if (srf != null) {
                fm.beginTransaction().remove(srf).commit();
            }

            Bundle args = new Bundle();
            srf = new SearchResultsFragment();

            args.putString(srf.KEY_SELECTION, extraSelection);
            args.putStringArray(srf.KEY_SELECTION_ARGS, extraSelectionArgs);
            srf.setArguments(args);

            fm.beginTransaction().add(R.id.search_results_holder, srf).commit();

        }

    }
}

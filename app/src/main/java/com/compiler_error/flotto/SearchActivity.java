package com.compiler_error.flotto;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SearchActivity extends AppCompatActivity {

    SearchFragment mSearchFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);



        FragmentManager fm = getSupportFragmentManager();
        mSearchFragment = (SearchFragment)fm.findFragmentById(R.id.search_params_holder);

        if (mSearchFragment == null) {
            mSearchFragment = new SearchFragment();
            fm.beginTransaction().add(R.id.search_params_holder, mSearchFragment)
                    .commit();
        }

    }
}

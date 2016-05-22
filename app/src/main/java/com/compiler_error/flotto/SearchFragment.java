package com.compiler_error.flotto;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

public class SearchFragment extends Fragment {

    private SearchResultsFragment mSearchResultFragment;
    public SearchFragment() {
        // Required empty public constructor
    }

    EditText mDate, mSum;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);

        setRetainInstance(true);
        mDate = (EditText) v.findViewById(R.id.search_date_edit);
        mDate.setFocusable(false);
        mDate.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mDate.setText("");
                if (mSum.getText().toString().equals(""))
                    removeResultsFragment();
                else
                    createNewResultsFragment();
                return false;
            }
        });
        mDate.setOnClickListener(new View.OnClickListener() {
            class DatePickerFragment extends ReceiptDateDialogFragment {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                    mDate.setText(year+"-"+String.format("%02d",monthOfYear+1)+"-"
                            +String.format("%02d",dayOfMonth));


                    createNewResultsFragment();
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

        mSum = (EditText) v.findViewById(R.id.search_sum_edit);

        mSum.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    createNewResultsFragment();
                }
                return false;
            }
        });



        return v;
    }

    void setResultArguments(Bundle args) {
        if (!mDate.getText().toString().equals("")) {
            args.putString(SearchResultsFragment.KEY_DATE, mDate.getText().toString());
        }
        if (!mSum.getText().toString().equals("")) {
            args.putString(SearchResultsFragment.KEY_SUM, mSum.getText().toString());
        }
    }

    void removeResultsFragment() {
        FragmentManager fm = ((AppCompatActivity)getActivity()).getSupportFragmentManager();

        if (mSearchResultFragment != null) {

            fm.beginTransaction().remove(mSearchResultFragment).commit();
        }

    }
    void createNewResultsFragment() {
        FragmentManager fm = ((AppCompatActivity)getActivity()).getSupportFragmentManager();
        removeResultsFragment();
        mSearchResultFragment = new SearchResultsFragment();
        Bundle args = new Bundle();

        setResultArguments(args);
        mSearchResultFragment.setArguments(args);
        fm.beginTransaction().add(R.id.search_results_holder, mSearchResultFragment).commit();
    }
}

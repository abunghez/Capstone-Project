package com.compiler_error.flotto;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.compiler_error.flotto.data.FlottoDbContract;


public class SearchResultsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private String mQSum, mQDate;
    public final static String KEY_SUM="KEY_SUM";
    public final static String KEY_DATE="KEY_DATE";
    private static final int SEARCH_LOADER = 1;

    public final static String KEY_SELECTION="KEY_SELECTION";
    public final static String KEY_SELECTION_ARGS="KEY_SELECTION_ARGS";

    String mSelection; String[] mSelectionArgs;

    ReceiptsAdapter mAdapter;
    RecyclerView mRecycler;

     public SearchResultsFragment() {

    }



    @Override
    public void onResume() {
        super.onResume();

        getLoaderManager().restartLoader(SEARCH_LOADER, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search_results, container, false);
        setRetainInstance(true);
        mQSum = getArguments().getString(KEY_SUM, null);
        mQDate = getArguments().getString(KEY_DATE, null);

        mSelection = getArguments().getString(KEY_SELECTION, null);
        mSelectionArgs = getArguments().getStringArray(KEY_SELECTION_ARGS);

        mAdapter = new ReceiptsAdapter(getActivity());
        mRecycler = (RecyclerView)v.findViewById(R.id.search_recycler_view);
        mRecycler.setAdapter(mAdapter);
        mRecycler.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));


        getLoaderManager().initLoader(SEARCH_LOADER, null, this);


        return v;
    }


    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selectSumString=null, selectDateString=null, selectString = null;
        String[] selectArgs = null;

        if (mSelection == null) {
        /* At least one of the strings is not null */
            if (mQSum != null) {
                selectString = selectSumString = FlottoDbContract.ReceiptTableColumns.SUM_COL + " = ? ";
                if (mQDate == null) {
                    selectArgs = new String[1];
                    selectArgs[0] = mQSum;
                }
            }
            if (mQDate != null) {
                selectString = selectDateString = FlottoDbContract.ReceiptTableColumns.DATE_COL + " = ? ";
                if (mQSum == null) {
                    selectArgs = new String[1];
                    selectArgs[0] = mQDate;
                }
            }

            if (mQSum != null && mQDate != null) {
                selectArgs = new String[2];
                selectString = selectSumString + " AND " + selectDateString;
                selectArgs[0] = mQSum;
                selectArgs[1] = mQDate;
            }

        } else {
            /* triggered by the service */
            selectString = mSelection;
            selectArgs = mSelectionArgs;
        }

        return new CursorLoader(getActivity(), FlottoDbContract.buildReceipts(), null,
                selectString, selectArgs, null);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            data.moveToFirst();
            mAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}

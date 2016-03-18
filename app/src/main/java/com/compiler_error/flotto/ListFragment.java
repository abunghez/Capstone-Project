package com.compiler_error.flotto;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.compiler_error.flotto.data.FlottoDbContract;

/**
 * Created by andrei on 17.03.2016.
 */
public class ListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    ListView mListView;
    ReceiptsAdapter mAdapter;
    public final static int RECEIPTS_LOADER = 0;
    public ListFragment() {
        super();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v;

        v = inflater.inflate(R.layout.fragment_list, container, false);
        mListView = (ListView)v.findViewById(R.id.theList);
        mAdapter = new ReceiptsAdapter(getActivity(), null, 0);
        mListView.setAdapter(mAdapter);
        getLoaderManager().initLoader(RECEIPTS_LOADER, null, this);

        return v;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), FlottoDbContract.ReceiptTableColumns.buildReceipts(),
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            data.moveToFirst();
            mAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}

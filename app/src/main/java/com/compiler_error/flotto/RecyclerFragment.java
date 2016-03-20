package com.compiler_error.flotto;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.compiler_error.flotto.data.FlottoDbContract;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by andrei on 17.03.2016.
 */
public class RecyclerFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    RecyclerView mRecyclerView;
    ReceiptsAdapter mAdapter;
    FloatingActionButton mFab;
    private String mCurrentPhotoFile;

    public static final int REQUEST_IMAGE_CAPTURE = 1;

    public final static int RECEIPTS_LOADER = 0;
    public RecyclerFragment() {
        super();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v;

        v = inflater.inflate(R.layout.app_bar_main, container, false);
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        ((AppCompatActivity)(getActivity())).setSupportActionBar(toolbar);
        DrawerArrowDrawable dad = new DrawerArrowDrawable(getActivity());
        toolbar.setNavigationIcon(dad);

        mAdapter = new ReceiptsAdapter();
        mRecyclerView = (RecyclerView) v.findViewById(R.id.receiptRecycler);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mFab = (FloatingActionButton) v.findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }

        });

        getLoaderManager().initLoader(RECEIPTS_LOADER, null, this);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        getLoaderManager().restartLoader(RECEIPTS_LOADER, null, this);
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

    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;

            try {
                photoFile = createImageFile();

            } catch (IOException e) {
                e.printStackTrace();
                mCurrentPhotoFile = null;
                return;

            }

            if (photoFile != null) {
                mCurrentPhotoFile = photoFile.getAbsolutePath();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }


        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp+ ".jpg";
        File storageDir = getActivity().getExternalFilesDir(null);

        File image = new File(storageDir, imageFileName);

        image.createNewFile();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {

            /**
             * TODO: run the image through tesseract
             *
             * for now, just display the receipt detail fragment with the thumbnail of the
             * acquired image
             */
            Intent intent = new Intent(getActivity(), NewReceiptActivity.class);

            if (mCurrentPhotoFile != null) {

                Bitmap bmp = BitmapFactory.decodeFile(mCurrentPhotoFile);
                Bitmap scaledBmp;

                scaledBmp = Bitmap.createScaledBitmap(bmp,
                        bmp.getWidth() / 4, bmp.getHeight() / 4, true);


                File out = new File(mCurrentPhotoFile);
                out.delete();

                try {
                    out.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

                OutputStream os = null;
                try {
                    os = new FileOutputStream(out);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();

                    return;
                }

                scaledBmp.compress(Bitmap.CompressFormat.JPEG, 100, os);
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

                intent.putExtra(NewReceiptActivity.EXTRA_IMAGE_PATH, mCurrentPhotoFile);
            }
            startActivity(intent);

        } else {
            mCurrentPhotoFile = null;
        }
    }

}

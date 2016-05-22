package com.compiler_error.flotto;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;

import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.compiler_error.flotto.data.FlottoDbContract;
import com.compiler_error.flotto.data.StatisticsCenter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor>, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    RecyclerView mRecyclerView;
    ReceiptsAdapter mAdapter;
    FloatingActionButton mFab;
    private String mCurrentPhotoFile;
    GoogleApiClient mGoogleAPIClient;
    TessBaseAPI mTess;

    private static final int LOCATION_PERMISSION_CODE = 0;


    public static final int REQUEST_IMAGE_CAPTURE = 1;

    public final static int RECEIPTS_LOADER = 0;
    private Location mLastLocation;


    private AlarmManager mAlarmManager;
    private PendingIntent mPendingIntent;

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
    private void moveTessFile() {
        String dataPath = getExternalFilesDir(null).getAbsolutePath();
        File tessDir = new File(dataPath+"/tessdata/");
        File trainFile = new File(dataPath+"/tessdata/eng.traineddata");
        AssetManager assets = getAssets();


        if (!tessDir.exists()) {
            tessDir.mkdir();
        }

        if (!trainFile.exists()) {
            InputStream in= null;
            OutputStream out = null;
            try {
                in = assets.open("eng.traineddata");
                out = new FileOutputStream(trainFile);
                copyFile(in, out);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {


                    if (out != null)
                        out.close();
                    if (in != null)
                        in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }
    private void cleanupOldEntries() {
        AsyncTask<Void, Void, Void> cleanupTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                ContentResolver cr = MainActivity.this.getContentResolver();
                Date now = new Date();
                Date daysAgo = new Date(now.getTime() - 90L * 24 * 60 * 60 * 1000);

                String oldDate = new SimpleDateFormat("yyyy-MM-dd").format(daysAgo);
                String[] selectionArgs={oldDate};
                cr.delete(FlottoDbContract.buildReceipts(), FlottoDbContract.ReceiptTableColumns.DATE_COL + " <= ?",
                        selectionArgs);
                return null;
            }
        };

        cleanupTask.execute();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moveTessFile();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        cleanupOldEntries();
        StatisticsCenter.initialize(this);
        //StatisticsCenter.updateStatistics();
        mAdapter = new ReceiptsAdapter(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.receiptRecycler);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }

        });

        getLoaderManager().initLoader(RECEIPTS_LOADER, null, this);


        if (mGoogleAPIClient == null) {
            mGoogleAPIClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, FetchFlottoResultsService.class);
        mPendingIntent = PendingIntent.getService(this, 0, intent, 0);

        mAlarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                0, AlarmManager.INTERVAL_HALF_DAY, mPendingIntent);
        mTess = new TessBaseAPI();
        mTess.init(getExternalFilesDir(null).getAbsolutePath(), "eng");
        mTess.setVariable("tessedit_char_whitelist", "01234567890TOALD.,-/");
    }

    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
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
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File storageDir = getExternalFilesDir(null);

        File image = new File(storageDir, imageFileName);

        image.createNewFile();
        return image;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {

            int theSum = 0;
            String theDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            /**
             *  run the image through tesseract
             *
             */
            if (mCurrentPhotoFile != null) {
                Bitmap tessBmp = BitmapFactory.decodeFile(mCurrentPhotoFile);

                mTess.setImage(tessBmp);
                String tessString = mTess.getUTF8Text();

            /* start looking for the sum */
                Pattern sumPattern = Pattern.compile("TOTAL\\s*(\\d+)");
                Matcher m = sumPattern.matcher(tessString);

                while (m.find()) {
                    if (m.groupCount() >= 1)
                        try {
                            theSum = new Integer(m.group(1));
                        }catch (Exception e) {
                            theSum = 0;
                        }
                }
            }
            /*******************************************************************/
            Intent intent = new Intent(this, NewReceiptActivity.class);

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

                intent = NewReceiptActivity.packNewReceiptIntent(
                        this,
                        AddReceiptFragment.INVALID_ID,
                        theSum,
                        theDate,
                        "file://" + mCurrentPhotoFile,
                        mLastLocation
                );

            }
            startActivity(intent);

        } else {
            mCurrentPhotoFile = null;
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            dispatchTakePictureIntent();
        } else if (id == R.id.nav_stats) {
            Intent i = new Intent(this, StatsActivity.class);

            startActivity(i);
        } else if (id == R.id.nav_map) {
            Intent i = new Intent(this, MapsActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_search) {
            Intent i  = new Intent(this, SearchActivity.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        getLoaderManager().restartLoader(RECEIPTS_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, FlottoDbContract.ReceiptTableColumns.buildReceipts(),
                null, null, null, FlottoDbContract.ReceiptTableColumns.DATE_COL + " DESC ");
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            data.moveToFirst();
            mAdapter.swapCursor(data);
            StatisticsCenter.updateStatistics();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_CODE);
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleAPIClient);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        mGoogleAPIClient.connect();
        super.onStart();

    }

    @Override
    protected void onStop() {
        mGoogleAPIClient.disconnect();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mLastLocation = null;

        } else
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleAPIClient);

    }


}

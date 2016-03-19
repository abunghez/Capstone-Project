package com.compiler_error.flotto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private String mCurrentPhotoFile;
    FloatingActionButton mFab;
    ListFragment mListFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }

        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mListFragment = (ListFragment)getSupportFragmentManager().findFragmentById(R.id.mainFragmentHolder);
        if (mListFragment == null) {

            mListFragment = new ListFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.mainFragmentHolder, mListFragment)
                    .commit();
        }
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





    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            dispatchTakePictureIntent();
        } else if (id == R.id.nav_gallery) {

        }  else if (id == R.id.nav_manage) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void dispatchTakePictureIntent() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            /**
             * TODO: run the image through tesseract
             *
             * for now, just display the receipt detail fragment with the thumbnail of the
             * acquired image
             */
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

                intent.putExtra(NewReceiptActivity.EXTRA_IMAGE_PATH, mCurrentPhotoFile);
            }
            startActivity(intent);

        } else {
            mCurrentPhotoFile = null;
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp+ ".jpg";
        File storageDir = getExternalFilesDir(null);

        File image = new File(storageDir, imageFileName);

        image.createNewFile();
        return image;
    }
}

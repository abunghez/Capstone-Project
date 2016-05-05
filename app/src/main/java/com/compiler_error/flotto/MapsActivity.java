package com.compiler_error.flotto;

import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ProgressBar;

import com.compiler_error.flotto.data.OnDataReadyListener;
import com.compiler_error.flotto.data.StatisticsCenter;
import com.compiler_error.flotto.data.Area;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final String EXTRA_FOCUS_LOCATION
            = "com.compiler_error.flotto.MapsActivity.EXTRA_LOCATION";


    public static final double DEFAULT_LATITUDE = 44.435496;
    public static final double DEFAULT_LONGITUDE =  26.102527;
    public static final int DEFAULT_PADDING_DP = 40;
    private GoogleMap mMap;
    private ProgressBar mMapsSpinner;

    private Location mFocusLocation;

    private OnDataReadyListener mOdrl;


    private int getPadding() {
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                DEFAULT_PADDING_DP, getResources().getDisplayMetrics());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        final SupportMapFragment mapFragment  = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);;

        mMapsSpinner = (ProgressBar) findViewById(R.id.mapsSpinner);
        mMapsSpinner.setVisibility(View.VISIBLE);


        mOdrl = new OnDataReadyListener() {
            @Override
            public void onDataReady() {
                mMapsSpinner.setVisibility(View.GONE);

                mapFragment.getMapAsync(MapsActivity.this);

            }
        };

        StatisticsCenter.addOnDataReadyListener(mOdrl);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        Intent i = getIntent();

        mFocusLocation = i.getParcelableExtra(EXTRA_FOCUS_LOCATION);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney")).showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/


        if (mFocusLocation == null) {
            /* move camera to a rectangle containing all areas */
            ArrayList<Area> areas = StatisticsCenter.getAreas();
            Area area;
            LatLngBounds bounds;

            double minLat, minLong, maxLat, maxLong;

            if (areas.isEmpty()) {

                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)));
                mMap.moveCamera(CameraUpdateFactory.zoomTo(10));

            } else {
                area = areas.get(0);
                minLat = maxLat = area.getLocation().getLatitude();
                minLong = maxLong = area.getLocation().getLongitude();
                for (int i = 0; i < areas.size(); i++) {
                    area = areas.get(i);
                    if (area.getLocation().getLatitude() > maxLat) {
                        maxLat = area.getLocation().getLatitude();
                    }
                    if (area.getLocation().getLatitude() < minLat) {
                        minLat = area.getLocation().getLatitude();
                    }

                    if (area.getLocation().getLongitude() > maxLong) {
                        maxLong = area.getLocation().getLongitude();
                    }
                    if (area.getLocation().getLongitude() < minLong) {
                        minLong = area.getLocation().getLongitude();
                    }
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(area.getLocation().getLatitude(), area.getLocation().getLongitude()))
                            .title(String.valueOf(area.getSum()))
                    ).showInfoWindow();
                }

                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(
                        new LatLngBounds(new LatLng(minLat, minLong), new LatLng(maxLat, maxLong)),
                        getPadding()
                ));
            }
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mFocusLocation.getLatitude(), mFocusLocation.getLongitude())));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(10));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mOdrl != null) {
            StatisticsCenter.removeOnDataReadyListener(mOdrl);
        }
    }
}

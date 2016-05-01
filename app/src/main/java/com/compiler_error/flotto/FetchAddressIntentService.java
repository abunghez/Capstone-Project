package com.compiler_error.flotto;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by andrei on 30.03.2016.
 */
public class FetchAddressIntentService extends IntentService {

    private static final String TAG="FetchAddressIntentService";
    protected ResultReceiver mReceiver;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public FetchAddressIntentService(String name) {
        super(name);
    }

    public FetchAddressIntentService() { super("");}

    @Override
    protected void onHandleIntent(Intent intent) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        Log.d(TAG, "onHandleIntent");
        Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);
        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);

        } catch (IOException e) {
            addresses = null;
        } catch (IllegalArgumentException ex) {
            addresses = null;
        }

        if (addresses == null) {
            deliverResultToReceiver(Constants.FAILURE_RESULT, "");
        } else {
            String fullAddr="";
            int lastIdx;
            for (int i = 0; i <= addresses.get(0).getMaxAddressLineIndex(); i++) {
                fullAddr = fullAddr+addresses.get(0).getAddressLine(i) +", ";
            }
            lastIdx = fullAddr.lastIndexOf(", ");
            if (lastIdx>0)
                fullAddr = fullAddr.substring(0, lastIdx);

            deliverResultToReceiver(Constants.SUCCESS_RESULT, fullAddr);
        }

    }

    public final class Constants {
        public static final int SUCCESS_RESULT = 0;
        public static final int FAILURE_RESULT = 1;
        public static final String PACKAGE_NAME =
                "com.compiler_error.flotto";
        public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
        public static final String RESULT_DATA_KEY = PACKAGE_NAME +
                ".RESULT_DATA_KEY";
        public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
                ".LOCATION_DATA_EXTRA";
    }
    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);
    }
}

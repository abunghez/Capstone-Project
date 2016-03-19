package com.compiler_error.flotto;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by andrei on 19.03.2016.
 */
public class NewReceiptActivity extends FragmentActivity{
    public final static String EXTRA_IMAGE_PATH="com.compiler_error.flotto.imagepath";

    AddReceiptFragment mReceiptFragment;
    String mImagePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent;
        Bundle fragmentArgs;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_receipt);
        intent = getIntent();

        mImagePath = intent.getStringExtra(EXTRA_IMAGE_PATH);
        mReceiptFragment = new AddReceiptFragment();
        fragmentArgs = new Bundle();
        if (mImagePath!=null)
            fragmentArgs.putString(AddReceiptFragment.PHOTO_PATH_KEY, mImagePath);

        mReceiptFragment.setArguments(fragmentArgs);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.newReceiptFragmentHolder, mReceiptFragment)
                .commit();

    }
}

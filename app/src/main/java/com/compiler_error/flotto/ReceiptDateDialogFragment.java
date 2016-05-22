package com.compiler_error.flotto;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by andrei on 21.05.2016.
 */
public abstract class ReceiptDateDialogFragment extends DialogFragment implements
        DatePickerDialog.OnDateSetListener {

    public final String KEY_DATE = "KEY_DATE";
    String dateString;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        int year, month, day;

        dateString = getArguments().getString(KEY_DATE);
        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            date = new Date();
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(),this,year,month,day);
    }
}

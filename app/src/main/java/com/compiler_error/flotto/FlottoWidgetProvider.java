package com.compiler_error.flotto;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.widget.RemoteViews;

import com.compiler_error.flotto.data.FlottoDbContract;

/**
 * Created by andrei on 22.05.2016.
 */
public class FlottoWidgetProvider extends AppWidgetProvider{


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        ContentResolver cr = context.getContentResolver();
        Cursor avgSpent = cr.query(FlottoDbContract.buildAvgDaily(), null, null, null, null);
        int valIndex, val=0;

        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.statcard_layout);

            views.setTextViewText(R.id.statDescriptionTextView, context.getString(R.string.daily_avg_description));
            if (avgSpent != null && avgSpent.moveToFirst()) {
                valIndex = avgSpent.getColumnIndex("AVG_SUM");
                val = avgSpent.getInt(valIndex);
            }

            Intent intent = new Intent(context, StatsActivity.class);
            PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);

            views.setTextViewText(R.id.statValueTextView, String.valueOf(val));
            views.setTextColor(R.id.statDescriptionTextView, ContextCompat.getColor(context, R.color.primary_text));

            views.setOnClickPendingIntent(R.id.statDescriptionTextView, pi);
            views.setOnClickPendingIntent(R.id.statValueTextView, pi);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

    }
}

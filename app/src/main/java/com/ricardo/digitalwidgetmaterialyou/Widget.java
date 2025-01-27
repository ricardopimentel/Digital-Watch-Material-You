package com.ricardo.digitalwidgetmaterialyou;

import android.Manifest;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;

public class Widget extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Atualiza todos os widgets
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        // Cria uma nova instância de RemoteViews
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

        // Obtém a data e hora atuais
        String dataAtual = GetData();

        // Define o texto no TextView (tvData)
        views.setTextViewText(R.id.tvData, dataAtual);

        // Intent para abrir a Despertador.java
        Intent intent = new Intent(context, Despertador.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Associar o PendingIntent ao clique do widget
        views.setOnClickPendingIntent(R.id.Widget, pendingIntent);

        // Atualiza o widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private String GetData() {
        String currentDateString = DateFormat.getDateInstance().format(new Date());
        return currentDateString;
    }
}

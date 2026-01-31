package com.ricardo.digitalwidgetmaterialyou;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Widget extends AppWidgetProvider {

    private static final String TAG = "DigitalWidget";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (AlarmManager.ACTION_NEXT_ALARM_CLOCK_CHANGED.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), Widget.class.getName());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
            onUpdate(context, appWidgetManager, appWidgetIds);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, true);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId,
            Bundle newOptions) {
        updateAppWidget(context, appWidgetManager, appWidgetId, false);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId,
            boolean fetchWeather) {
        // Load properties with fallback to ID 0
        // Font selection removed, defaulting to Modern layout
        int layoutId = R.layout.widget_modern;
        RemoteViews views = new RemoteViews(context.getPackageName(), layoutId);

        // Click Action -> Always Open MenuActivity
        Intent intent = new Intent(context, MenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Use appWidgetId as requestCode to ensure unique PendingIntent per widget
        PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_container, pendingIntent);

        // --- Background Logic ---
        String bgShape = loadPref(context, appWidgetId, "bg_shape", "organic");
        String bgStyle = loadPref(context, appWidgetId, "bg_style", "default");

        int backgroundRes = 0;
        boolean applyTint = false;
        int tintColor = 0;

        if ("glass".equals(bgStyle)) {
            if ("rounded".equals(bgShape))
                backgroundRes = R.drawable.bg_glass_rounded;
            else if ("square".equals(bgShape))
                backgroundRes = R.drawable.bg_glass_square;
            else if ("circular".equals(bgShape))
                backgroundRes = R.drawable.bg_glass_circle;
            else
                backgroundRes = R.drawable.bg_glass_organic;
        } else {
            if ("rounded".equals(bgShape))
                backgroundRes = R.drawable.bg_rounded;
            else if ("square".equals(bgShape))
                backgroundRes = R.drawable.bg_square;
            else if ("circular".equals(bgShape))
                backgroundRes = R.drawable.bg_circle;
            else
                backgroundRes = R.drawable.bg_organic; // Default (Organic)

            if ("transparent".equals(bgStyle))
                backgroundRes = 0;
            else if ("black_50".equals(bgStyle)) {
                tintColor = android.graphics.Color.parseColor("#80000000");
                applyTint = true;
            } else if ("white_50".equals(bgStyle)) {
                tintColor = android.graphics.Color.parseColor("#80FFFFFF");
                applyTint = true;
            }
        }

        if (backgroundRes != 0)
            views.setInt(R.id.widget_container, "setBackgroundResource", backgroundRes);
        else
            views.setInt(R.id.widget_container, "setBackgroundResource", 0);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            views.setColorStateList(R.id.widget_container, "setBackgroundTintList",
                    applyTint ? android.content.res.ColorStateList.valueOf(tintColor) : null);
        }

        // --- Color Logic ---
        String colorPref = loadPref(context, appWidgetId, "color", "default");
        int color = 0;
        if ("default".equals(colorPref)) {
            // Do nothing. Let the layout XML define the colors.
            color = 0;
        } else if ("white".equals(colorPref))
            color = android.graphics.Color.WHITE;
        else if ("black".equals(colorPref))
            color = android.graphics.Color.BLACK;
        else if ("red".equals(colorPref))
            color = android.graphics.Color.parseColor("#D32F2F");
        else if ("green".equals(colorPref))
            color = android.graphics.Color.parseColor("#388E3C");
        else if ("blue".equals(colorPref))
            color = android.graphics.Color.parseColor("#1976D2");

        if (color != 0) {
            views.setTextColor(R.id.tvTime, color);
            views.setTextColor(R.id.tvData, color);
            views.setTextColor(R.id.tvAlarm, color);
            views.setTextColor(R.id.tvBattery, color);
            views.setInt(R.id.imgAlarm, "setColorFilter", color);
            views.setInt(R.id.imgBattery, "setColorFilter", color);
            // Color for weather will be set below
        }

        // --- Responsiveness ---
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        int minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        if (minWidth == 0)
            minWidth = 200;
        int minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        if (minHeight == 0)
            minHeight = 100;
        boolean showDate = minHeight >= 70;
        boolean showInfo = minHeight >= 70 && minWidth >= 110;

        views.setViewVisibility(R.id.tvData, showDate ? View.VISIBLE : View.GONE);

        // --- Weather Logic ---
        String weatherEnabled = loadPref(context, appWidgetId, "weather_enabled", "false");
        String alarmEnabled = loadPref(context, appWidgetId, "alarm_enabled", "true");
        String batteryEnabled = loadPref(context, appWidgetId, "battery_enabled", "true");

        // Always load cached values to display
        String cachedTemp = loadPref(context, appWidgetId, "weather_temp", "--");
        String cachedCodeStr = loadPref(context, appWidgetId, "weather_code", "0");
        String cachedDayStr = loadPref(context, appWidgetId, "weather_is_day", "true");
        int cachedCode = 0;
        try {
            cachedCode = Integer.parseInt(cachedCodeStr);
        } catch (Exception e) {
        }
        boolean cachedDay = "true".equals(cachedDayStr);

        boolean showWeather = "true".equals(weatherEnabled) && showInfo;
        boolean useBottomLayout = "true".equals(alarmEnabled) && "true".equals(batteryEnabled) && showWeather;

        if (showWeather) {
            // Set data on BOTH views so it's ready
            views.setTextViewText(R.id.tvWeatherInline, cachedTemp);
            views.setImageViewResource(R.id.imgWeatherInline, WeatherHelper.getIconRes(cachedCode, cachedDay));

            views.setTextViewText(R.id.tvWeatherBottom, cachedTemp);
            views.setImageViewResource(R.id.imgWeatherBottom, WeatherHelper.getIconRes(cachedCode, cachedDay));

            if (color != 0) {
                views.setTextColor(R.id.tvWeatherInline, color);
                views.setInt(R.id.imgWeatherInline, "setColorFilter", color);

                views.setTextColor(R.id.tvWeatherBottom, color);
                views.setInt(R.id.imgWeatherBottom, "setColorFilter", color);
            }

            // Toggle Visibility
            if (useBottomLayout) {
                views.setViewVisibility(R.id.llWeatherInline, View.GONE);
                views.setViewVisibility(R.id.llWeatherBottom, View.VISIBLE);
            } else {
                views.setViewVisibility(R.id.llWeatherInline, View.VISIBLE);
                views.setViewVisibility(R.id.llWeatherBottom, View.GONE);
            }

            if (fetchWeather) {
                WeatherHelper.getWeather(context, new WeatherHelper.WeatherCallback() {
                    @Override
                    public void onWeatherLoaded(String temp, int weatherCode, boolean isDay) {
                        WidgetConfigActivity.savePref(context, appWidgetId, "weather_temp", temp);
                        WidgetConfigActivity.savePref(context, appWidgetId, "weather_code",
                                String.valueOf(weatherCode));
                        WidgetConfigActivity.savePref(context, appWidgetId, "weather_is_day", isDay ? "true" : "false");
                        // Refresh UI without fetching again
                        updateAppWidget(context, appWidgetManager, appWidgetId, false);
                    }

                    @Override
                    public void onError(String error) {
                        android.util.Log.e("WidgetWeather", "Error: " + error);
                        WidgetConfigActivity.savePref(context, appWidgetId, "weather_temp", "Err");
                        WidgetConfigActivity.savePref(context, appWidgetId, "weather_code", "-1");
                        updateAppWidget(context, appWidgetManager, appWidgetId, false);
                    }
                });
            }
        } else {
            views.setViewVisibility(R.id.llWeatherInline, View.GONE);
            views.setViewVisibility(R.id.llWeatherBottom, View.GONE);
        }

        // --- Alarm ---
        // Loaded above
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        AlarmManager.AlarmClockInfo nextAlarm = (alarmManager != null) ? alarmManager.getNextAlarmClock() : null;
        if (nextAlarm != null && showInfo && "true".equals(alarmEnabled)) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String alarmTime = sdf.format(new Date(nextAlarm.getTriggerTime()));
            views.setTextViewText(R.id.tvAlarm, alarmTime);
            views.setViewVisibility(R.id.llAlarm, View.VISIBLE);
        } else {
            views.setViewVisibility(R.id.llAlarm, View.GONE);
        }

        // --- Battery ---
        // Loaded above
        try {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.getApplicationContext().registerReceiver(null, ifilter);
            if (batteryStatus != null && showInfo && "true".equals(batteryEnabled)) {
                int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                if (level != -1 && scale != -1) {
                    views.setTextViewText(R.id.tvBattery, (int) ((level / (float) scale) * 100) + "%");
                    views.setViewVisibility(R.id.llBattery, View.VISIBLE);
                }
            } else {
                views.setViewVisibility(R.id.llBattery, View.GONE);
            }
        } catch (Exception e) {
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static String loadPref(Context context, int appWidgetId, String key, String defaultValue) {
        String value = WidgetConfigActivity.loadPref(context, appWidgetId, key, "sys_def");
        // android.util.Log.d(TAG, "loadPref raw: ID=" + appWidgetId + " Key=" + key + "
        // Val=" + value);

        // 1. If specific widget value is MISSING ("sys_def"), use Global (ID 0).
        if ("sys_def".equals(value)) {
            value = WidgetConfigActivity.loadPref(context, 0, key, defaultValue);
        }

        // 2. If specific widget value is EXPLICITLY "default", check Global.
        // (Unless we are loading Global itself, i.e. appWidgetId == 0)
        // 2. Logic removed: Explicit "default" should mean "Original Layout", not
        // "Inherit Global".
        // If the user wants Global, we'd need a separate "Use Global" option.
        // For now, "Default" = Layout Colors.

        // 3. Final safety: if still "sys_def", use hardcoded default
        if ("sys_def".equals(value)) {
            value = defaultValue;
        }

        return value;
    }
}

package com.ricardo.digitalwidgetmaterialyou;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

public class WidgetConfigActivity extends androidx.appcompat.app.AppCompatActivity {

    private static final String PREFS_NAME = "com.ricardo.digitalwidgetmaterialyou.Widget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    // State
    // Font removed
    private String mColor = "default";
    private String mShape = "organic";
    private String mStyle = "default";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
        setContentView(R.layout.activity_widget_config);

        // Toolbar Setup
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish()); // Handle Back Click

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            mAppWidgetId = 0;
        }

        // Load Prefs
        // Load Prefs
        // Font logic removed

        mColor = loadPref(this, mAppWidgetId, "color", "default");
        if ("sys_def".equals(mColor))
            mColor = "default";

        mShape = loadPref(this, mAppWidgetId, "bg_shape", "organic");
        if ("sys_def".equals(mShape))
            mShape = "organic";

        mStyle = loadPref(this, mAppWidgetId, "bg_style", "default");
        if ("sys_def".equals(mStyle))
            mStyle = "default";

        // UI Setup
        // UI Setup
        updateUI();

        // Listeners - Font REMOVED

        // Listeners - Color
        findViewById(R.id.optColorDefault).setOnClickListener(v -> {
            mColor = "default";
            updateUI();
        });
        findViewById(R.id.optColorWhite).setOnClickListener(v -> {
            mColor = "white";
            updateUI();
        });
        findViewById(R.id.optColorBlack).setOnClickListener(v -> {
            mColor = "black";
            updateUI();
        });
        findViewById(R.id.optColorRed).setOnClickListener(v -> {
            mColor = "red";
            updateUI();
        });
        findViewById(R.id.optColorGreen).setOnClickListener(v -> {
            mColor = "green";
            updateUI();
        });
        findViewById(R.id.optColorBlue).setOnClickListener(v -> {
            mColor = "blue";
            updateUI();
        });

        // Listeners - Shape
        findViewById(R.id.optShapeOrganic).setOnClickListener(v -> {
            mShape = "organic";
            updateUI();
        });
        findViewById(R.id.optShapeRounded).setOnClickListener(v -> {
            mShape = "rounded";
            updateUI();
        });
        findViewById(R.id.optShapeSquare).setOnClickListener(v -> {
            mShape = "square";
            updateUI();
        });
        findViewById(R.id.optShapeCircular).setOnClickListener(v -> {
            mShape = "circular";
            updateUI();
        });

        // Listeners - Style
        findViewById(R.id.optStyleDefault).setOnClickListener(v -> {
            mStyle = "default";
            updateUI();
        });
        findViewById(R.id.optStyleTransparent).setOnClickListener(v -> {
            mStyle = "transparent";
            updateUI();
        });
        findViewById(R.id.optStyleGlass).setOnClickListener(v -> {
            mStyle = "glass";
            updateUI();
        });
        findViewById(R.id.optStyleBlack50).setOnClickListener(v -> {
            mStyle = "black_50";
            updateUI();
        });
        findViewById(R.id.optStyleWhite50).setOnClickListener(v -> {
            mStyle = "white_50";
            updateUI();
        });

        // Switches
        final Switch swAlarm = findViewById(R.id.swAlarm);
        final Switch swBattery = findViewById(R.id.swBattery);
        final Switch swWeather = findViewById(R.id.swWeather);

        String alarmEnabled = loadPref(this, mAppWidgetId, "alarm_enabled", "sys_def");
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            mAppWidgetId = 0; // Use 0 for "Global/Default"
        }

        // Restore existing preferences or load Global Defaults
        // Try specific, fallback to global (0) if not found ("sys_def")
        // Restore existing preferences or load Global Defaults
        // Font logic removed

        // Load Banner Ad
        com.google.android.gms.ads.AdView adView = findViewById(R.id.adViewConfig);
        com.google.android.gms.ads.AdRequest adRequest = new com.google.android.gms.ads.AdRequest.Builder().build();
        adView.loadAd(adRequest);

        String color = loadPref(this, mAppWidgetId, "color", "sys_def");
        if ("sys_def".equals(color) && mAppWidgetId != 0) {
            color = loadPref(this, 0, "color", "default");
        } else if ("sys_def".equals(color)) {
            color = "default";
        }
        mColor = color;

        String shape = loadPref(this, mAppWidgetId, "bg_shape", "sys_def");
        if ("sys_def".equals(shape) && mAppWidgetId != 0)
            shape = loadPref(this, 0, "bg_shape", "organic");
        if ("sys_def".equals(shape))
            shape = "organic";
        mShape = shape;

        String style = loadPref(this, mAppWidgetId, "bg_style", "sys_def");
        if ("sys_def".equals(style) && mAppWidgetId != 0)
            style = loadPref(this, 0, "bg_style", "default");
        if ("sys_def".equals(style))
            style = "default";
        mStyle = style;

        String batteryEnabled = loadPref(this, mAppWidgetId, "battery_enabled", "sys_def");
        String weatherEnabled = loadPref(this, mAppWidgetId, "weather_enabled", "sys_def");

        if ("sys_def".equals(alarmEnabled))
            alarmEnabled = "true";
        if ("sys_def".equals(batteryEnabled))
            batteryEnabled = "true";
        if ("sys_def".equals(weatherEnabled))
            weatherEnabled = "false";

        swAlarm.setChecked("true".equals(alarmEnabled));
        swBattery.setChecked("true".equals(batteryEnabled));
        swWeather.setChecked("true".equals(weatherEnabled));

        Button btnSave = findViewById(R.id.btnSave);
        if (mAppWidgetId == 0) {
            btnSave.setText("Salvar Configuração Padrão");
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Context context = WidgetConfigActivity.this;

                // Font save removed
                savePref(context, mAppWidgetId, "color", mColor);
                savePref(context, mAppWidgetId, "bg_shape", mShape);
                savePref(context, mAppWidgetId, "bg_style", mStyle);
                savePref(context, mAppWidgetId, "alarm_enabled", swAlarm.isChecked() ? "true" : "false");
                savePref(context, mAppWidgetId, "battery_enabled", swBattery.isChecked() ? "true" : "false");
                savePref(context, mAppWidgetId, "weather_enabled", swWeather.isChecked() ? "true" : "false");

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

                if (mAppWidgetId != 0) {
                    int[] appWidgetIds = new int[] { mAppWidgetId };
                    new Widget().onUpdate(context, appWidgetManager, appWidgetIds);

                    Intent resultValue = new Intent();
                    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                    setResult(RESULT_OK, resultValue);
                } else {
                    ComponentName thisAppWidget = new ComponentName(context.getPackageName(), Widget.class.getName());
                    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
                    for (int id : appWidgetIds) {
                        // Font save removed
                        savePref(context, id, "color", mColor);
                        savePref(context, id, "bg_shape", mShape);
                        savePref(context, id, "bg_style", mStyle);
                        savePref(context, id, "alarm_enabled", swAlarm.isChecked() ? "true" : "false");
                        savePref(context, id, "battery_enabled", swBattery.isChecked() ? "true" : "false");
                        savePref(context, id, "weather_enabled", swWeather.isChecked() ? "true" : "false");
                    }
                    new Widget().onUpdate(context, appWidgetManager, appWidgetIds);
                    setResult(RESULT_OK);
                }
                finish();
            }
        });
    }

    private void updateUI() {
        // Font checks removed

        setCheck(R.id.checkColorDefault, mColor.equals("default"));
        setCheck(R.id.checkColorWhite, mColor.equals("white"));
        setCheck(R.id.checkColorBlack, mColor.equals("black"));
        setCheck(R.id.checkColorRed, mColor.equals("red"));
        setCheck(R.id.checkColorGreen, mColor.equals("green"));
        setCheck(R.id.checkColorBlue, mColor.equals("blue"));

        setCheck(R.id.checkShapeOrganic, mShape.equals("organic"));
        setCheck(R.id.checkShapeRounded, mShape.equals("rounded"));
        setCheck(R.id.checkShapeSquare, mShape.equals("square"));
        setCheck(R.id.checkShapeCircular, mShape.equals("circular"));

        setCheck(R.id.checkStyleDefault, mStyle.equals("default"));
        setCheck(R.id.checkStyleTransparent, mStyle.equals("transparent"));
        setCheck(R.id.checkStyleGlass, mStyle.equals("glass"));
        setCheck(R.id.checkStyleBlack50, mStyle.equals("black_50"));
        setCheck(R.id.checkStyleWhite50, mStyle.equals("white_50"));
    }

    private void setCheck(int resId, boolean checked) {
        View v = findViewById(resId);
        if (v != null)
            v.setVisibility(checked ? View.VISIBLE : View.GONE);
    }

    static void savePref(Context context, int appWidgetId, String key, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId + "_" + key, text);
        prefs.apply();
    }

    static String loadPref(Context context, int appWidgetId, String key, String defaultVal) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(PREF_PREFIX_KEY + appWidgetId + "_" + key, defaultVal);
    }
}

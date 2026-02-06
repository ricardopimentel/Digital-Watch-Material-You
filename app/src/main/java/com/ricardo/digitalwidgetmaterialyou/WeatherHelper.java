package com.ricardo.digitalwidgetmaterialyou;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeatherHelper {

    private static final String API_URL = "https://api.open-meteo.com/v1/forecast?latitude=%s&longitude=%s&current_weather=true";
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Handler handler = new Handler(Looper.getMainLooper());

    public interface WeatherCallback {
        void onWeatherLoaded(String temp, int weatherCode, boolean isDay);

        void onError(String error);
    }

    public static void getWeather(Context context, final WeatherCallback callback) {
        // 1. Get Location (Last Known)
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location location = null;
        try {
            // Smart Strategy: Check ALL enabled providers for the most recent position
            java.util.List<String> providers = lm.getProviders(true);
            long bestTime = 0;
            for (String provider : providers) {
                Location l = lm.getLastKnownLocation(provider);
                if (l != null) {
                    if (location == null || l.getTime() > bestTime) {
                        location = l;
                        bestTime = l.getTime();
                    }
                }
            }
        } catch (SecurityException e) {
            callback.onError("Permission denied");
            return;
        }

        // --- NEW: Location Fallback & Caching ---
        if (location != null) {
            // Save valid location
            saveLocation(context, location);
        } else {
            // Try to load cached location
            location = loadLocation(context);
        }

        if (location == null) {
            callback.onError("Location unavailable");
            // Default to Sao Paulo for testing if needed? No, error is better.
            return;
        }

        final double lat = location.getLatitude();
        final double lon = location.getLongitude();

        // 2. Fetch Data in Background
        executor.execute(() -> {
            try {
                String urlString = String.format(java.util.Locale.US, API_URL, lat, lon);
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(15000);

                int responseCode = conn.getResponseCode();

                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    reader.close();

                    JSONObject json = new JSONObject(result.toString());
                    JSONObject current = json.getJSONObject("current_weather");
                    double temp = current.getDouble("temperature");
                    int code = current.getInt("weathercode");
                    int isDay = current.getInt("is_day");

                    String tempStr = String.format("%.0f°C", temp);

                    handler.post(() -> callback.onWeatherLoaded(tempStr, code, isDay == 1));
                } else {
                    handler.post(() -> callback.onError("HTTP " + responseCode));
                }
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    // Helper to map code to icon resource
    public static int getIconRes(int code, boolean isDay) {
        // Simplified WMO Code mapping
        // 0: Clear
        // 1,2,3: Cloudy
        // 45,48: Fog
        // 51-67: Rain
        // 71-77: Snow
        // 95-99: Storm

        switch (code) {
            case 0:
                return isDay ? R.drawable.ic_weather_clear_day : R.drawable.ic_weather_clear_night;
            case 1:
            case 2:
                return isDay ? R.drawable.ic_weather_partly_cloudy : R.drawable.ic_weather_partly_cloudy; // Or separate
                                                                                                          // night if
                                                                                                          // available
            case 3:
                return R.drawable.ic_weather_cloudy;
            case 45:
            case 48:
                return R.drawable.ic_weather_fog;
            case 51:
            case 53:
            case 55:
                return R.drawable.ic_weather_drizzle;
            case 61:
            case 63:
            case 65:
                return R.drawable.ic_weather_rain;
            case 71:
            case 73:
            case 75:
                return R.drawable.ic_weather_snow;
            case 95:
            case 96:
            case 99:
                return R.drawable.ic_weather_storm;
            default:
                return R.drawable.ic_weather_unknown; // Fallback
        }
    }

    private static void saveLocation(Context context, Location location) {
        context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
                .edit()
                .putString("last_lat", String.valueOf(location.getLatitude()))
                .putString("last_lon", String.valueOf(location.getLongitude()))
                .apply();
    }

    private static Location loadLocation(Context context) {
        android.content.SharedPreferences prefs = context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE);
        String latStr = prefs.getString("last_lat", null);
        String lonStr = prefs.getString("last_lon", null);

        if (latStr != null && lonStr != null) {
            try {
                Location loc = new Location("cached");
                loc.setLatitude(Double.parseDouble(latStr));
                loc.setLongitude(Double.parseDouble(lonStr));
                return loc;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}

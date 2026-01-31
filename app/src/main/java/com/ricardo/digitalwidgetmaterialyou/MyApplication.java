package com.ricardo.digitalwidgetmaterialyou;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.appopen.AppOpenAd;

import java.util.Date;

public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks {

    private AppOpenAdManager appOpenAdManager;
    private Activity currentActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
        MobileAds.initialize(this, initializationStatus -> {
        });
        appOpenAdManager = new AppOpenAdManager();
    }

    private int activityReferences = 0;
    private boolean isActivityChangingConfigurations = false;

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        if (++activityReferences == 1 && !isActivityChangingConfigurations) {
            // App enters foreground
            // User restriction: Only show if opening the App Icon (Info.class)
            // This prevents showing when clicking the Widget (MenuActivity)
            if (activity instanceof Info) {
                if (!appOpenAdManager.isShowingAd) {
                    appOpenAdManager.showAdIfAvailable(activity);
                }
            }
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        isActivityChangingConfigurations = activity.isChangingConfigurations();
        if (--activityReferences == 0 && !isActivityChangingConfigurations) {
            // App enters background
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        currentActivity = null;
    }

    /** Inner class that loads and shows the App Open Ad. */
    private class AppOpenAdManager {
        private static final String LOG_TAG = "AppOpenAdManager";
        // Ad Unit ID provided by user
        private static final String AD_UNIT_ID = "ca-app-pub-6443706394815840/2848341683";

        private AppOpenAd appOpenAd = null;
        private boolean isLoadingAd = false;
        private boolean isShowingAd = false;

        // Keep track of the time an ad was loaded to check for expiration.
        private long loadTime = 0;

        /** Constructor. */
        public AppOpenAdManager() {
            loadAd(MyApplication.this);
        }

        /** Load the ad. */
        public void loadAd(Context context) {
            // Do not load ad if there is an unused ad or one is already loading.
            if (isLoadingAd || isAdAvailable()) {
                return;
            }

            isLoadingAd = true;
            AdRequest request = new AdRequest.Builder().build();
            AppOpenAd.load(
                    MyApplication.this, AD_UNIT_ID, request,
                    new AppOpenAd.AppOpenAdLoadCallback() {
                        @Override
                        public void onAdLoaded(AppOpenAd ad) {
                            Log.d(LOG_TAG, "Ad was loaded.");
                            appOpenAd = ad;
                            isLoadingAd = false;
                            loadTime = (new Date()).getTime();
                        }

                        @Override
                        public void onAdFailedToLoad(LoadAdError loadAdError) {
                            Log.d(LOG_TAG, "Ad failed to load: " + loadAdError.getMessage());
                            isLoadingAd = false;
                        }
                    });
        }

        /** Check if ad exists and can be shown. */
        public boolean isAdAvailable() {
            return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
        }

        /** Show the ad if one is available. */
        public void showAdIfAvailable(@NonNull final Activity activity) {
            if (isShowingAd) {
                Log.d(LOG_TAG, "The app open ad is already showing.");
                return;
            }

            if (!isAdAvailable()) {
                Log.d(LOG_TAG, "The app open ad is not ready yet.");
                loadAd(activity);
                return;
            }

            Log.d(LOG_TAG, "Will show ad.");
            appOpenAd.setFullScreenContentCallback(
                    new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Set the reference to null so isAdAvailable() returns false.
                            appOpenAd = null;
                            isShowingAd = false;
                            Log.d(LOG_TAG, "Ad dismissed.");
                            loadAd(activity);
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            appOpenAd = null;
                            isShowingAd = false;
                            Log.d(LOG_TAG, "Ad failed to show: " + adError.getMessage());
                            loadAd(activity);
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            isShowingAd = true;
                            Log.d(LOG_TAG, "Ad showed.");
                        }
                    });
            isShowingAd = true;
            appOpenAd.show(activity);
        }

        /** Utility method to check if ad has expired (4 hours). */
        private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
            long dateDifference = (new Date()).getTime() - loadTime;
            long numMilliSecondsPerHour = 3600000;
            return (dateDifference < (numMilliSecondsPerHour * numHours));
        }
    }
}

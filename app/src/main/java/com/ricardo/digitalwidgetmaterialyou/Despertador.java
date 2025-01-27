package com.ricardo.digitalwidgetmaterialyou;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.AlarmClock;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

/**
 * Created by Ricardo on 01/05/2015.
 */
public class Despertador extends Activity {

    private InterstitialAd interstitial;

    private AdView adView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.despertador);
        // Criar o anúncio intersticial.
        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId(getResources().getString(R.string.interstinal_ad_unit_id));
        // Criar a solicitação de anúncio.
        AdRequest adRequest = new AdRequest.Builder().build();
        // Começar a carregar o anúncio intersticial.
        interstitial.loadAd(adRequest);
        interstitial.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                interstitial.show();
            }
            @Override
            public void onAdClosed(){
                AbrirAlermeClock();
            }
            @Override
            public void onAdFailedToLoad(int errorCode){
                AbrirAlermeClock();
            }
        });
    }

    public void AbrirAlermeClock(){
        Intent mClockIntent = new Intent(AlarmClock.ACTION_SHOW_ALARMS);
        mClockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mClockIntent);
    }
}

package com.ricardo.digitalwidgetmaterialyou;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

/**
 * Created by Ricardo on 01/05/2015.
 * Updated for modern AdMob API.
 */
public class Despertador extends Activity {

    private static final String TAG = "Despertador"; // Tag para logs
    private InterstitialAd mInterstitialAd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.despertador);

        // Criar a solicitação de anúncio.
        AdRequest adRequest = new AdRequest.Builder().build();

        // Carregar o anúncio intersticial usando o novo método estático.
        InterstitialAd.load(this, getResources().getString(R.string.interstinal_ad_unit_id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // O anúncio foi carregado com sucesso.
                        // Guarde a referência para poder mostrá-lo depois.
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");

                        // Mostra o anúncio imediatamente após carregar.
                        mInterstitialAd.show(Despertador.this);

                        // Configura um callback para saber quando o anúncio for fechado.
                        mInterstitialAd.setFullScreenContentCallback(new com.google.android.gms.ads.FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Chamado quando o anúncio é dispensado.
                                Log.d(TAG, "The ad was dismissed.");
                                AbrirAlermeClock();
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {
                                // Chamado se houver um erro ao mostrar o anúncio.
                                Log.d(TAG, "The ad failed to show.");
                                AbrirAlermeClock();
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Erro ao carregar o anúncio.
                        Log.i(TAG, "onAdFailedToLoad: " + loadAdError.getMessage());
                        mInterstitialAd = null;

                        // Se o anúncio falhou, vá direto para a próxima tela.
                        AbrirAlermeClock();
                    }
                });
    }

    public void AbrirAlermeClock() {
        // Intenção para abrir o aplicativo de alarme do sistema.
        Intent mClockIntent = new Intent(AlarmClock.ACTION_SHOW_ALARMS);
        mClockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mClockIntent);
        finish(); // Finaliza esta activity para não ficar na pilha de navegação.
    }
}

package com.sankofa.royale;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;

import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {

    /** Terre rouge du jeu, visible derrière la barre d'état et la barre de navigation. */
    private static final int FOND = Color.parseColor("#2B100C");

    @Override
    @SuppressWarnings("deprecation")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window w = getWindow();

        // Même comportement sur toutes les versions d'Android : l'application se
        // dessine bord à bord, puis Capacitor (adjustMarginsForEdgeToEdge = "force")
        // repousse la WebView hors de la barre d'état, de l'encoche caméra et de la
        // barre de navigation. Plus rien du jeu ne passe sous l'heure ou la batterie.
        WindowCompat.setDecorFitsSystemWindows(w, false);
        w.getDecorView().setBackgroundColor(FOND);

        // Avant Android 15, les barres ont leur propre couleur : on la cale sur le jeu.
        if (Build.VERSION.SDK_INT < 35) {
            w.setStatusBarColor(FOND);
            w.setNavigationBarColor(FOND);
        }

        // Heure, batterie, réseau et notifications en clair sur fond sombre.
        WindowInsetsControllerCompat bars = WindowCompat.getInsetsController(w, w.getDecorView());
        bars.setAppearanceLightStatusBars(false);
        bars.setAppearanceLightNavigationBars(false);
    }
}

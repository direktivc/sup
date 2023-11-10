package com.example.sup;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SplashScreenActivity extends AppCompatActivity{

    // Spremenljivka za sledenje temu, ali je zaslon zažet
    public static Boolean started = false;

    // Objekt za avtentikacijo Firebase
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Pridobitev instance za avtentikacijo Firebase
        mAuth = FirebaseAuth.getInstance();

        // Preverjanje, ali je uporabnik že prijavljen
        if (mAuth.getCurrentUser() != null) {
            // Če je uporabnik prijavljen, preusmeri na glavno dejavnost (MainActivity)
            Intent intent = new Intent(getApplication(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            // Če uporabnik ni prijavljen, preusmeri na dejavnost za izbiro prijave/registracije (ChooseLoginRegistrationActivity)
            Intent intent = new Intent(getApplication(), ChooseLoginRegistrationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }
}



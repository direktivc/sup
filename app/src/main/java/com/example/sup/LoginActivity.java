package com.example.sup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmail, mPassword; // Elementi za vnos e-pošte in gesla
    private FirebaseAuth mAuth; // Objekt za upravljanje Firebase avtentikacije
    private  FirebaseAuth.AuthStateListener firebaseAuthStateListener; // Poslušalec za spremembe avtentikacijskega stanja

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Nastavitev vsebine aktivnosti s pripadajočo razporeditvijo (layout-om)

        // Inicializacija Firebase avtentikacije
        mAuth = FirebaseAuth.getInstance();

        // Nastavitev poslušalca za spremembe avtentikacijskega stanja
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // Preveri, ali je uporabnik trenutno prijavljen
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    // Če je uporabnik prijavljen, preusmeri na glavno aktivnost
                    Intent intent = new Intent(getApplication(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish(); // Zaključi trenutno aktivnost (LoginActivity)
                }
            }
        };

        // Povezava s komponentami iz razporeditve (layout-a)
        Button mLogin = findViewById(R.id.Login); // Gumb za prijavo
        mEmail = findViewById(R.id.email); // Polje za vnos e-pošte
        mPassword = findViewById(R.id.Password); // Polje za vnos gesla

        // Nastavitev poslušalca za gumb za prijavo
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pridobitev vnesenih podatkov o e-pošti in geslu
                final String email =mEmail.getText().toString();
                final String password =mPassword.getText().toString();
                // Prijava uporabnika s Firebase
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, task -> {
                    if (!task.isSuccessful()){
                        Toast.makeText(LoginActivity.this, "Sign in ERROR", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }
}
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {


    // Elementi za vnos e-pošte, gesla in imena
    private EditText mEmail, mPasword, mName;
    // Objekt za upravljanje Firebase avtentikacije
    private FirebaseAuth mAuth;
    // Poslušalec za spremembe avtentikacijskega stanja
    private  FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);// Nastavitev vsebine aktivnosti s pripadajočo razporeditvijo (layout-om)
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // Preveri, ali je uporabnik trenutno prijavljen
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user!=null)
                {
                    // Če je uporabnik prijavljen, preusmeri na glavno aktivnost
                    Intent intent = new Intent(getApplication(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        };
        // Inicializacija Firebase avtentikacije
        mAuth = FirebaseAuth.getInstance();
        Button mRegistration = findViewById(R.id.Registration);
        // Povezava s komponentami iz razporeditve (layout-a)
        mName = findViewById(R.id.name);
        mEmail = findViewById(R.id.email);
        mPasword = findViewById(R.id.Password);

        mRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            // Poslušalec za obdelavo dogodka klikanja na gumb za registracijo
            public void onClick(View view) {
                // Pridobitev vnesenih podatkov o imenu, e-pošti in geslu
                final String name = mName.getText().toString();
                final String email = mEmail.getText().toString();
                final String password = mPasword.getText().toString();

                // Ustvarjanje novega uporabnika v Firebase z uporabo e-pošte in gesla
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Preveri, ali je registracija uspela
                        if (!task.isSuccessful()) {
                            // Če registracija ni uspela, prikaži sporočilo o napaki
                            Toast.makeText(getApplication(), "Sign in ERROR", Toast.LENGTH_SHORT).show();
                        } else {
                            // Če je registracija uspela, pridobi ID trenutno prijavljenega uporabnika
                            String userId = mAuth.getCurrentUser().getUid();

                            // Dostop do referenčne poti v Realtime Database za trenutnega uporabnika
                            DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

                            // Pripravi podatke o uporabniku za posodobitev v Realtime Database
                            Map<String, Object> userInfo = new HashMap<>();
                            userInfo.put("email", email);
                            userInfo.put("name", name);
                            userInfo.put("profileImageUrl", "default");

                            // Posodobi podatke v Realtime Database
                            currentUserDb.updateChildren(userInfo);
                        }
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
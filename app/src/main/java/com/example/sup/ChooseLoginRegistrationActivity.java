package com.example.sup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

// Aktivnost za izbiro med prijavo in registracijo
public class ChooseLoginRegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Nastavi vsebino na aktivnost_choose_login_registration.xml layout
        setContentView(R.layout.activity_choose_login_registration);

        // Pridobi reference do gumba za prijavo in gumba za registracijo iz layouta
        Button mLogin = findViewById(R.id.Login);
        Button mRegistration = findViewById(R.id.Registration);

        // Nastavi poslušalce dogodkov za gumbe

        // Poslušalec za gumb prijave
        mLogin.setOnClickListener(view -> {
            // Ustvari in inicializira novo namero za preklop na LoginActivity
            Intent intent = new Intent(getApplication(), LoginActivity.class);
            // Zaženi novo aktivnost
            startActivity(intent);
        });

        // Poslušalec za gumb registracije
        mRegistration.setOnClickListener(view -> {
            // Ustvari in inicializira novo namero za preklop na RegistrationActivity
            Intent intent = new Intent(getApplication(), RegistrationActivity.class);
            // Zaženi novo aktivnost
            startActivity(intent);
        });
    }
}

package com.example.sup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.net.Uri;
import android.os.Environment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.io.File;

public class ShowCaptureActivity extends AppCompatActivity {

    // Uporabnikova identifikacija
    String Uid;

    // Objekt za shranjevanje rotiranega bitmappa
    Bitmap rotateBitmap;

    // Izhodni imenik za shranjevanje slik
    private File outputDirectory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Nastavi vsebino pogleda na layout activity_show_capture.xml
        setContentView(R.layout.activity_show_capture);

        // Pridobi dodatne podatke iz prejšnje aktivnosti
        Bundle extras = getIntent().getExtras();

        // Pridobi ime datoteke zajete slike
        String captureFileName = extras.getString("captureFileName");

        // Nastavi na null, dokler ne preverimo, ali obstaja datoteka
        rotateBitmap = null;

        // Preveri, ali je ime datoteke pravilno nastavljeno
        if (captureFileName != null) {
            // Poišči element ImageView v razporejevalniku z ID-jem imageCaptured
            ImageView image = findViewById(R.id.imageCaptured);

            // Ustvari pot do shranjene slike iz imena datoteke
            File imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "YourAppDirectoryName");

            // Preveri, ali datoteka obstaja
            if (imageFile.exists()) {
                // Dekodiraj sliko iz datoteke v bitmapi
                Bitmap decodeBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());

                // Rotiraj bitmapo
                rotateBitmap = rotate(decodeBitmap);

                // Nastavi rotirano bitmapo v ImageView
                image.setImageBitmap(rotateBitmap);
            }
        }

        // Pridobi uporabnikov UID
        Uid = FirebaseAuth.getInstance().getUid();

        // Poveži gumb za zgodbo (story) in nastavi poslušalca dogodkov
        Button mStory = findViewById(R.id.story);
        mStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Po kliku na gumb zaženi funkcijo za shranjevanje v zgodbo
                saveToStory();
            }
        });
    }




    private void saveToStory() {
        // Pridobi referenco na podatkovno bazo Firebase in določi pot do uporabnikove zgodbe
        final DatabaseReference userStoryDb = FirebaseDatabase.getInstance().getReference().child("user").child(Uid).child("story");

        // Generiraj edinstven ključ za vsako shranjeno sliko v zgodbi
        final String key = userStoryDb.push().getKey();

        // Določi pot do shranjenih slik v shrambi Firebase Storage
        StorageReference filePath = FirebaseStorage.getInstance().getReference().child("captures").child(key);

        // Pretvori rotirano bitmapo v byte[]
        byte[] dataToUpload = convertBitmapToByteArray(rotateBitmap);

        // Naloži byte[] na Firebase Storage
        UploadTask uploadTask = filePath.putBytes(dataToUpload);

        // Dodaj poslušalca za obvladovanje uspešnosti naloge nalaganja
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Po uspešnem nalaganju, pridobi URL za dostop do naložene slike
                taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Pridobi URL-je in pretvori v niz
                        String imageURL = uri.toString();

                        // Pridobi trenutni čas v milisekundah
                        Long currentTimestamp = System.currentTimeMillis();

                        // Določi končni čas (tukaj: trenutni čas + 24 ur)
                        Long endTimestamp = currentTimestamp + (24 * 60 * 60 * 1000);

                        // Ustvari mapo z vsemi potrebnimi podatki za shranjevanje v zgodbo
                        Map<String, Object> mapToUpload = new HashMap<>();
                        mapToUpload.put("imageUrl", imageURL);
                        mapToUpload.put("timestampbeg", currentTimestamp);
                        mapToUpload.put("timestampoend", endTimestamp);

                        // Shranjevanje podatkov v podatkovno bazo Firebase
                        userStoryDb.child(key).setValue(mapToUpload);
                    }
                });
            }
        });

        // Dodaj poslušalca za obvladovanje neuspeha naloge nalaganja
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // V primeru neuspeha naloge zapri trenutno aktivnost
                finish();
            }
        });
    }


    private Bitmap rotate(Bitmap decodeBitmap) {
        int w = decodeBitmap.getWidth();
        int h = decodeBitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.setRotate(90);
        return Bitmap.createBitmap(decodeBitmap, 0, 0, w, h, matrix, true);
    }
    // Pretvori Bitmap v byte[]
    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
}

}

package com.example.sup;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.camera.core.ImageCaptureException;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class CameraFragment extends Fragment {

    private PreviewView mPreviewView;  // Pogled za predogled kamere
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;  // Za pridobivanje ponudnika kamere
    final int CAMERA_REQUEST_CODE = 1;  // Koda zahtevka za dovoljenje kamere
    private ImageCapture imageCapture;  // Za zajem slik
    private File outputDirectory;  // Izhodni imenik za shranjevanje slik
    private String currentPhotoPath;  // Pot do trenutne slike

    // Metoda za ustvarjanje novega fragmenta kamere
    public static CameraFragment newInstance() {
        return new CameraFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Napihnemo postavitev za ta fragment
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        // Inicializiramo PreviewView
        mPreviewView = view.findViewById(R.id.surfaceView);
        // Pridobimo izhodni imenik za shranjevanje posnetih slik
        outputDirectory = getOutputDirectory();

        // Preverimo dovoljenje za kamero, če ni dovoljeno, zahtevamo dovoljenje, sicer zaženemo kamero
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            startCamera();
        }

        // Nastavimo gumb za odjavo
        Button mLogout = view.findViewById(R.id.logout);
        mLogout.setOnClickListener(view1 -> LogOut());

        // Nastavimo gumb za zajem slike
        Button mCapture = view.findViewById(R.id.capture);
        mCapture.setOnClickListener(v -> captureImage());

        return view;
    }

    // Metoda za zagon kamere
    private void startCamera() {
        // Inicializiramo ponudnika kamere
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());

        // Dodamo poslušalca za obdelavo inicializacije kamere
        cameraProviderFuture.addListener(() -> {
            try {
                // Pridobimo ponudnika kamere
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                // Nastavimo predogled
                Preview preview = new Preview.Builder().build();
                // Nastavimo izbirnik kamere, da uporablja zadnjo kamero
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                // Povežemo ponudnika kamere z življenjskim ciklom fragmenta
                preview.setSurfaceProvider(mPreviewView.getSurfaceProvider());
                imageCapture = new ImageCapture.Builder()
                        .setTargetRotation(mPreviewView.getDisplay().getRotation())
                        .build();

                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    // Metoda za zajem slike
    private void captureImage() {
        // Ustvarimo datoteko za shranjevanje posnete slike
        File photoFile = new File(outputDirectory, new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis()) + ".jpg");
        // Nastavimo možnosti za shranjevanje posnete slike
        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        // Uporabimo imageCapture za zajem slike
        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(requireContext()), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(ImageCapture.OutputFileResults outputFileResults) {
                // Slika uspešno shranjena
                currentPhotoPath = photoFile.getAbsolutePath();
                Toast.makeText(requireContext(), "Slika shranjena: " + currentPhotoPath, Toast.LENGTH_LONG).show();

                // Pretvorimo shranjeno sliko v byte[]
                byte[] capturedImageData = convertImageToByteArray(photoFile);

                // Zaženemo ShowCaptureActivity in prenesemo ime datoteke
                Intent intent = new Intent(requireContext(), ShowCaptureActivity.class);
                intent.putExtra("captureFileName", photoFile.getName());
                startActivity(intent);
            }

            // Metoda za pretvorbo slike iz datoteke v byte[]
            private byte[] convertImageToByteArray(File imageFile) {
                try {
                    // Odpremo tok za branje iz slike
                    FileInputStream fileInputStream = new FileInputStream(imageFile);

                    // Ustvarimo tok za pisanje v byte array
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                    int bytesRead;
                    byte[] buffer = new byte[1024];

                    // Beremo iz slike v majhnih kosih in pišemo v byte array
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, bytesRead);
                    }

                    // Vrnemo celoten byte array, ki vsebuje podatke slike
                    return byteArrayOutputStream.toByteArray();
                } catch (IOException e) {
                    // Če pride do napake, izpišemo sled in vrnemo null
                    e.printStackTrace();
                    return null;
                }
            }


            @Override
            public void onError(ImageCaptureException exception) {
                exception.printStackTrace();
            }
        });
    }

    // Metoda za odjavo uporabnika iz aplikacije
    private void LogOut() {
        // Odjava iz Firebase avtentikacije
        FirebaseAuth.getInstance().signOut();

        // Ustvarimo namero (intent) za prehod na SplashScreenActivity po odjavi
        Intent intent = new Intent(requireContext(), SplashScreenActivity.class);

        // Dodamo zastavo FLAG_ACTIVITY_CLEAR_TOP, da izpraznimo vrhnje aktivnosti in preprečimo vračanje nazaj
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Zaženemo SplashScreenActivity
        startActivity(intent);

        // Zaključimo trenutno aktivnost (v tem primeru kamero)
        requireActivity().finish();
    }


    // Metoda za pridobitev izhodnega imenika za shranjevanje slik
    private File getOutputDirectory() {
        // Pridobimo osnovni izhodni imenik za shranjevanje medijskih datotek (slik)
        File mediaDir = requireActivity().getExternalMediaDirs()[0];

        // Pridobimo osnovni izhodni imenik za shranjevanje slik v skupno mapo Pictures
        File outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        // Ustvarimo podmapo v Pictures imeniku za shranjevanje slik za našo aplikacijo
        File appDir = new File(outputDir, "YourAppDirectoryName");

        // Preverimo, ali podmapa za našo aplikacijo obstaja
        if (!appDir.exists()) {
            // Če ne obstaja, jo poskusimo ustvariti
            if (!appDir.mkdirs()) {
                // Če ustvarjanje ne uspe, vrnemo osnovni izhodni imenik za shranjevanje medijskih datotek
                return mediaDir;
            }
        }

        // Vrnemo ustvarjeno podmapo za shranjevanje slik za našo aplikacijo
        return appDir;
    }
}

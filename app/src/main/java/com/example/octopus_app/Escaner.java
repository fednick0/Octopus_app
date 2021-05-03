package com.example.octopus_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;


public class Escaner extends AppCompatActivity {

    /**
     * Revisar esto, es el ejemplo diseñado por google en 2017
     * https://github.com/googlesamples/android-vision/blob/master/visionSamples/barcode-reader/app/src/main/java/com/google/android/gms/samples/vision/barcodereader/BarcodeCaptureActivity.java
     */

    private FuncionesUnicas f;
    private boolean firstTime;

    private void inicializar(){
        Toolbar toolbar = findViewById(R.id.escanerToolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        firstTime = true;
        f = new FuncionesUnicas(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escaner);

        inicializar();
        codigodebarras();
    }

    private void codigodebarras() {
        SurfaceView sf = findViewById(R.id.surfaceViews);

        BarcodeDetector bcd = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        final CameraSource cs = new CameraSource
                .Builder(this, bcd)
                .setAutoFocusEnabled(true)
                .build();

        sf.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    cs.start(holder);

                } catch (IOException e) {
                    f.mostrarToast(e.toString());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cs.stop();
            }
        });

        bcd.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> contenido = detections.getDetectedItems();

                if (contenido.size() != 0 && firstTime) {

                    f.vibrar();

                    (new ToneGenerator(AudioManager.STREAM_MUSIC, 100))
                            .startTone(ToneGenerator.TONE_CDMA_PIP,150);

                    firstTime = false;

                    Intent i = new Intent();
                    i.putExtra("valor", contenido.valueAt(0).displayValue);

                    setResult(RESULT_OK, i);

                    finish();
                }
            }
        });
    }
}

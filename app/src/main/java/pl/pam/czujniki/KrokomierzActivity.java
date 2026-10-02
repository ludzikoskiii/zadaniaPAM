package pl.pam.czujniki;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Krokomierz: używa TYPE_STEP_COUNTER, a gdy go brak – wykrywa kroki
 * samodzielnie z akcelerometru (szczyty modułu przyspieszenia).
 */
public class KrokomierzActivity extends AppCompatActivity implements SensorEventListener {

    private static final int KOD_UPRAWNIEN = 1;
    private static final float DLUGOSC_KROKU_M = 0.75f;
    private static final float PROG_KROKU = 11.5f;  // m/s^2
    private static final long MIN_ODSTEP_MS = 300;

    private SensorManager sensorManager;
    private Sensor licznikKrokow, akcelerometr;
    private TextView tvGlowny, tvInfo;

    private float startLicznika = -1;
    private int kroki;
    private boolean powyzejProgu;
    private long ostatniKrok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tekst);
        tvGlowny = findViewById(R.id.tvGlowny);
        tvInfo = findViewById(R.id.tvInfo);
        findViewById(R.id.btnReset).setOnClickListener(v -> {
            kroki = 0;
            startLicznika = -1;
            odswiez();
        });

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        licznikKrokow = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        akcelerometr = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, KOD_UPRAWNIEN);
        }
        odswiez();
    }

    private void odswiez() {
        tvGlowny.setText(String.valueOf(kroki));
        String zrodlo = licznikKrokow != null ? "czujnik kroków" : "akcelerometr";
        tvInfo.setText(String.format("kroków\nDystans: %.2f m\nŹródło: %s",
                kroki * DLUGOSC_KROKU_M, zrodlo));
    }

    private void rejestruj() {
        sensorManager.unregisterListener(this);
        if (licznikKrokow != null) {
            sensorManager.registerListener(this, licznikKrokow, SensorManager.SENSOR_DELAY_UI);
        } else if (akcelerometr != null) {
            sensorManager.registerListener(this, akcelerometr, SensorManager.SENSOR_DELAY_GAME);
        } else {
            tvInfo.setText("Brak czujników do liczenia kroków");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        rejestruj();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] wyniki) {
        super.onRequestPermissionsResult(requestCode, permissions, wyniki);
        if (requestCode == KOD_UPRAWNIEN) rejestruj();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            // Licznik systemowy liczy od restartu – liczymy różnicę od startu ekranu.
            if (startLicznika < 0) startLicznika = event.values[0] - kroki;
            kroki = (int) (event.values[0] - startLicznika);
            odswiez();
        } else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0], y = event.values[1], z = event.values[2];
            double modul = Math.sqrt(x * x + y * y + z * z);
            long teraz = System.currentTimeMillis();
            if (modul > PROG_KROKU && !powyzejProgu) {
                powyzejProgu = true;
                if (teraz - ostatniKrok > MIN_ODSTEP_MS) {
                    ostatniKrok = teraz;
                    kroki++;
                    odswiez();
                }
            } else if (modul < PROG_KROKU - 1.5f) {
                powyzejProgu = false;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}

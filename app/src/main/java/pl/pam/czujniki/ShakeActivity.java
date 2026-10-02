package pl.pam.czujniki;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class ShakeActivity extends AppCompatActivity implements SensorEventListener {

    private static final float PROG_G = 2.5f;      // próg siły potrząśnięcia (w g)
    private static final long ODSTEP_MS = 500;     // minimalny odstęp między wstrząsami

    private SensorManager sensorManager;
    private Sensor akcelerometr;
    private TextView tvGlowny, tvInfo;
    private int licznik;
    private long ostatni;
    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tekst);
        tvGlowny = findViewById(R.id.tvGlowny);
        tvInfo = findViewById(R.id.tvInfo);
        findViewById(R.id.btnReset).setOnClickListener(v -> {
            licznik = 0;
            odswiez();
            findViewById(R.id.root).setBackgroundColor(Color.WHITE);
        });

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        akcelerometr = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        odswiez();
        if (akcelerometr == null) tvInfo.setText("Brak akcelerometru");
    }

    private void odswiez() {
        tvGlowny.setText(String.valueOf(licznik));
        tvInfo.setText("Potrząśnij telefonem!\nLiczba wstrząsów");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (akcelerometr != null) {
            sensorManager.registerListener(this, akcelerometr, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float gX = event.values[0] / SensorManager.GRAVITY_EARTH;
        float gY = event.values[1] / SensorManager.GRAVITY_EARTH;
        float gZ = event.values[2] / SensorManager.GRAVITY_EARTH;
        double sila = Math.sqrt(gX * gX + gY * gY + gZ * gZ);

        long teraz = System.currentTimeMillis();
        if (sila > PROG_G && teraz - ostatni > ODSTEP_MS) {
            ostatni = teraz;
            licznik++;
            odswiez();
            findViewById(R.id.root).setBackgroundColor(
                    Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}

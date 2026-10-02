package pl.pam.czujniki;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PoziomicaActivity extends AppCompatActivity implements SensorEventListener {

    private static final float ALFA = 0.15f; // filtr dolnoprzepustowy

    private SensorManager sensorManager;
    private Sensor akcelerometr;
    private PoziomicaView widok;
    private float ax, ay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        widok = new PoziomicaView(this);
        setContentView(widok);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        akcelerometr = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (akcelerometr == null) {
            Toast.makeText(this, "Brak akcelerometru", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (akcelerometr != null) {
            sensorManager.registerListener(this, akcelerometr, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        ax += ALFA * (event.values[0] - ax);
        ay += ALFA * (event.values[1] - ay);
        widok.ustaw(ax, ay);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}

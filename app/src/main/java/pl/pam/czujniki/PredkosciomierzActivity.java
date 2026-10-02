package pl.pam.czujniki;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/** Prędkościomierz oparty o GPS. */
public class PredkosciomierzActivity extends AppCompatActivity implements LocationListener {

    private static final int KOD_UPRAWNIEN = 2;

    private LocationManager locationManager;
    private TextView tvGlowny, tvInfo;
    private float maxKmh;
    private float dystansM;
    private Location poprzednia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tekst);
        tvGlowny = findViewById(R.id.tvGlowny);
        tvInfo = findViewById(R.id.tvInfo);
        findViewById(R.id.btnReset).setOnClickListener(v -> {
            maxKmh = 0;
            dystansM = 0;
            poprzednia = null;
            pokaz(0);
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        tvGlowny.setText("0.0 km/h");
        tvInfo.setText("Oczekiwanie na sygnał GPS...");
    }

    private boolean maUprawnienia() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    @SuppressLint("MissingPermission")
    private void start() {
        if (!maUprawnienia()) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, KOD_UPRAWNIEN);
            return;
        }
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            tvInfo.setText("Włącz lokalizację (GPS)");
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] wyniki) {
        super.onRequestPermissionsResult(requestCode, permissions, wyniki);
        if (requestCode == KOD_UPRAWNIEN) {
            if (maUprawnienia()) start();
            else tvInfo.setText("Brak uprawnień do lokalizacji");
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        float kmh;
        if (location.hasSpeed()) {
            kmh = location.getSpeed() * 3.6f;
        } else if (poprzednia != null) {
            float dt = (location.getTime() - poprzednia.getTime()) / 1000f;
            kmh = dt > 0 ? location.distanceTo(poprzednia) / dt * 3.6f : 0;
        } else {
            kmh = 0;
        }
        if (poprzednia != null) dystansM += location.distanceTo(poprzednia);
        poprzednia = location;
        maxKmh = Math.max(maxKmh, kmh);
        pokaz(kmh);
    }

    private void pokaz(float kmh) {
        tvGlowny.setText(String.format("%.1f km/h", kmh));
        tvInfo.setText(String.format("Maks.: %.1f km/h\nDystans: %.0f m", maxKmh, dystansM));
    }

    @Override public void onProviderEnabled(@NonNull String provider) { start(); }
    @Override public void onProviderDisabled(@NonNull String provider) {
        tvInfo.setText("Włącz lokalizację (GPS)");
    }
}

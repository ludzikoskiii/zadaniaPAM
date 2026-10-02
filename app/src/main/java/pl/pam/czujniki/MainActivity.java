package pl.pam.czujniki;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        podepnij(R.id.btnPoziomica, PoziomicaActivity.class);
        podepnij(R.id.btnShake, ShakeActivity.class);
        podepnij(R.id.btnKrokomierz, KrokomierzActivity.class);
        podepnij(R.id.btnLista, ListaCzujnikowActivity.class);
        podepnij(R.id.btnPredkosc, PredkosciomierzActivity.class);
    }

    private void podepnij(int id, Class<?> cel) {
        findViewById(id).setOnClickListener(v -> startActivity(new Intent(this, cel)));
    }
}

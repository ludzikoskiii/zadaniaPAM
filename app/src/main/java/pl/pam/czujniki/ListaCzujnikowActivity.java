package pl.pam.czujniki;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ListaCzujnikowActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);

        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> czujniki = sm.getSensorList(Sensor.TYPE_ALL);

        List<String> opisy = new ArrayList<>();
        for (Sensor s : czujniki) {
            opisy.add(String.format("%s\nProducent: %s\nTyp: %s\nZakres: %.2f  Rozdz.: %.4f  Pobór: %.2f mA",
                    s.getName(), s.getVendor(), s.getStringType(),
                    s.getMaximumRange(), s.getResolution(), s.getPower()));
        }
        setTitle("Lista czujników (" + czujniki.size() + ")");

        ListView lista = findViewById(R.id.lista);
        lista.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, opisy));
    }
}

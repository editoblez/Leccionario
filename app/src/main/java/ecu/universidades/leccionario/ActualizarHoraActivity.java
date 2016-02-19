package ecu.universidades.leccionario;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class ActualizarHoraActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_hora);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}

package ecu.universidades.leccionario;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class CrearLeccionarioActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_leccionario);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}

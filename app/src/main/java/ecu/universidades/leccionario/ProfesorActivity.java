package ecu.universidades.leccionario;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class ProfesorActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profesor);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        EditText edit = (EditText) findViewById(R.id.test);
        edit.setText(intent.getStringExtra("Message"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profesor, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_asistencia:
                intent = new Intent(getApplicationContext(), IngresarTurnoActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_change_pass:
                intent = new Intent(getApplicationContext(), ChangePassActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_register_extra_info:
                intent = new Intent(getApplicationContext(), RegistrarObservacionActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void crearTurno() {
        Intent intent = new Intent(getApplicationContext(),IngresarTurnoActivity.class);
        startActivity(intent);
    }
}
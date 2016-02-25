package ecu.universidades.leccionario;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class InstructorActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        EditText edit = (EditText) findViewById(R.id.test);
        edit.setText(intent.getStringExtra("Message"));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_instructor, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_create_user:
                intent = new Intent(getApplicationContext(), PersonaActivity.class);
                intent.putExtra("type", PersonaType.USUARIO);
                startActivity(intent);
                return true;
            case R.id.action_update_user:
                intent = new Intent(getApplicationContext(), ActualiazarUsuarioActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_create_student:
                intent = new Intent(getApplicationContext(), PersonaActivity.class);
                intent.putExtra("type", PersonaType.ESTUDIANTE);
                startActivity(intent);
                return true;
            case R.id.action_update_student:
                intent = new Intent(getApplicationContext(), ActualiazarUsuarioActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_create_asignatura:
                intent = new Intent(getApplicationContext(), CrearAsignaturaActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_update_asignatura:
                intent = new Intent(getApplicationContext(), ActualizarAsignaturaActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_create_grupo:
                intent = new Intent(getApplicationContext(), CrearGrupoActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_update_grupo:
                intent = new Intent(getApplicationContext(), ActualizarGrupoActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_create_leccionario:
                intent = new Intent(getApplicationContext(), CrearLeccionarioActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_update_lecc:
                intent = new Intent(getApplicationContext(), ActualizarLeccionarioActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_create_topic:
                intent = new Intent(getApplicationContext(), CrearTemaActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_update_topic:
                intent = new Intent(getApplicationContext(), ActualizarTemaActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_create_hora:
                intent = new Intent(getApplicationContext(),CrearHoraActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_update_hora:
                intent = new Intent(getApplicationContext(),ActualizarHoraActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_create_incidencia:
                intent = new Intent(getApplicationContext(),CrearIncidenciaActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_update_incidencia:
                intent = new Intent(getApplicationContext(),ActualizarIncidenciaActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createNewUser ()
    {

    }



    private boolean createNewMateria ()
    {
        Intent intent = new Intent(getApplicationContext(), CrearAsignaturaActivity.class);
        startActivity(intent);
        return true;
    }

}

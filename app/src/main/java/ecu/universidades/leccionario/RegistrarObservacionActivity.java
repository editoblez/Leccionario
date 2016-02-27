package ecu.universidades.leccionario;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RegistrarObservacionActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

    Spinner spListEstudianteView;
    Spinner spListIncidenciaView;
    Button btnSaveObservacion;

    ServiceClass serviceClass = null;
    JSONArray jsonArrayIncidencias;
    JSONArray jsonArrayEstudiantes;
    private int idProfesor;

    List<String> listEstudiante = new ArrayList<String>();
    List<String> listIncidencia = new ArrayList<String>();

    int idLeccionario;
    private int idEstudiante;
    private int idIncidencia;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_observacion);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        idProfesor = getIntent().getExtras().getInt("idUsuario");

        btnSaveObservacion = (Button) findViewById(R.id.btnSaveObservacion);
        btnSaveObservacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        spListIncidenciaView = (Spinner) findViewById(R.id.spListIncidenciaView);
        spListIncidenciaView.setOnItemSelectedListener(this);

        spListEstudianteView = (Spinner) findViewById(R.id.spListEstudianteView);
        spListEstudianteView.setOnItemSelectedListener(this);

        fillSpinnerIncidencia();
        fillEstudiante();
        String a;
    }

    private void fillEstudiante() {
        String url = getString(R.string.base_url) + "estudiante/getAllByTurno/" + idProfesor;
        serviceClass = new ServiceClass(this, url,
                HttpRequestType.GET, HttpResponseType.JSONARRAYOBJECT);
        JSONObject response;
        try {
            response = serviceClass.execute().get();
            if (response.isNull("error")) {
                listEstudiante = new ArrayList<String>();
                this.jsonArrayEstudiantes = response.getJSONArray("result");
                for (int i = 0; i < this.jsonArrayEstudiantes.length(); i++) {
                    JSONObject tmpJsonObject = jsonArrayEstudiantes.getJSONObject(i);
                    listEstudiante.add(tmpJsonObject.getString("cedula"));
                    idLeccionario = tmpJsonObject.getInt("idLeccionario");
                }
            } else {
                JsonUtils.makeMessage(this, response.getString("error"));
            }
        } catch (InterruptedException e) {
            JsonUtils.makeMessage(this, e.getMessage());
        } catch (ExecutionException e) {
            JsonUtils.makeMessage(this, e.getMessage());
        } catch (JSONException e) {
            JsonUtils.makeMessage(this, e.getMessage());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, listEstudiante);
        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        this.spListEstudianteView.setAdapter(dataAdapter);
        this.serviceClass = null;
    }

    private void save() {
        String url = getString(R.string.base_url) + "lhoraincidenciaestudiante/create";// esta logica es para ti mi amor
        //String url = getString(R.string.base_url) + "/" + this.idSelectedTema;(no se cual es la tabla q va a llenar)
        JSONObject params = new JSONObject();
        try {
            params.put("idEstudiante", this.idEstudiante);
            params.put("idIncidencia", this.idIncidencia);
            params.put("idLeccionario", this.idLeccionario);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        serviceClass = new ServiceClass(this, url,
                HttpRequestType.POST, HttpResponseType.NONE, params);
        JSONObject response;
        try {
            response = serviceClass.execute().get();
            if (response.isNull("error")) {
                JsonUtils.makeMessage(this, getString(R.string.success_msg));
            } else {
                JsonUtils.makeMessage(this, response.getString("error"));
            }
        } catch (InterruptedException e) {
            JsonUtils.makeMessage(this, e.getMessage());
        } catch (ExecutionException e) {
            JsonUtils.makeMessage(this, e.getMessage());
        } catch (JSONException e) {
            JsonUtils.makeMessage(this, e.getMessage());
        }
        this.serviceClass = null;
    }


    private void fillSpinnerIncidencia() {
        String url = getString(R.string.base_url) + "incidencia/getAll";
        serviceClass = new ServiceClass(this, url,
                HttpRequestType.GET, HttpResponseType.JSONARRAYOBJECT);
        JSONObject response;
        try {
            response = serviceClass.execute().get();
            if (response.isNull("error")) {
                listIncidencia = new ArrayList<String>();
                this.jsonArrayIncidencias = response.getJSONArray("result");
                for (int i = 0; i < this.jsonArrayIncidencias.length(); i++) {
                    JSONObject tmpJsonObject = jsonArrayIncidencias.getJSONObject(i);
                    listIncidencia.add(tmpJsonObject.getString("descripcion"));
                }
            }
        } catch (InterruptedException e) {
            JsonUtils.makeMessage(this, e.getMessage());
        } catch (ExecutionException e) {
            JsonUtils.makeMessage(this, e.getMessage());
        } catch (JSONException e) {
            JsonUtils.makeMessage(this, e.getMessage());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, listIncidencia);
        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        this.spListIncidenciaView.setAdapter(dataAdapter);
        this.serviceClass = null;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        JSONObject jsonObject;
        try {
            switch (parent.getId()) {
                case R.id.spListEstudianteView:
                    jsonObject = JsonUtils.getJsonObjectFromJsonArray(this.jsonArrayEstudiantes,
                            "cedula",
                            parent.getItemAtPosition(position).toString());
                    this.idEstudiante = jsonObject.getInt("idEstudiante");
                    break;
                case R.id.spListIncidenciaView:
                    jsonObject = JsonUtils.getJsonObjectFromJsonArray(this.jsonArrayIncidencias,
                            "descripcion",
                            parent.getItemAtPosition(position).toString());
                    this.idIncidencia = jsonObject.getInt("idIncidencia");
                    break;
            }
        } catch (JSONException e) {
            JsonUtils.makeMessage(this, e.getMessage());
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}





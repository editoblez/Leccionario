package ecu.universidades.leccionario;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class IngresarTurnoActivity extends ActionBarActivity implements  AdapterView.OnItemSelectedListener {

    Spinner spTurnoListaMteriaView;
    Spinner spTurnoListaTemaView;
    Spinner spListaGrupoGrupo;
    Spinner spListaSemestre;

    int idSelectedMateria;
    int idSelectedTema;
    int idSelectedGrupo;
    int idSelectedSemestre;

    Button btnSaveTurnoOKView;

    JSONArray jsonArrayMateria;
    JSONArray jsonArrayTema;
    JSONArray jsonArrayGrupo;
    JSONArray jsonArraySemestre;

    List<String> listAllGrupo;
    List<String> listAllTema;
    List<String> listAllSemestre;

    int idLeccionario;
    int idProfesor;

    ServiceClass serviceClass = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingresar_turno);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        idProfesor = getIntent().getExtras().getInt("idUsuario");

        spTurnoListaMteriaView = (Spinner) findViewById(R.id.spTurnoListaMteriaView);
        spTurnoListaMteriaView .setOnItemSelectedListener(this);

        spTurnoListaTemaView = (Spinner) findViewById(R.id.spTurnoListaTemaView);
        spTurnoListaTemaView.setOnItemSelectedListener(this);

        spListaGrupoGrupo = (Spinner) findViewById(R.id.spListaGrupoGrupo);
        spListaGrupoGrupo.setOnItemSelectedListener(this);

        spListaSemestre=(Spinner)findViewById(R.id.spListaSemestre);
        spListaSemestre.setOnItemSelectedListener(this);

        fillMateria();
        fillSemestre();


        btnSaveTurnoOKView = (Button) findViewById(R.id.btnSaveTurnoOKView);
        btnSaveTurnoOKView.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                save();
            }
        });
    }

    private void save() {
        boolean cancel = false;
        if (this.serviceClass != null) return;
        String url= getString(R.string.base_url) + "leccionariohora/create";// esta logica es para ti mi amor
        //String url = getString(R.string.base_url) + "/" + this.idSelectedTema;(no se cual es la tabla q va a llenar)
        JSONObject params = new JSONObject();
        try {
            params.put("idTema", this.idSelectedTema);
            params.put("idProfesor", this.idProfesor);
            params.put("idLeccionario", this.idLeccionario);

        } catch (JSONException e) {
            e.printStackTrace();
        }serviceClass = new ServiceClass(this, url,
                HttpRequestType.POST, HttpResponseType.NONE, params);
        JSONObject response;
        try {
            response = serviceClass.execute().get();
            if (response.isNull("error")) {
               JsonUtils.makeMessage(this, getString(R.string.success_msg));
            }
            else
            {
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


    private void fillMateria() {
        String url = getString(R.string.base_url) + "materia/getAll";
        serviceClass = new ServiceClass(this, url,
                HttpRequestType.GET, HttpResponseType.JSONARRAYOBJECT);
        JSONObject response;
        listAllTema = null;
        try {
            response = serviceClass.execute().get();
            if (response.isNull("error")) {
                listAllTema = new ArrayList<String>();
                this.jsonArrayMateria = response.getJSONArray("result");
                for (int i = 0; i < this.jsonArrayMateria.length(); i++) {
                    JSONObject tmpJsonObject = jsonArrayMateria.getJSONObject(i);
                    listAllTema.add(tmpJsonObject.getString("nombreMateria"));
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
                (this, android.R.layout.simple_spinner_item, listAllTema);
        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        this.spTurnoListaMteriaView.setAdapter(dataAdapter);
        fillTema();
        this.serviceClass = null;
    }

    private void fillTema() {
        String url = getString(R.string.base_url) + "tema/getAllTemaByMateria/" + this.idSelectedMateria;
        serviceClass = new ServiceClass(this, url,
                HttpRequestType.GET, HttpResponseType.JSONARRAYOBJECT);
        JSONObject response;
        List<String> list = null;
        try {
            response = serviceClass.execute().get();
            if (response.isNull("error")) {
                list = new ArrayList<String>();
                this.jsonArrayTema = response.getJSONArray("result");
                for (int i = 0; i < this.jsonArrayTema.length(); i++) {
                    JSONObject tmpJsonObject = jsonArrayTema.getJSONObject(i);
                    list.add(tmpJsonObject.getString("nombreTema"));
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
                (this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        this.spTurnoListaTemaView.setAdapter(dataAdapter);
        this.serviceClass = null;
    }
    private void fillSemestre() {
        if (this.serviceClass != null) return;
        String url = getString(R.string.base_url) + "curso/getSemestre";
        serviceClass = new ServiceClass(this, url,
                HttpRequestType.GET, HttpResponseType.JSONARRAYOBJECT);
        JSONObject response;
        listAllSemestre = null;
        try {
            response = serviceClass.execute().get();
            if (response.isNull("error")) {
                listAllSemestre = new ArrayList<String>();
                this.jsonArraySemestre = response.getJSONArray("result");
                for (int i = 0; i < this.jsonArraySemestre.length(); i++)
                {
                    JSONObject tmpJsonObject = jsonArraySemestre.getJSONObject(i);
                    listAllSemestre.add(tmpJsonObject.getString("semestre"));
                }
            }
            else
            {
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
                (this, android.R.layout.simple_spinner_item,listAllSemestre);
        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        this.spListaSemestre.setAdapter(dataAdapter);
        this.serviceClass = null;
        //fillGrupo();
    }
    private void fillGrupo() {
        if (this.serviceClass != null) return;
        String url = getString(R.string.base_url) + "curso/getAllCursoByFreeHour/" + idSelectedSemestre;
        serviceClass = new ServiceClass(this, url,
                HttpRequestType.GET, HttpResponseType.JSONARRAYOBJECT);
        JSONObject response;
        listAllGrupo = null;
        try {
            response = serviceClass.execute().get();
            if (response.isNull("error")) {
                listAllGrupo = new ArrayList<String>();
                this.jsonArrayGrupo = response.getJSONArray("result");

                for (int i = 0; i < this.jsonArrayGrupo.length(); i++) {
                    JSONObject tmpJsonObject = jsonArrayGrupo.getJSONObject(i);
                    listAllGrupo.add(tmpJsonObject.getString("nombreCurso"));
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

        if (listAllGrupo != null) {
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                    (this, android.R.layout.simple_spinner_item, listAllGrupo);
            dataAdapter.setDropDownViewResource
                    (android.R.layout.simple_spinner_dropdown_item);
            this.spListaGrupoGrupo.setAdapter(dataAdapter);
        }
        this.serviceClass = null;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId())
        {
            case R.id.spTurnoListaMteriaView:
                this.onItemSelectedMateria(parent, view, position, id);
                break;
            case R.id.spTurnoListaTemaView:
                this.onItemSelectedTema(parent, view, position, id);
                break;
            case R.id.spListaGrupoGrupo:
                this.onItemSelectedCurso(parent, view, position, id);
                break;
            case R.id.spListaSemestre:
                this.onItemSelectedSemestre(parent, view, position, id);
                break;
        }
    }
    private void onItemSelectedSemestre(AdapterView<?> parent, View view, int position, long id) {
        JSONObject jsonObject = JsonUtils.getJsonObjectFromJsonArray(this.jsonArraySemestre,
                "semestre",
                Integer.parseInt(parent.getItemAtPosition(position).toString()));
        if (jsonObject != null)
        {
            try {
                this.idSelectedSemestre = jsonObject.getInt("semestre");
                fillGrupo();
            } catch (JSONException e) {
                Log.e("JSONException ", e.getMessage());
            }
        }

    }
    public void onItemSelectedCurso(AdapterView<?> parent, View view, int position, long id) {
        JSONObject jsonObject = JsonUtils.getJsonObjectFromJsonArray(this.jsonArrayGrupo,
                "nombreCurso",
                parent.getItemAtPosition(position).toString());
        if (jsonObject != null) {
            try {
                this.idSelectedGrupo = jsonObject.getInt("idCurso");
                this.idLeccionario = jsonObject.getInt("idLeccionario");

            } catch (JSONException e) {
                Log.e("JSONException ", e.getMessage());
            }
        }

    }
    private void onItemSelectedMateria(AdapterView<?> parent, View view, int position, long id) {
        JSONObject jsonObject = JsonUtils.getJsonObjectFromJsonArray(this.jsonArrayMateria,
                "nombreMateria",
                parent.getItemAtPosition(position).toString());
        if (jsonObject != null)
        {
            try {
                this.idSelectedMateria = jsonObject.getInt("idMateria");
                fillTema();
            } catch (JSONException e) {
                Log.e("JSONException ", e.getMessage());
            }
        }

    }

    private void onItemSelectedTema(AdapterView<?> parent, View view, int position, long id) {
        JSONObject jsonObject = JsonUtils.getJsonObjectFromJsonArray(this.jsonArrayTema,
                "nombreTema",
                parent.getItemAtPosition(position).toString());
        if (jsonObject != null)
        {
            try {
                this.idSelectedTema = jsonObject.getInt("idTema");
            } catch (JSONException e) {
                Log.e("JSONException ", e.getMessage());
            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

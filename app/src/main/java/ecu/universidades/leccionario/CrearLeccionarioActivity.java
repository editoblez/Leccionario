package ecu.universidades.leccionario;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CrearLeccionarioActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

    Button btnLeccionarioOKView;
    EditText txtAnolectivoInicioView;
    EditText txtAnoElectivoFinView;
    Spinner spListaGrupoView;
    Spinner spListaSemestreView;
    ServiceClass serviceClass = null;
    JSONArray jsonArrayGrupo;
    JSONArray jsonArraySemestre;
    int idSelectedGrupo;
    int idSelectedSemestre;

    String date="";

    List<String> listAllGrupo;
    List<String> listAllSemestre;

    int idInstructor;


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_leccionario);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        idInstructor = intent.getExtras().getInt("idUsuario");

        btnLeccionarioOKView = (Button) findViewById(R.id.btnSaveLeccionariooOKView);
        btnLeccionarioOKView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        txtAnolectivoInicioView = (EditText) findViewById(R.id.txtAnolectivoInicioView);

        txtAnolectivoInicioView.setError(null);

        txtAnoElectivoFinView = (EditText) findViewById(R.id.txtAnoElectivoFinView);
        txtAnoElectivoFinView.setError(null);

        spListaGrupoView = (Spinner) findViewById(R.id.spListaGrupoView);
        spListaGrupoView.setOnItemSelectedListener(this);

        spListaSemestreView = (Spinner) findViewById(R.id.spListaSemestreView);
        spListaSemestreView.setOnItemSelectedListener(this);

        fillSemestre();

        Calendar c = Calendar.getInstance();
        c.getTime();



        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void save() {
        if (serviceClass != null) {
            return;
        }
        txtAnolectivoInicioView.setError(null);
        String anoLectivoInicio = txtAnolectivoInicioView.getText().toString();

        txtAnoElectivoFinView.setError(null);
        String anoLectivoFin = txtAnoElectivoFinView.getText().toString();

        boolean cancel = false;
        View focusView = null;
        if (TextUtils.isEmpty(anoLectivoInicio)) {
            txtAnolectivoInicioView.setError(getString(R.string.error_field_required));
            focusView = txtAnolectivoInicioView;
            cancel = true;
        } else if (TextUtils.isEmpty(anoLectivoFin)) {
            txtAnoElectivoFinView.setError(getString(R.string.error_field_required));
            focusView = txtAnoElectivoFinView;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {

            String url = getString(R.string.base_url) + "leccionario/create";
            JSONObject params = new JSONObject();
            try {
                params.put("idLeccionario", Integer.MIN_VALUE);
                params.put("idCurso", this.idSelectedGrupo);
                params.put("inicioLectivo", Integer.parseInt(anoLectivoInicio));
                params.put("finLectivo", Integer.parseInt(anoLectivoFin));
                params.put("activo", true);
                params.put("idInstructor", idInstructor);
                serviceClass = new ServiceClass(this, url, HttpRequestType.POST,
                        HttpResponseType.NONE, params);
                JSONObject response =  serviceClass.execute().get();
                if (response.isNull("error")) {
                    JsonUtils.makeMessage(this, getString(R.string.success_msg));
                }
                else
                {
                    JsonUtils.makeMessage(this, response.getString("error"));
                }
            } catch (JSONException e) {
                JsonUtils.makeMessage(this, e.getMessage());
            } catch (InterruptedException e) {
                JsonUtils.makeMessage(this, e.getMessage());
            } catch (ExecutionException e) {
                JsonUtils.makeMessage(this, e.getMessage());
            }
        }
        serviceClass = null;
        fillGrupo();
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
        this.spListaSemestreView.setAdapter(dataAdapter);
        this.serviceClass = null;
        //fillGrupo();
    }
    private void fillGrupo() {
        if (this.serviceClass != null) return;
        String url = getString(R.string.base_url) + "curso/getAllCursoBSemestreActive/" + idSelectedSemestre;
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
            this.spListaGrupoView.setAdapter(dataAdapter);
        }
        this.serviceClass = null;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId())
        {
            case R.id.spListaSemestreView:
                this.onItemSelectedSemestre(parent, view, position, id);
                break;
            case R.id.spListaGrupoView:
                this.onItemSelectedGrupo(parent, view, position, id);
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

    private void onItemSelectedGrupo(AdapterView<?> parent, View view, int position, long id) {
        JSONObject jsonObject = JsonUtils.getJsonObjectFromJsonArray(this.jsonArrayGrupo,
                "nombreCurso",
                parent.getItemAtPosition(position).toString());
        if (jsonObject != null)
        {
            try {
                this.idSelectedGrupo = jsonObject.getInt("idCurso");
            } catch (JSONException e) {
                Log.e("JSONException ", e.getMessage());
            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }



}

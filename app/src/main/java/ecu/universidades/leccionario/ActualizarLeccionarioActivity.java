package ecu.universidades.leccionario;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ActualizarLeccionarioActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

    LinearLayout lyLeccionarioFind;
    DatePicker datePicker;
    Spinner spListaSemestreView;
    Spinner spListaGrupoView;
    Button btnLeccionarioFindView;

    LinearLayout lyLeccionarioResult;
    EditText txtAnoLectivoInicioView;
    EditText txtAnoLectivoFinView;
    CheckBox chLeccionarioView;

    LinearLayout lyLeccionarioButtonAction;
    Button btnLeccionariooOKView;
    Button btnLeccionarioVolver;

    int idInstructor;

    List<String> listAllGrupo;
    List<String> listAllSemestre;

    JSONArray jsonArraySemestre;
    JSONArray jsonArrayGrupo;

    int idSelectedSemestre = -1;
    int idSelectedGrupo = -1;

    int idLeccionario;

    Long date;


    ServiceClass serviceClass = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_leccionario);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        idInstructor = intent.getExtras().getInt("idUsuario");

        btnLeccionarioFindView = (Button) findViewById(R.id.btnLeccionarioFindView);
        btnLeccionarioFindView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                find();
            }
        });
        btnLeccionariooOKView = (Button) findViewById(R.id.btnLeccionariooOKView);
        btnLeccionariooOKView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
        btnLeccionarioVolver = (Button) findViewById(R.id.btnLeccionarioVolver);
        btnLeccionarioVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
        lyLeccionarioFind = (LinearLayout) findViewById(R.id.lyLeccionarioFind);
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date = datePicker.getCalendarView().getDate();
            }
        });
        spListaSemestreView = (Spinner) findViewById(R.id.spListaSemestreView);
        spListaSemestreView.setOnItemSelectedListener(this);
        spListaGrupoView = (Spinner) findViewById(R.id.spListaGrupoView);
        spListaGrupoView.setOnItemSelectedListener(this);
        lyLeccionarioResult = (LinearLayout) findViewById(R.id.lyLeccionarioResult);
        txtAnoLectivoInicioView = (EditText) findViewById(R.id.txtAnoLectivoInicioView);
        txtAnoLectivoInicioView.setError(null);
        txtAnoLectivoFinView = (EditText) findViewById(R.id.txtAnoLectivoFinView);
        txtAnoLectivoFinView.setError(null);
        chLeccionarioView = (CheckBox) findViewById(R.id.chLeccionarioView);

        lyLeccionarioButtonAction = (LinearLayout) findViewById(R.id.lyLeccionarioButtonAction);

        lyLeccionarioFind.setVisibility(View.VISIBLE);
        lyLeccionarioResult.setVisibility(View.GONE);
        lyLeccionarioButtonAction.setVisibility(View.GONE);

        fillSemestre();
    }

    private void find() {
        if (serviceClass != null) return;
        if (idSelectedGrupo == -1 || this.spListaGrupoView.getSelectedItem().equals(null)) {
            JsonUtils.makeMessage(this, getString(R.string.error_field_required));
            return;
        }
        lyLeccionarioFind.setVisibility(View.GONE);
        lyLeccionarioResult.setVisibility(View.VISIBLE);
        lyLeccionarioButtonAction.setVisibility(View.VISIBLE);
        String url = getString(R.string.base_url) + "leccionario/getAllLeccionarioByCursoAndDate/"
                + this.idInstructor + "/" + this.date + "/" + idSelectedGrupo;
        serviceClass = new ServiceClass(this, url,
                HttpRequestType.GET, HttpResponseType.JSONOBJECT);
        JSONObject response;
        listAllSemestre = null;
        try {
            response = serviceClass.execute().get();
            if (response.isNull("error")) {
                response = response.getJSONObject("result");
                this.txtAnoLectivoInicioView.setText(String.valueOf(response.getInt("inicioLectivo")));
                this.txtAnoLectivoFinView.setText(String.valueOf(response.getInt("finLectivo")));
                this.chLeccionarioView.setChecked(response.getBoolean("activo"));
                this.idLeccionario = response.getInt("idLeccionario");
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

        serviceClass = null;
    }

    private void back() {
        lyLeccionarioFind.setVisibility(View.VISIBLE);
        lyLeccionarioResult.setVisibility(View.GONE);
        lyLeccionarioButtonAction.setVisibility(View.GONE);
    }

    private void save() {
        if (serviceClass != null) {
            return;
        }
        txtAnoLectivoInicioView.setError(null);
        String anoLectivoInicio = txtAnoLectivoInicioView.getText().toString();

        txtAnoLectivoFinView.setError(null);
        String anoLectivoFin = txtAnoLectivoFinView.getText().toString();

        boolean cancel = false;
        View focusView = null;
        if (TextUtils.isEmpty(anoLectivoInicio)) {
            txtAnoLectivoInicioView.setError(getString(R.string.error_field_required));
            focusView = txtAnoLectivoInicioView;
            cancel = true;
        } else if (TextUtils.isEmpty(anoLectivoFin)) {
            txtAnoLectivoFinView.setError(getString(R.string.error_field_required));
            focusView = txtAnoLectivoFinView;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {

            String url = getString(R.string.base_url) + "leccionario/update";
            JSONObject params = new JSONObject();
            try {
                params.put("idLeccionario", this.idLeccionario);
                params.put("inicioLectivo", Integer.parseInt(anoLectivoInicio));
                params.put("finLectivo", Integer.parseInt(anoLectivoFin));
                params.put("activo", this.chLeccionarioView.isChecked());
                params.put("idInstructor", idInstructor);
                params.put("fecha", String.valueOf(date));
                params.put("idCurso", this.idSelectedGrupo);

                serviceClass = new ServiceClass(this, url, HttpRequestType.POST,
                        HttpResponseType.NONE, params);
                JSONObject response = serviceClass.execute().get();
                if (response.isNull("error")) {
                    JsonUtils.makeMessage(this, getString(R.string.success_msg));
                } else {
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
                for (int i = 0; i < this.jsonArraySemestre.length(); i++) {
                    JSONObject tmpJsonObject = jsonArraySemestre.getJSONObject(i);
                    listAllSemestre.add(tmpJsonObject.getString("semestre"));
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
                (this, android.R.layout.simple_spinner_item, listAllSemestre);
        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        this.spListaSemestreView.setAdapter(dataAdapter);
        this.serviceClass = null;
    }

    private void fillGrupo() {
        if (this.serviceClass != null) return;
        date = datePicker.getCalendarView().getDate();
        String url = getString(R.string.base_url) + "curso/getAllCursoByInstructor/"
                + idSelectedSemestre + "/" + date;
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
    private void onItemSelectedSemestre(AdapterView<?> parent, View view, int position, long id) {
        JSONObject jsonObject = JsonUtils.getJsonObjectFromJsonArray(this.jsonArraySemestre,
                "semestre",
                Integer.parseInt(parent.getItemAtPosition(position).toString()));
        if (jsonObject != null) {
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
        if (jsonObject != null) {
            try {
                this.idSelectedGrupo = jsonObject.getInt("idCurso");
            } catch (JSONException e) {
                Log.e("JSONException ", e.getMessage());
            }
        }

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spListaSemestreView:
                this.onItemSelectedSemestre(parent, view, position, id);
                break;
            case R.id.spListaGrupoView:
                this.onItemSelectedGrupo(parent, view, position, id);
                break;
            default:
                Log.e("MSG", "RARO");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}

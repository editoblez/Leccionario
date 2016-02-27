package ecu.universidades.leccionario;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ActualizarGrupoActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener{
    EditText txtCursoNombreView;
    EditText txtCursoSemestreView;

    Spinner spCursoNombreView;
    Spinner spCursoSemestreView;

    CheckBox chBoxActivoView;
    Button btnCursoOKView;

    List<String> listAllGrupo;
    List<String> listAllSemestre;

    JSONArray jsonArraySemestre;
    JSONArray jsonArrayGrupo;
    int idSelectedSemestre;
    int idSelectedGrupo;

    ServiceClass serviceClass = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_grupo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Context context = getApplicationContext();

        txtCursoSemestreView = (EditText) findViewById(R.id.txtCursoSemestreView);
        spCursoSemestreView = (Spinner) findViewById(R.id.spCursoSemestreView);
        spCursoSemestreView.setOnItemSelectedListener(this);

        txtCursoNombreView = (EditText) findViewById(R.id.txtCursoNombreView);
        spCursoNombreView = (Spinner) findViewById(R.id.spCursoNombreView);
        spCursoNombreView.setOnItemSelectedListener(this);

        chBoxActivoView = (CheckBox) findViewById(R.id.chCursoActivoView);
        btnCursoOKView = (Button) findViewById(R.id.btnCursoOKView);


        btnCursoOKView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
        fillSemestre();


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
                txtCursoNombreView.setError(response.getString("error"));
                txtCursoNombreView.requestFocus();
            }
        } catch (InterruptedException e) {
            txtCursoSemestreView.setError(e.getMessage());
        } catch (ExecutionException e) {
            txtCursoSemestreView.setError(e.getMessage());
        } catch (JSONException e) {
            txtCursoSemestreView.setError(e.getMessage());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,listAllSemestre);
        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        this.spCursoSemestreView.setAdapter(dataAdapter);
        this.serviceClass = null;
        //fillGrupo();
    }

    private void fillGrupo() {
        if (this.serviceClass != null) return;
        String url = getString(R.string.base_url) + "curso/getAllCursoBSemestre/" + idSelectedSemestre;
        serviceClass = new ServiceClass(this, url,
                HttpRequestType.GET, HttpResponseType.JSONARRAYOBJECT);
        JSONObject response;
        listAllGrupo = null;
        try {
            response = serviceClass.execute().get();
            if (response.isNull("error")) {
                listAllGrupo = new ArrayList<String>();
                this.jsonArrayGrupo = response.getJSONArray("result");
                for (int i = 0; i < this.jsonArrayGrupo.length(); i++)
                {
                    JSONObject tmpJsonObject = jsonArrayGrupo.getJSONObject(i);
                    listAllGrupo.add(tmpJsonObject.getString("nombreCurso"));
                }
            }
            else
            {
                /*txtCursoNombreView.setError(response.getString("error"));
                txtCursoNombreView.requestFocus();*/
            }
        } catch (InterruptedException e) {
            txtCursoNombreView.setError(e.getMessage());
        } catch (ExecutionException e) {
            txtCursoNombreView.setError(e.getMessage());
        } catch (JSONException e) {
            txtCursoNombreView.setError(e.getMessage());
        }

        if (listAllGrupo != null) {
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                    (this, android.R.layout.simple_spinner_item, listAllGrupo);
            dataAdapter.setDropDownViewResource
                    (android.R.layout.simple_spinner_dropdown_item);
            this.spCursoNombreView.setAdapter(dataAdapter);
        }
        this.serviceClass = null;
    }
    private void save() {
        boolean cancel = false;
        if (this.serviceClass != null) return;
        View focus = null;
        txtCursoNombreView.setError(null);
        String newGrpoNew = txtCursoNombreView.getText().toString();

        txtCursoSemestreView.setError(null);
        String newSemestreNew = txtCursoSemestreView.getText().toString();

        if (TextUtils.isEmpty(newGrpoNew) ) {
            txtCursoNombreView.setError(getString(R.string.error_field_required));
            focus = txtCursoNombreView;
            focus.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(newSemestreNew)  ) {

            txtCursoSemestreView.setError(getString(R.string.error_field_required));
            focus = txtCursoSemestreView;
            focus.requestFocus();
            return;
        }
        Boolean activo = (chBoxActivoView.isChecked());
        String url = getString(R.string.base_url) + "curso/" + this.idSelectedGrupo;
        JSONObject params = new JSONObject();

        try {
            params.put("semestre", newSemestreNew);
            params.put("nombreCurso", newGrpoNew);
            params.put("activo", activo);
            params.put("idCurso", this.idSelectedGrupo);

        } catch (JSONException e) {
            e.printStackTrace();
        }  serviceClass = new ServiceClass(this, url,
                HttpRequestType.PUT, HttpResponseType.NONE, params);
        JSONObject response;
        try {
            response = serviceClass.execute().get();
            if (response.isNull("error")) {
                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.success_msg), Toast.LENGTH_SHORT);
                toast.show();
            }
            else
            {
                txtCursoNombreView.setError(response.getString("error"));
                txtCursoNombreView.requestFocus();
            }
        } catch (InterruptedException e) {
            txtCursoNombreView.setError(e.getMessage());
            txtCursoSemestreView.setError(e.getMessage());
        } catch (ExecutionException e) {
            txtCursoNombreView.setError(e.getMessage());
            txtCursoSemestreView.setError(e.getMessage());
        } catch (JSONException e) {
            txtCursoNombreView.setError(e.getMessage());
            txtCursoSemestreView.setError(e.getMessage());
        }
        this.serviceClass = null;
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId())
        {
            case R.id.spCursoSemestreView:
                this.onItemSelectedSemestre(parent, view, position, id);
                break;
            case R.id.spCursoNombreView:
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
                this.txtCursoNombreView.setText(jsonObject.getString("nombreCurso"));
                this.txtCursoSemestreView.setText(jsonObject.getString("semestre"));
                this.chBoxActivoView.setChecked(jsonObject.getBoolean("activo"));
                this.idSelectedGrupo = jsonObject.getInt("idCurso");
            } catch (JSONException e) {
                Log.e("JSONException ", e.getMessage());
            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


}

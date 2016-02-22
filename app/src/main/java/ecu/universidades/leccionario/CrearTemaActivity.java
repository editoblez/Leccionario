package ecu.universidades.leccionario;

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

public class CrearTemaActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

    Button btnSaveView;
    Spinner spListMateriaView;
    // private createIncidenciaTask cIncidenciasTask = null;
    EditText temaView;
    int idSelectedMateria;
    ServiceClass serviceClass = null;
    JSONArray jsonArrayMateria;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_tema);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        btnSaveView = (Button) findViewById(R.id.btnSaveTemaView);
        btnSaveView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        temaView = (EditText) findViewById(R.id.idTemaView);
        temaView.setError(null);

        spListMateriaView = (Spinner) findViewById(R.id.spListMateriaView);
        spListMateriaView.setOnItemSelectedListener(this);

        fillMateria();

    }

    private void save() {
        if (serviceClass != null) {
            return;
        }
        temaView.setError(null);
        String descripcionTema = temaView.getText().toString();

        boolean cancel = false;

        View focusView = null;
        if (TextUtils.isEmpty(descripcionTema)) {
            temaView.setError(getString(R.string.error_field_required));
            focusView = temaView;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //showProgress(true);
            String url = getString(R.string.base_url) + "tema";
            JSONObject params = new JSONObject();
            try {
                JSONObject materia = new JSONObject();
                materia.put("idMateria", this.idSelectedMateria);
                params.put("idTema", 0);
                params.put("idMateria", materia);
                params.put("nombreTema", descripcionTema);
                params.put("activo", true);
            } catch (JSONException e) {
                Log.e("JSONException ", e.getMessage());
            }
            serviceClass = new ServiceClass(this, url,
                    HttpRequestType.POST, HttpResponseType.NONE, params);
            JSONObject result = null;
            try {
                result = serviceClass.execute().get();
                if (result.isNull("error")) {
                    Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.success_msg), Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    temaView.setError(result.getString("error"));
                    temaView.requestFocus();
                }
            } catch (InterruptedException e) {
                Log.e("InterruptedException", e.getMessage());
            } catch (ExecutionException e) {
                Log.e("ExecutionException", e.getMessage());
            } catch (JSONException e) {
                Log.e("JSONException", e.getMessage());
            }

        }

        this.serviceClass = null;
    }

    private void fillMateria() {
        if  (serviceClass != null) return;
        String url = getString(R.string.base_url) + "materia/getAll";
        serviceClass = new ServiceClass(this, url,
                HttpRequestType.GET, HttpResponseType.JSONARRAYOBJECT);
        JSONObject response;
        List<String> list = null;
        try {
            response = serviceClass.execute().get();
            if (response.isNull("error")) {
                list = new ArrayList<String>();
                this.jsonArrayMateria = response.getJSONArray("result");
                for (int i = 0; i < this.jsonArrayMateria.length(); i++) {
                    JSONObject tmpJsonObject = jsonArrayMateria.getJSONObject(i);
                    list.add(tmpJsonObject.getString("nombreMateria"));
                }
            }
        } catch (InterruptedException e) {
            this.temaView.setError(e.getMessage());
        } catch (ExecutionException e) {
            this.temaView.setError(e.getMessage());
        } catch (JSONException e) {
            this.temaView.setError(e.getMessage());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        this.spListMateriaView.setAdapter(dataAdapter);
        this.serviceClass = null;

    }


        @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            JSONObject jsonObject = JsonUtils.getJsonObjectFromJsonArray(this.jsonArrayMateria,
                    "nombreMateria",
                    parent.getItemAtPosition(position).toString());
            if (jsonObject != null)
            {
                try {
                    this.idSelectedMateria = jsonObject.getInt("idMateria");
                } catch (JSONException e) {
                    Log.e("JSONException ", e.getMessage());
                }
            }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
package ecu.universidades.leccionario;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ActualizarGrupoActivity extends ActionBarActivity {
    EditText txtGrupoNameView;
    EditText txtSemestreNameView;
    Spinner spListGrupoView;
    Spinner spListSemestreView;
    CheckBox chBoxActivoView;
    Button btnOKView;
    List<String> listAllGrupo;
    List<String> listAllSemestre;
    ServiceClass serviceClass = null;
    JSONArray jsonArray;
    int idSelectedSemestre;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_grupo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Context context = getApplicationContext();


        txtGrupoNameView = (EditText) findViewById(R.id.tNombregrupo);
        spListGrupoView = (Spinner) findViewById(R.id.spListGrupoView);
        txtGrupoNameView.setOnItemSelectedListener(this);

        txtSemestreNameView = (EditText) findViewById(R.id.tSemestre);
        spListSemestreView = (Spinner) findViewById(R.id.spListSemestreView);
        txtSemestreNameView.setOnItemSelectedListener(this);

        chBoxActivoView = (CheckBox) findViewById(R.id.chBoxActivoView);
        btnOKView = (Button) findViewById(R.id.btnOKGrupo);


        btnOKView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
        listAllGrupo = null;
        fillSpinnerGrupo();
        fillSpinnerSemestre();
    }
    private void fillSpinnerGrupo() {
        String url = getString(R.string.base_url) + "curso/getAll";
        serviceClass = new ServiceClass(this, url,
                HttpRequestType.GET, HttpResponseType.JSONARRAYOBJECT);
        JSONObject response;
        List<String> list = null;
        try {
            response = serviceClass.execute().get();
            if (response.isNull("error")) {
                list = new ArrayList<String>();
                this.jsonArray = response.getJSONArray("result");
                for (int i = 0; i < this.jsonArray.length(); i++)
                {
                    JSONObject tmpJsonObject = jsonArray.getJSONObject(i);
                    list.add(tmpJsonObject.getString("nombreGrupo"));
                }
            }
        } catch (InterruptedException e) {
            txtGrupoNameView.setError(e.getMessage());
        } catch (ExecutionException e) {
            txtGrupoNameView.setError(e.getMessage());
        } catch (JSONException e) {
            txtGrupoNameView.setError(e.getMessage());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,list);
        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        this.spListGrupoView.setAdapter(dataAdapter);
        this.serviceClass = null;
    }

    private void  fillSpinnerSemestre() {
        String url = getString(R.string.base_url) + "curso/getAll";
        serviceClass = new ServiceClass(this, url,
                HttpRequestType.GET, HttpResponseType.JSONARRAYOBJECT);
        JSONObject response;
        List<String> list = null;
        try {
            response = serviceClass.execute().get();
            if (response.isNull("error")) {
                list = new ArrayList<String>();
                this.jsonArray = response.getJSONArray("result");
                for (int i = 0; i < this.jsonArray.length(); i++)
                {
                    JSONObject tmpJsonObject = jsonArray.getJSONObject(i);
                    list.add(tmpJsonObject.getString("semestre"));
                }
            }
        } catch (InterruptedException e) {
            txtSemestreNameView.setError(e.getMessage());
        } catch (ExecutionException e) {
            txtSemestreNameView.setError(e.getMessage());
        } catch (JSONException e) {
            txtSemestreNameView.setError(e.getMessage());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,list);
        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        this.spListSemestreView.setAdapter(dataAdapter);
        this.serviceClass = null;
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        JSONObject jsonObject = JsonUtils.getJsonObjectFromJsonArray(this.jsonArray,
                "nombreMateria",
                parent.getItemAtPosition(position).toString());
        if (jsonObject != null)
        {
            try {
                this.txtGrupoNameView.setText(jsonObject.getString("nombreGrupo"));
                this.txtGrupoNameView.setText(jsonObject.getString("semestre"));
                this.chBoxActivoView.setChecked(jsonObject.getBoolean("activo"));
                this.txtGrupoNameView = jsonObject.getInt("idGrupo");
            } catch (JSONException e) {
                Log.e("JSONException ", e.getMessage());
            }
        }
    }

}

package ecu.universidades.leccionario;

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

public class ActualizarTemaActivity extends ActionBarActivity implements  AdapterView.OnItemSelectedListener {

    Spinner spMateriaView;
    Spinner spTemaView;
    EditText txtTemaView;
    CheckBox chTemaView;
    Button btnOKView;

    JSONArray jsonArrayMateria;
    JSONArray jsonArrayTema;
    int idSelectedMateria;
    int idSelectedTema;



    ServiceClass serviceClass = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_tema);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spMateriaView = (Spinner) findViewById(R.id.spMateriaView);
        spMateriaView .setOnItemSelectedListener(this);
        spTemaView = (Spinner) findViewById(R.id.spTemaView);
        spTemaView.setOnItemSelectedListener(this);

        txtTemaView = (EditText) findViewById(R.id.txtTemaView);
        chTemaView = (CheckBox) findViewById(R.id.chTemaView);
        btnOKView = (Button) findViewById(R.id.btnOKView);
        btnOKView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
        spMateriaView = (Spinner) findViewById(R.id.spMateriaView);
        fillMateria();
    }

    private void save() {
        boolean cancel = false;
        if (this.serviceClass != null) return;
        View focus = txtTemaView;
        txtTemaView.setError(null);
        String newTema = txtTemaView.getText().toString();
        if (TextUtils.isEmpty(newTema))
        {
            txtTemaView.setError(getString(R.string.error_field_required));
            focus.requestFocus();
            return;
        }
        Boolean activo = (chTemaView.isChecked());
        String url = getString(R.string.base_url) + "tema/" + this.idSelectedTema;
        JSONObject params = new JSONObject();
        try {
            JSONObject materia = new JSONObject();
            materia.put("idMateria", this.idSelectedMateria);
            params.put("idTema", this.idSelectedTema);
            params.put("nombreTema", newTema);
            params.put("activo", activo);
            params.put("idMateria", materia);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        serviceClass = new ServiceClass(this, url,
                HttpRequestType.PUT, HttpResponseType.NONE, params);
        JSONObject response;
        try {
            response = serviceClass.execute().get();
            if (response.isNull("error")) {
                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.success_msg), Toast.LENGTH_SHORT);
                //toast.setGravity(Gravity.TOP | Gravity.RIGHT, 0, 0);
                toast.show();
            }
        } catch (InterruptedException e) {
            txtTemaView.setError(e.getMessage());
        } catch (ExecutionException e) {
            txtTemaView.setError(e.getMessage());
        }
        this.serviceClass = null;
    }

    private void fillMateria() {
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
            txtTemaView.setError(e.getMessage());
        } catch (ExecutionException e) {
            txtTemaView.setError(e.getMessage());
        } catch (JSONException e) {
            txtTemaView.setError(e.getMessage());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        this.spMateriaView.setAdapter(dataAdapter);
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
            txtTemaView.setError(e.getMessage());
        } catch (ExecutionException e) {
            txtTemaView.setError(e.getMessage());
        } catch (JSONException e) {
            txtTemaView.setError(e.getMessage());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        this.spTemaView.setAdapter(dataAdapter);
        this.serviceClass = null;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId())
        {
            case R.id.spMateriaView:
                this.onItemSelectedMateria(parent, view, position, id);
                break;
            case R.id.spTemaView:
                this.onItemSelectedTema(parent, view, position, id);
                break;
        }
    }

    private void onItemSelectedMateria (AdapterView<?> parent, View view, int position, long id)
    {
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
        this.serviceClass = null;
    }

    private void onItemSelectedTema (AdapterView<?> parent, View view, int position, long id)
    {
        JSONObject jsonObject = JsonUtils.getJsonObjectFromJsonArray(this.jsonArrayTema,
                "nombreTema",
                parent.getItemAtPosition(position).toString());
        if (jsonObject != null)
        {
            try {
                this.txtTemaView.setText(jsonObject.getString("nombreTema"));
                this.chTemaView.setChecked(jsonObject.getBoolean("activo"));
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

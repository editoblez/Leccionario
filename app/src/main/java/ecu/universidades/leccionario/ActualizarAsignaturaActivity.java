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

public class ActualizarAsignaturaActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

    EditText txtMateriaNameView;
    Spinner spListMateriaView;
    CheckBox chBoxActivoView;
    Button btnOKView;
    List<String> listAllMateria;
    ServiceClass serviceClass = null;
    JSONArray jsonArray;
    int idSelectedMateria;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_asignatura);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Context context = getApplicationContext();
        //CharSequence text = getString(R.string.success_msg);
        //int duration = Toast.LENGTH_SHORT;

        txtMateriaNameView = (EditText) findViewById(R.id.crearMateria);
        spListMateriaView = (Spinner) findViewById(R.id.spMateriaView);
        spListMateriaView.setOnItemSelectedListener(this);
        chBoxActivoView = (CheckBox) findViewById(R.id.chBoxActivoView);
        btnOKView = (Button) findViewById(R.id.btnSaveTurnoOKView);
        btnOKView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
        listAllMateria = null;
        fillSpinner();
    }

    private void fillSpinner() {
        String url = getString(R.string.base_url) + "materia/getAll";
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
                    list.add(tmpJsonObject.getString("nombreMateria"));
                }
            }
        } catch (InterruptedException e) {
            txtMateriaNameView.setError(e.getMessage());
        } catch (ExecutionException e) {
            txtMateriaNameView.setError(e.getMessage());
        } catch (JSONException e) {
            txtMateriaNameView.setError(e.getMessage());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,list);
        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        this.spListMateriaView.setAdapter(dataAdapter);
        this.serviceClass = null;
    }

    private void save() {
        boolean cancel = false;
        if (this.serviceClass != null) return;
        View focus = txtMateriaNameView;
        txtMateriaNameView.setError(null);
        String newMateriaNew = txtMateriaNameView.getText().toString();
        if (TextUtils.isEmpty(newMateriaNew))
        {
            txtMateriaNameView.setError(getString(R.string.error_field_required));
            focus.requestFocus();
            return;
        }
        Boolean activo = (chBoxActivoView.isChecked());
        String url = getString(R.string.base_url) + "materia/" + this.idSelectedMateria;
        JSONObject params = new JSONObject();
        try {
            params.put("idMateria", this.idSelectedMateria);
            params.put("nombreMateria", newMateriaNew);
            params.put("activo", activo);
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
            txtMateriaNameView.setError(e.getMessage());
        } catch (ExecutionException e) {
            txtMateriaNameView.setError(e.getMessage());
        }
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
                this.txtMateriaNameView.setText(jsonObject.getString("nombreMateria"));
                this.chBoxActivoView.setChecked(jsonObject.getBoolean("activo"));
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

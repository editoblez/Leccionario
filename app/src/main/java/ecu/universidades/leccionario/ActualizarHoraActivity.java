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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.SimpleFormatter;

public class ActualizarHoraActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {


    Spinner idHoraView;
    EditText horaInicioView;
    EditText horaFinView;
    CheckBox chHoraView;
    Button btnHoraOKView;

    ServiceClass serviceClass = null;
    JSONArray jsonArray;
    int idSelectedHora;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_hora);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.idHoraView = (Spinner) findViewById(R.id.idHoraView);
        this.idHoraView.setOnItemSelectedListener(this);
        this.horaInicioView = (EditText) findViewById(R.id.horaInicioView);
        this.horaFinView = (EditText) findViewById(R.id.horaFinView);
        this.chHoraView = (CheckBox) findViewById(R.id.chHoraView);
        this.btnHoraOKView = (Button) findViewById(R.id.btnHoraOKView);
        this.btnHoraOKView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        fillSpinner();
    }

    private void fillSpinner() {
        if (serviceClass != null) return;
        String url = getString(R.string.base_url) + "hora/getAll";
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
                    list.add(tmpJsonObject.getString("idHora"));
                }
            }
            else
            {
                this.horaFinView.setError(response.getString("error"));
                this.horaFinView.requestFocus();

            }
        } catch (InterruptedException e) {
            horaFinView.setError(e.getMessage());
        } catch (ExecutionException e) {
            horaFinView.setError(e.getMessage());
        } catch (JSONException e) {
            horaFinView.setError(e.getMessage());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,list);
        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        this.idHoraView.setAdapter(dataAdapter);
        this.serviceClass = null;
    }

    private void save() {
        boolean cancel = false;
        if (this.serviceClass != null) return;
        View focus = horaFinView;
        horaFinView.setError(null);
        String horaInicio = horaInicioView.getText().toString();
        if (TextUtils.isEmpty(horaInicio))
        {
            horaInicioView.setError(getString(R.string.error_field_required));
            focus.requestFocus();
            return;
        }
        String horaFin = horaFinView.getText().toString();
        if (TextUtils.isEmpty(horaInicio))
        {
            horaFinView.setError(getString(R.string.error_field_required));
            focus.requestFocus();
            return;
        }
        Boolean activo = (chHoraView.isChecked());
        String url = getString(R.string.base_url) + "hora/update/" + this.idSelectedHora;
        JSONObject params = new JSONObject();
        try {
            params.put("idHora", this.idSelectedHora);
            params.put("horaInicio", horaInicio);
            params.put("horaFin", horaFin);
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
                toast.show();
            }
            else
            {
                horaFinView.setError(response.getString("error"));
            }
        } catch (InterruptedException e) {
            horaFinView.setError(e.getMessage());
            horaFinView.requestFocus();
        } catch (ExecutionException e) {
            horaFinView.setError(e.getMessage());
            horaFinView.requestFocus();
        } catch (JSONException e) {
            horaFinView.setError(e.getMessage());
            horaFinView.requestFocus();
        }
        this.serviceClass = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        JSONObject jsonObject = JsonUtils.getJsonObjectFromJsonArray(this.jsonArray,
                "idHora",
                Integer.parseInt(parent.getItemAtPosition(position).toString()));
        if (jsonObject != null)
        {
            try {
                this.horaInicioView.setText(jsonObject.getString("horaInicio"));
                this.horaFinView.setText(jsonObject.getString("horaFin"));
                this.chHoraView.setChecked(jsonObject.getBoolean("activo"));
                this.idSelectedHora = jsonObject.getInt("idHora");
            } catch (JSONException e) {
                Log.e("JSONException ", e.getMessage());
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

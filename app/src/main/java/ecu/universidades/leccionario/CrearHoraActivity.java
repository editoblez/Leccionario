package ecu.universidades.leccionario;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class CrearHoraActivity extends ActionBarActivity {
    Button btnSaveView;
    EditText idHoraView;
    EditText horaInicioView;
    EditText horaFinView;

    ServiceClass serviceClass = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_hora);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnSaveView = (Button) findViewById(R.id.btnSaveHoraView);
        btnSaveView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        idHoraView = (EditText) findViewById(R.id.idHoraView);
        idHoraView.setError(null);

        horaInicioView = (EditText) findViewById(R.id.idHoraInicioView);
        horaInicioView.setError(null);

        horaFinView = (EditText) findViewById(R.id.idHoraFinView);
        horaFinView.setError(null);
    }

    private void save() {
        if (serviceClass != null) {
            return;
        }
        idHoraView.setError(null);
        String idHora = idHoraView.getText().toString();

        horaInicioView.setError(null);
        String horaInicio = horaInicioView.getText().toString();

        horaFinView.setError(null);
        String horaFin = horaFinView.getText().toString();

        boolean cancel = false;

        View focusView = null;
        if (TextUtils.isEmpty(idHora))
        {
            idHoraView.setError(getString(R.string.error_field_required));
            focusView = idHoraView;
            cancel = true;
        }
        if (TextUtils.isEmpty(horaInicio))
        {
            horaInicioView.setError(getString(R.string.error_field_required));
            focusView = horaInicioView;
            cancel = true;
        }
        if (TextUtils.isEmpty(horaFin))
        {
            horaFinView.setError(getString(R.string.error_field_required));
            focusView = horaFinView;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //showProgress(true);
            String url = getString(R.string.base_url) + "hora/create";
            JSONObject params = new JSONObject();
            try {
                params.put("idHora", Integer.parseInt(idHora));
                params.put("horaInicio", horaInicio);
                params.put("horaFin", horaFin);
                params.put("activo", true);
            } catch (JSONException e) {
                Log.e("JSONException ", e.getMessage());
            }
            serviceClass = new ServiceClass(this, url,
                    HttpRequestType.POST, HttpResponseType.NONE, params);
            JSONObject result = null;
            try {
                 result = serviceClass.execute().get();
                if (result.isNull("error")){
                    Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.success_msg), Toast.LENGTH_SHORT);
                    toast.show();
                }
                else{
                    idHoraView.setError(result.getString("error"));
                    idHoraView.requestFocus();
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


}


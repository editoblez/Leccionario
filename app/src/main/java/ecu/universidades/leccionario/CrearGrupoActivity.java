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

import com.google.android.gms.common.api.GoogleApiClient;

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

public class CrearGrupoActivity extends ActionBarActivity {


    Button btnCursoOKView;
    ServiceClass  serviceClass = null;
    private EditText txtCursoNombreView;
    private EditText txtCursoSemestreView;


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        btnCursoOKView = (Button) findViewById(R.id.btnCursoOKView);
        btnCursoOKView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        txtCursoNombreView = (EditText) findViewById(R.id.txtCursoNombreView);

        txtCursoNombreView.setError(null);

        txtCursoSemestreView = (EditText) findViewById(R.id.txtCursoSemestreView);

        txtCursoSemestreView.setError(null);

    }

    private void save() {
        if (serviceClass != null) {
            return;
        }
        txtCursoNombreView.setError(null);
        String nombreGrupo = txtCursoNombreView.getText().toString();

        txtCursoSemestreView.setError(null);
        String nombreSemestre = txtCursoSemestreView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(nombreGrupo)) {
            txtCursoNombreView.setError(getString(R.string.error_field_required));
            focusView = txtCursoNombreView;
            cancel = true;
        } else if (TextUtils.isEmpty(nombreSemestre)) {
            txtCursoSemestreView.setError(getString(R.string.error_field_required));
            focusView = txtCursoSemestreView;
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
            String url = getString(R.string.base_url) + "curso";
            JSONObject params = new JSONObject();
            try {
                params.put("idCurso", 0);
                params.put("nombreCurso", nombreGrupo);
                params.put("semestre", Integer.parseInt(nombreSemestre));
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
                    txtCursoSemestreView.setError(result.getString("error"));
                    txtCursoSemestreView.requestFocus();
                }
            } catch (InterruptedException e) {
                Log.e("InterruptedException", e.getMessage());
            } catch (ExecutionException e) {
                Log.e("ExecutionException", e.getMessage());
            } catch (JSONException e) {
                Log.e("JSONException", e.getMessage());
            }
        }
    }
}




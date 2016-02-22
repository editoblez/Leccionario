package ecu.universidades.leccionario;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
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
import org.json.JSONObject;

import java.io.IOException;

public class CrearIncidenciaActivity extends ActionBarActivity {
    Button btnSaveView;
    private createIncidenciaTask cIncidenciasTask = null;
    private EditText descripcionView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_incidencia);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        btnSaveView = (Button) findViewById(R.id.btnSaveIncidecia);
        btnSaveView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        descripcionView = (EditText) findViewById(R.id.idDescripcion);
        descripcionView.setError(null);
    }

    private void save() {
        if (cIncidenciasTask != null) {
            return;
        }

        descripcionView.setError(null);
        String descripcion = descripcionView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(descripcion)) {
            descripcionView.setError(getString(R.string.error_field_required));
            focusView = descripcionView;
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
            cIncidenciasTask = new createIncidenciaTask(descripcion);
            cIncidenciasTask.execute((Void) null);
        }
    }

    public class createIncidenciaTask extends AsyncTask<Void, Void, Boolean> {
        private final String descripcion;
        private boolean errorFlag = false;
        private String error_msg = "";
        HttpEntity entityResponse;
        JSONObject jsonResult;

        createIncidenciaTask(String breveDescripcion) {

            descripcion = breveDescripcion;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            HttpClient httpClient = new DefaultHttpClient();
            //Create url for request
            String url = getString(R.string.base_url) + "incidencia";
            HttpPost httpPost = new HttpPost(url);
            //Set type of request
            httpPost.setHeader("Content-type", "application/json");
            //Prepare a jsonObject for request
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("idIncidencia", 0);
                jsonObject.put("descripcion", descripcion);
                jsonObject.put("activa", true);
            } catch (Exception e) {
                errorFlag = true;
                error_msg = getString(R.string.error_unable_set_connection);
            }
            //Make a connection
            if (!errorFlag) {
                try {
                    StringEntity se = new StringEntity(jsonObject.toString());
                    httpPost.setEntity(se);
                    HttpResponse response = httpClient.execute(httpPost);
                    if (response.getStatusLine().getStatusCode() != 200
                            && response.getStatusLine().getStatusCode() != 204) //!OK
                    {
                        errorFlag = true;
                        error_msg = getString(R.string.error_http_response);
                    }
                } catch (ClientProtocolException e) {
                    errorFlag = true;
                    error_msg = getString(R.string.error_protocol_connection);
                } catch (IOException e) {
                    errorFlag = true;
                    error_msg = getString(R.string.error_waiting_response);
                }
            }
            return !errorFlag;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            cIncidenciasTask = null;
            //showProgress(false);
            if (!success) {

                descripcionView.setError(error_msg);
                descripcionView.requestFocus();

            } else {
                Context context = getApplicationContext();
                CharSequence text = getString(R.string.success_msg);
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.setGravity(Gravity.TOP | Gravity.RIGHT, 0, 0);
                toast.show();
            }

            descripcionView.setText(null);
            errorFlag = false;
            error_msg = "";
        }

        @Override
        protected void onCancelled() {
            /*mAuthTask = null;
            showProgress(false);*/
        }
    }

}

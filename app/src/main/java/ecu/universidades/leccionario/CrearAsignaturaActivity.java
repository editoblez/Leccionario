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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.IOException;

public class CrearAsignaturaActivity extends ActionBarActivity {

    Button btnSaveView;
    private createMateriaTask cMateriaTask = null;
    private EditText nombreMateriaView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_asignatura);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnSaveView = (Button) findViewById(R.id.btnSaveTurnoOKView);
        btnSaveView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        nombreMateriaView = (EditText) findViewById(R.id.crearMateria);

        nombreMateriaView.setError(null);

    }

    private void save() {
        if (cMateriaTask != null) {
            return;
        }

        nombreMateriaView.setError(null);
        String nombreMateria = nombreMateriaView.getText().toString();
        boolean cancel = false;
        View focusView = null;
        if (TextUtils.isEmpty(nombreMateria))
        {
            nombreMateriaView.setError(getString(R.string.error_field_required));
            focusView = nombreMateriaView;
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
            cMateriaTask = new createMateriaTask(nombreMateria);
            cMateriaTask.execute((Void) null);
        }

    }

    public class createMateriaTask extends AsyncTask<Void, Void, Boolean> {
        private final String nombreMateria;
        private boolean errorFlag = false;
        private String error_msg = "";
        HttpEntity entityResponse;
        JSONObject jsonResult;

        createMateriaTask (String materia){
            nombreMateria = materia;
        }
        @Override
        protected Boolean doInBackground(Void... voids) {
            HttpClient httpClient = new DefaultHttpClient();
            //Create url for request
            String url = getString(R.string.base_url) + "materia";
            HttpPost httpPost = new HttpPost(url);
            //Set type of request
            httpPost.setHeader("Content-type", "application/json");
            //Prepare a jsonObject for request
            JSONObject jsonObject = new JSONObject();

            try
            {
                jsonObject.put("idMateria", 0);
                jsonObject.put("nombreMateria", nombreMateria);
                jsonObject.put("activo", true);
            }
            catch (Exception e)
            {
                errorFlag = true;
                error_msg = getString(R.string.error_unable_set_connection);
            }

            //Make a connection
            if (!errorFlag)
            {
                try
                {
                    StringEntity se = new StringEntity(jsonObject.toString());
                    httpPost.setEntity(se);
                    HttpResponse response = httpClient.execute(httpPost);
                    if (response.getStatusLine().getStatusCode() != 200
                            && response.getStatusLine().getStatusCode() != 204) //!OK
                    {
                        errorFlag = true;
                        error_msg = getString(R.string.error_http_response);
                    }
                }
                catch (ClientProtocolException e)
                {
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
            cMateriaTask = null;
            //showProgress(false);
            if (!success) {
                nombreMateriaView.setError(error_msg);
                nombreMateriaView.requestFocus();
            }
            else{
                Context context = getApplicationContext();
                CharSequence text = getString(R.string.success_msg);
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.setGravity(Gravity.TOP| Gravity.RIGHT, 0, 0);
                toast.show();
            }
            nombreMateriaView.setText(null);
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

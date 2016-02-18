package ecu.universidades.leccionario;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
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

public class CrearGrupoActivity extends ActionBarActivity {


    Button btnSaveView;
    private createGrupoTask cGrupoTask = null;
    private EditText nombreGrupoView;
    private EditText nombreSemestreView;


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


        btnSaveView = (Button) findViewById(R.id.btnSaveCurso);
        btnSaveView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        nombreGrupoView = (EditText) findViewById(R.id.nombreGrupo);

        nombreGrupoView.setError(null);

        nombreSemestreView = (EditText) findViewById(R.id.semestre);

        nombreSemestreView.setError(null);

    }

    private void save() {
        if (cGrupoTask != null) {
            return;
        }
        nombreGrupoView.setError(null);
        String nombreGrupo = nombreGrupoView.getText().toString();

        nombreSemestreView.setError(null);
        String nombreSemestre = nombreSemestreView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(nombreGrupo)) {
            nombreGrupoView.setError(getString(R.string.error_field_required));
            focusView = nombreGrupoView;
            cancel = true;
        } else if (TextUtils.isEmpty(nombreSemestre)) {
            nombreSemestreView.setError(getString(R.string.error_field_required));
            focusView = nombreSemestreView;
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
            cGrupoTask = new createGrupoTask(nombreGrupo, nombreSemestre);
            cGrupoTask.execute((Void) null);
        }

    }

    public class createGrupoTask extends AsyncTask<Void, Void, Boolean> {
        private final String nombreGrupo;
        private final String nombreSemestre;
        private boolean errorFlag = false;
        private String error_msg = "";
        HttpEntity entityResponse;
        JSONObject jsonResult;

        createGrupoTask(String nombreG, String semestre) {
            nombreGrupo = nombreG;
            nombreSemestre = semestre;
        }
        @Override
        protected Boolean doInBackground(Void... voids) {
            HttpClient httpClient = new DefaultHttpClient();
            //Create url for request
            String url = getString(R.string.base_url) + "curso";
            HttpPost httpPost = new HttpPost(url);
            //Set type of request
            httpPost.setHeader("Content-type", "application/json");
            //Prepare a jsonObject for request
            JSONObject jsonObject = new JSONObject();

            try
            {
                jsonObject.put("idCurso", 0);
                jsonObject.put("semestre",nombreSemestre);
                jsonObject.put("nombreCurso", nombreGrupo);
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
            cGrupoTask = null;
            //showProgress(false);
            if (!success) {
                nombreGrupoView.setError(error_msg);
                nombreGrupoView.requestFocus();
                nombreSemestreView.setError(error_msg);
                nombreSemestreView.requestFocus();

            }
            else{
                Context context = getApplicationContext();
                CharSequence text = getString(R.string.success_msg);
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.setGravity(Gravity.TOP| Gravity.RIGHT, 0, 0);
                toast.show();
            }
            nombreGrupoView.setText(null);
            nombreSemestreView.setText(null);
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




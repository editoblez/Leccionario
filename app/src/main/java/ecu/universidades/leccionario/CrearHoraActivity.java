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

public class CrearHoraActivity extends ActionBarActivity {
    Button btnSaveView;
    private createHoraTask cHoraTask = null;
    private EditText idHoraView;
    private EditText horaInicioView;
    private EditText horaFinView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_hora);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnSaveView = (Button) findViewById(R.id.btnSaveHora);
        btnSaveView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        idHoraView = (EditText) findViewById(R.id.crearMateria);
        idHoraView.setError(null);

        horaInicioView = (EditText) findViewById(R.id.crearMateria);
        horaInicioView.setError(null);

        horaFinView = (EditText) findViewById(R.id.crearMateria);
        horaFinView.setError(null);


    }
    private void save() {
        if (cHoraTask != null) {
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
            cHoraTask = new createHoraTask(idHora, horaInicio,horaFin );
            cHoraTask.execute((Void) null);
        }

    }
    public class createHoraTask extends AsyncTask<Void, Void, Boolean> {
        private final String idHora;
        private final String horaInicio;
        private final String horaFin;

        private boolean errorFlag = false;
        private String error_msg = "";
        HttpEntity entityResponse;
        JSONObject jsonResult;

        createHoraTask (String hora, String inicioH, String finH ){
            idHora = hora;
            horaInicio = inicioH;
            horaFin = finH;
        }
        @Override
        protected Boolean doInBackground(Void... voids) {
            HttpClient httpClient = new DefaultHttpClient();
            //Create url for request
            String url = getString(R.string.base_url) + "hora";
            HttpPost httpPost = new HttpPost(url);
            //Set type of request
            httpPost.setHeader("Content-type", "application/json");
            //Prepare a jsonObject for request
            JSONObject jsonObject = new JSONObject();

            try
            {
                jsonObject.put("idHora", 0);
                jsonObject.put("horaInicio", horaInicio);
                jsonObject.put("horaFin", horaFin);
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
            cHoraTask = null;
            //showProgress(false);
            if (!success) {
                idHoraView.setError(error_msg);
                idHoraView.requestFocus();

                horaInicioView.setError(error_msg);
                horaInicioView.requestFocus();

                horaFinView.setError(error_msg);
                horaFinView.requestFocus();
            }
            else{
                Context context = getApplicationContext();
                CharSequence text = getString(R.string.success_msg);
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.setGravity(Gravity.TOP| Gravity.RIGHT, 0, 0);
                toast.show();
            }
            idHoraView.setText(null);
            horaInicioView.setText(null);
            horaFinView.setText(null);
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


package ecu.universidades.leccionario;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends ActionBarActivity {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
   private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mUserView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mUserView = (EditText) findViewById(R.id.user);
        mPasswordView = (EditText) findViewById(R.id.password);
        /*mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
        if (id == R.id.login || id == EditorInfo.IME_NULL) {
            attemptLogin();
            return true;
        }
        return false;
    }
});*/

        Button mLoginUserView = (Button) findViewById(R.id.login_button);
        mLoginUserView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }
        // Reset errors.
        mUserView.setError(null);
        mPasswordView.setError(null);
        // Store values at the time of the login attempt.
        String user = mUserView.getText().toString();
        String password = mPasswordView.getText().toString();
        boolean cancel = false;
        View focusView = null;
        // Check for a valid email address.
        if (TextUtils.isEmpty(user))
        {
            mUserView.setError(getString(R.string.error_field_required));
            focusView = mUserView;
            cancel = true;
        }
        else if (TextUtils.isEmpty(password))
        {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(user, password);
            mAuthTask.execute((Void) null);
        }
    }

   /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

     /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
         private final String mUser;
         private final String mPassword;
         private boolean errorFlag = false;
         private String error_msg = "";
         HttpEntity entityResponse;
         JSONObject jsonResult;
         int idUsuario;
         int rol;

        UserLoginTask(String user, String password) {
            mUser = user;
            mPassword = password;
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            Log.e("doInBackground: ", "Entrando");
            // TODO: attempt authentication against a network service.
            //Create client
            HttpClient httpClient = new DefaultHttpClient();
            //Create url for request
            String url = getString(R.string.base_url) + "usuario/login";
            HttpPost httpPost = new HttpPost(url);
            //Set type of request
            httpPost.setHeader("Content-type", "application/json");
            //Prepare a jsonObject for request
            JSONObject jsonObject = new JSONObject();
            try
            {
                jsonObject.put("username", mUser);
                jsonObject.put("password", mPassword);
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
                    if (response.getStatusLine().getStatusCode() != 200) //!OK
                    {
                        errorFlag = true;
                        error_msg = getString(R.string.error_http_response);
                    }
                    else {
                        String result = EntityUtils.toString(response.getEntity());
                        jsonResult = new JSONObject(result);
                        Log.e("Result of http: ", jsonResult.toString());
                    }
                }
                catch (ClientProtocolException e)
                {
                    errorFlag = true;
                    error_msg = getString(R.string.error_protocol_connection);
                } catch (JSONException e) {
                    errorFlag = true;
                    error_msg = getString(R.string.error_unable_read_response);
                } catch (IOException e) {
                    errorFlag = true;
                    error_msg = getString(R.string.error_waiting_response);
                }
            }

            if (!errorFlag)
            {
                try
                {
                    if (jsonResult.getBoolean("success"))
                    {
                        this.idUsuario = jsonResult.getInt("idUsuario");
                        this.rol = jsonResult.getInt("rol");
                    }
                    else
                    {
                        errorFlag = true;
                        error_msg = jsonResult.getString("description");
                    }
                }
                catch (JSONException e)
                {
                    errorFlag = true;
                    error_msg = getString(R.string.error_unable_read_response);
                }

            }
            return !errorFlag;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Log.e("onPostExecute: ", "Entrando");
            mAuthTask = null;
            showProgress(false);
            if (success && !errorFlag) {
                try {
                    Intent intent;
                    if (jsonResult.getInt("rol") == 1)
                    {
                        intent = new Intent(getApplicationContext(), InstructorActivity.class);
                    }
                    else
                    {
                        intent = new Intent(getApplicationContext(), ProfesorActivity.class);
                    }
                    intent.putExtra("Message", jsonResult.get("idUsuario").toString());
                    intent.putExtra("idUsuario", jsonResult.get("idUsuario").toString());
                    startActivity(intent);
                } catch (JSONException e) {
                    mPasswordView.setError(getString(R.string.error_json));
                }
            } else {
                //mPasswordView.setError(getString(R.string.error_incorrect_password));
                mUserView.setError(error_msg);
                mUserView.requestFocus();
            }
            mPasswordView.setText(null);
            errorFlag = false;
            error_msg = "";
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }



}


package ecu.universidades.leccionario;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by EASYSOFT on 20/2/2016.
 */
public class ServiceClass extends AsyncTask<Void, Void, JSONObject> {
    ActionBarActivity activity;
    String url;
    HttpRequestType httpRequestType;
    HttpResponseType httpResponseType;
    JSONObject params;
    Boolean errorFlag;
    String error_msg;
    JSONObject responseJsonObject = new JSONObject();

    HttpRequestBase requestHTTP;
    HttpResponse responseHttp;

    ServiceClass(ActionBarActivity activity, String URL,
                 HttpRequestType httpRequestType, HttpResponseType httpResponseType) {
        this.activity = activity;
        this.url = URL;
        this.httpRequestType = httpRequestType;
        this.httpResponseType = httpResponseType;
        this.params = null;
        errorFlag = false;
        error_msg = "";

    }

    ServiceClass(ActionBarActivity activity, String URL,
                 HttpRequestType httpRequestType, HttpResponseType httpResponseType,
                 JSONObject params) {
        this.activity = activity;
        this.url = URL;
        this.httpRequestType = httpRequestType;
        this.httpResponseType = httpResponseType;
        this.params = params;
        errorFlag = false;
    }

    @Override
    protected JSONObject doInBackground(Void... voids) {
        //Create the client
        HttpClient httpClient = new DefaultHttpClient();
        switch (this.httpRequestType)
        {
            case DELETE: break;
            case GET:
                requestHTTP = new HttpGet(url);
                break;
            case POST:
                requestHTTP = new HttpPost(url);
                break;
            case PUT:
                requestHTTP = new HttpPut(url);
                break;
        }
        //Set Heador to JSON
        requestHTTP.setHeader("Content-type", "application/json");
        //Make a connection
        if (!errorFlag) {
            try {
                if (this.params != null)
                {
                    StringEntity se = new StringEntity(this.params.toString());
                    if (requestHTTP instanceof HttpPost)
                        ((HttpPost) requestHTTP).setEntity(se);
                    if (requestHTTP instanceof HttpPut)
                        ((HttpPut) requestHTTP).setEntity(se);
                }
                responseHttp = httpClient.execute(requestHTTP);
                int statusCode = responseHttp.getStatusLine().getStatusCode();
                if ( statusCode != 200
                        && statusCode != 204) //!OK
                {
                    errorFlag = true;
                    error_msg = activity.getString(R.string.error_SQL);
                }
            } catch (ClientProtocolException e) {
                errorFlag = true;
                error_msg = activity.getString(R.string.error_protocol_connection);
            } catch (IOException e) {
                errorFlag = true;
                error_msg = activity.getString(R.string.error_waiting_response);
            }
        }
        try {
            if (httpRequestType != HttpRequestType.POST && httpRequestType != HttpRequestType.PUT)
            {
                switch (this.httpResponseType)
                {
                    case JSONARRAYOBJECT:
                        responseJsonObject.put("result", errorFlag ? "[]" :
                                new JSONArray(EntityUtils.toString(responseHttp.getEntity())));
                        break;
                    case JSONOBJECT:
                        responseJsonObject.put("result", errorFlag ? "" :
                                new JSONObject(EntityUtils.toString(responseHttp.getEntity())));
                        break;
                    default:
                        responseJsonObject.put("result", errorFlag ? "[]" :
                                new JSONObject());
                }
            }
            if (errorFlag)
            {
                responseJsonObject.put("error", error_msg);
            }
        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage());
        } catch (IOException e) {
            Log.e("JSONException", e.getMessage());
        }
        return responseJsonObject;
    }

    @Override
    protected void onPostExecute(final JSONObject success) {

    }

    @Override
    protected void onCancelled() {
            /*mAuthTask = null;
            showProgress(false);*/

    }

}

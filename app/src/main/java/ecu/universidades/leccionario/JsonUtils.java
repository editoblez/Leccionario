package ecu.universidades.leccionario;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by EASYSOFT on 20/2/2016.
 */
public class JsonUtils {

    public static JSONObject getJsonObjectFromJsonArray (JSONArray jsonArray, String name, Object value)
    {
        JSONObject jsonObject = null;
        if (jsonArray == null) return null;
        for (int i = 0; i < jsonArray.length(); i++)
        {
            try {
                jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.get(name).equals(value)) break;
            } catch (JSONException e) {
                Log.e("JSONException", e.getMessage());
            }
        }
        return jsonObject;
    }


    public static void makeMessage (ActionBarActivity context, String msg)
    {
        Toast toast = Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_LONG);
        toast.show();
    }
}

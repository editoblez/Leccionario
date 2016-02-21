package ecu.universidades.leccionario;

import android.util.Log;

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
}

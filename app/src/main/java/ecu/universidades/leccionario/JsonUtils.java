package ecu.universidades.leccionario;

import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

    public static String makeMD5 (String text)
    {
        StringBuffer sb = null;
        try {
            MessageDigest digester = null;
            digester = MessageDigest.getInstance("MD5");
            byte[] bytes = text.getBytes("UTF-8");

            byte[] thedigest = digester.digest(bytes);

            //convert the byte to hex format method 1
            sb = new StringBuffer();
            for (int i = 0; i < thedigest.length; i++) {
                sb.append(Integer.toString((thedigest[i] & 0xff) + 0x100, 16).substring(1));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
            return sb.toString();


    }

    public static String isValidCedula (String cedula)
    {
        String result = null;
        if (TextUtils.isEmpty(cedula))
        {
            result = "Campo requerido";
        }
        else if (!TextUtils.isDigitsOnly(cedula))
        {
            result = "Debe contener solo digitos";
        }
        else if (cedula.length() != 10)
        {
            result = "La longitud de la cedula debe ser 10";

        }

        return result;

    }
}

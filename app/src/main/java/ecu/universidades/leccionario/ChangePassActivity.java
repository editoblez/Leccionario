package ecu.universidades.leccionario;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class ChangePassActivity extends ActionBarActivity {

    EditText oldPass;
    EditText newPass;
    EditText newConfirmPass;
    Button btnGrabar;

    int idUsuario;
    ServiceClass serviceClass = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        idUsuario = getIntent().getExtras().getInt("idUsuario");
        oldPass = (EditText ) findViewById(R.id.oldPass);
        newPass = (EditText ) findViewById(R.id.newPass);
        newConfirmPass = (EditText ) findViewById(R.id.newConfirmPass);
        btnGrabar = (Button ) findViewById(R.id.btnGrabar);
        btnGrabar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
    }

    private void save() {
        if (serviceClass != null) return;
        View focus = null;
        boolean cancel = false;
        if (TextUtils.isEmpty(oldPass.getText()))
        {
            cancel = true;
            oldPass.setError(getString(R.string.error_field_required));
            focus = oldPass;

        }
        else if (TextUtils.isEmpty(newPass.getText()))
        {
            cancel = true;
            newPass.setError(getString(R.string.error_field_required));
            focus = newPass;

        }else if (TextUtils.isEmpty(newConfirmPass.getText()))
        {
            cancel = true;
            newConfirmPass.setError(getString(R.string.error_field_required));
            focus = newConfirmPass;

        }else if (! (newPass.getText().toString().equals(newConfirmPass.getText().toString())))
        {
            cancel = true;
            newConfirmPass.setError(getString(R.string.error_mismatch_password));
            focus = newConfirmPass;

        }

        if (cancel)
        {
            focus.requestFocus();
        }
        else
        {
            String url = getString(R.string.base_url) + "usuario/changePassword";
            JSONObject params = new JSONObject();
            try {
                params.put("idUsuario", this.idUsuario);
                params.put("oldPassword", JsonUtils.makeMD5(oldPass.getText().toString()));
                params.put("password", JsonUtils.makeMD5(newPass.getText().toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            serviceClass = new ServiceClass(this, url,
                    HttpRequestType.POST, HttpResponseType.NONE, params);
            JSONObject response;
            try {
                response = serviceClass.execute().get();
                if (response.isNull("error")) {
                    JsonUtils.makeMessage(this, getString(R.string.success_msg));
                }
                else
                {
                    JsonUtils.makeMessage(this, response.getString("error"));
                }
            } catch (InterruptedException e) {
                JsonUtils.makeMessage(this, e.getMessage());
            } catch (ExecutionException e) {
                JsonUtils.makeMessage(this, e.getMessage());
            } catch (JSONException e) {
                JsonUtils.makeMessage(this, e.getMessage());
            }
            this.serviceClass = null;
        }


    }

}

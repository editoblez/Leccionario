package ecu.universidades.leccionario;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class ActualizarPersonaActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener{

    LinearLayout EstudianteLayout;
    LinearLayout UsuarioLayout;
    LinearLayout BuscarLayout;
    LinearLayout CommonLayout;
    LinearLayout ButtonLayout;

    EditText txtPersonaCedula;
    EditText txtPersona1erNombre;
    EditText txtPersona2doNombre;
    EditText txtPersona1erApellido;
    EditText txtPersona2doApellido;
    CheckBox chPersonaActivo;

    EditText txtPersonaUsuario;
    EditText txtPersonaPassword;
    EditText txtPersonaPasswordConfirm;
    Spinner spEstudianteSemestre;
    Spinner spEstudianteCurso;

    Boolean isUser;
    ServiceClass serviceClass = null;

    Button btnPersonaOK;
    Button btnPersonaFind;
    Button btnPersonaBack;

    List<String> listAllGrupo;
    List<String> listAllSemestre;

    JSONArray jsonArraySemestre;
    JSONArray jsonArrayGrupo;
    int idSelectedSemestre = -1;
    int idSelectedGrupo = -1;

    int idPersona = -1;

    PersonaType personaType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_persona);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnPersonaFind = (Button) findViewById(R.id.btnPersonaFind);
        btnPersonaFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                find();

            }
        });
        btnPersonaOK = (Button) findViewById(R.id.btnPersonaOK);
        btnPersonaOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
        btnPersonaBack = (Button) findViewById(R.id.btnPersonaBack);
        btnPersonaBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        personaType = (PersonaType) getIntent().getExtras().get("type");
        EstudianteLayout = (LinearLayout) findViewById(R.id.EstudianteLayoutView);
        UsuarioLayout = (LinearLayout) findViewById(R.id.UsuarioLayoutView);
        BuscarLayout = (LinearLayout) findViewById(R.id.BuscarLayoutView);
        CommonLayout = (LinearLayout) findViewById(R.id.CommonLayoutView);
        ButtonLayout= (LinearLayout) findViewById(R.id.ButtonLayout);


        EstudianteLayout.setVisibility(View.GONE);
        UsuarioLayout.setVisibility(View.GONE);
        CommonLayout.setVisibility(View.GONE);
        ButtonLayout.setVisibility(View.GONE);
        BuscarLayout.setVisibility(View.VISIBLE);

        datosComunPersona();
        Intent intent = getIntent();

    }

    private void back() {
        EstudianteLayout.setVisibility(View.GONE);
        UsuarioLayout.setVisibility(View.GONE);
        CommonLayout.setVisibility(View.GONE);
        ButtonLayout.setVisibility(View.GONE);
        BuscarLayout.setVisibility(View.VISIBLE);
    }

    private void find() {
        txtPersonaCedula.setError(null);
        if (serviceClass != null) return;
        if (TextUtils.isEmpty(txtPersonaCedula.getText()) )
        {
            txtPersonaCedula.setError(getString(R.string.error_field_required));
            txtPersonaCedula.requestFocus();
            return;
        }
        BuscarLayout.setVisibility(View.GONE);
        CommonLayout.setVisibility(View.VISIBLE);
        UsuarioLayout.setVisibility(View.VISIBLE);
        ButtonLayout.setVisibility(View.VISIBLE);

        switch (personaType) {
            case ESTUDIANTE:
                EstudianteLayout.setVisibility(View.VISIBLE);
                UsuarioLayout.setVisibility(View.GONE);
                initEstudiante();
                //loadEstudiante ();
                load();
                break;
            default:
                EstudianteLayout.setVisibility(View.GONE);
                UsuarioLayout.setVisibility(View.VISIBLE);
                initUser();
                load();
                break;
        }

    }

    private void load() {
        if (this.serviceClass != null) return;
        String url = getString(R.string.base_url);
        url += personaType == PersonaType.USUARIO
                ? "usuario/getAllByCedula/"
                : "estudiante/getAllByCedula/";
        url +=  txtPersonaCedula.getText();
        serviceClass = new ServiceClass(this, url,
                HttpRequestType.GET, HttpResponseType.JSONOBJECT);
        JSONObject response;
        try {
            response = serviceClass.execute().get();
            if (response.isNull("error")) {
                response = response.getJSONObject("result");
                txtPersonaCedula.setText(response.getString("cedula"));
                txtPersona1erNombre.setText(response.getString("primerNombre"));
                txtPersona2doNombre.setText(response.getString("segundoNombre"));
                txtPersona1erApellido.setText(response.getString("primerApellido"));
                txtPersona2doApellido.setText(response.getString("segundoApellido"));
                chPersonaActivo.setChecked(response.getBoolean("activo"));
                if (personaType == PersonaType.USUARIO)
                {
                    idPersona = response.getInt("idUsuario");
                    txtPersonaUsuario.setText(response.getString("nombreUsuario"));
                }
                else
                {
                    idPersona = response.getInt("idEstudiante");
                    String semestre = String.valueOf(response.getInt("semestre"));
                    String idCurso = String.valueOf(response.getInt("idCurso"));
                    String nombreCurso = String.valueOf(response.getString("nombreCurso"));
                    for (int i = 0; i < spEstudianteSemestre.getCount(); i++)
                    {
                        if (spEstudianteSemestre.getAdapter().getItem(i).equals(semestre))
                        {
                            spEstudianteSemestre.setSelection(i);
                        }
                    }

                    for (int i = 0; i < spEstudianteCurso.getCount(); i++)
                    {
                        if (spEstudianteCurso.getAdapter().getItem(i).equals(semestre))
                        {
                            spEstudianteCurso.setSelection(i);
                        }
                    }

                }
            }
            else
            {
                JsonUtils.makeMessage(this, response.getString("error"));
                back();
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

    private void initEstudiante() {
        this.isUser = false;
        spEstudianteSemestre = (Spinner) findViewById(R.id.spEstudianteSemestre);
        spEstudianteCurso = (Spinner) findViewById(R.id.spEstudianteCurso);
        spEstudianteSemestre.setOnItemSelectedListener(this);
        spEstudianteCurso.setOnItemSelectedListener(this);
        fillSemestre();

    }

    private void initUser() {
        this.isUser = true;
        txtPersonaUsuario = (EditText) findViewById(R.id.txtPersonaUsuario);
        txtPersonaUsuario.setError(null);

        txtPersonaPassword = (EditText) findViewById(R.id.txtPersonaPassword);
        txtPersonaPassword.setError(null);

        txtPersonaPasswordConfirm = (EditText) findViewById(R.id.txtPersonaPasswordConfirm);
        txtPersonaPasswordConfirm.setError(null);

    }
    private void datosComunPersona(){

        txtPersonaCedula = (EditText) findViewById(R.id.txtPersonaCedula);
        txtPersonaCedula.setError(null);

        txtPersona1erNombre = (EditText) findViewById(R.id.txtPersona1erNombre);
        txtPersona1erNombre.setError(null);

        txtPersona2doNombre = (EditText) findViewById(R.id.txtPersona2doNombre);
        txtPersona2doNombre.setError(null);

        txtPersona1erApellido = (EditText) findViewById(R.id.txtPersona1erApellido);
        txtPersona1erApellido.setError(null);

        txtPersona2doApellido = (EditText) findViewById(R.id.txtPersona2doApellido);
        txtPersona2doApellido.setError(null);

        chPersonaActivo = (CheckBox) findViewById(R.id.chPersonaActivo);


    }

    private void save() {
        if (this.serviceClass != null) return;
        boolean cancel = false;
        boolean msg_toast = false;
        String msg = getString(R.string.error_field_required);
        View focus = null;
        txtPersonaCedula.setError(null);
        txtPersona2doApellido.setError(null);
        txtPersona1erApellido.setError(null);
        txtPersona2doNombre.setError(null);
        txtPersona1erNombre.setError(null);
        if (TextUtils.isEmpty(txtPersonaCedula.getText())){
            cancel = true;
            txtPersonaCedula.setError(getString(R.string.error_field_required));
            focus = txtPersonaCedula;
        }
        else if (TextUtils.isEmpty(txtPersona1erNombre.getText().toString())){
            cancel = true;
            txtPersona1erNombre.setError(getString(R.string.error_field_required));
            focus = txtPersona1erNombre;
        }
        else if (TextUtils.isEmpty(txtPersona2doNombre.getText())){
            cancel = true;
            txtPersona2doNombre.setError(getString(R.string.error_field_required));
            focus = txtPersona2doNombre;
        }
        else if (TextUtils.isEmpty(txtPersona1erApellido.getText())){
            txtPersona1erApellido.setError(getString(R.string.error_field_required));
            cancel = true;
            focus = txtPersona1erApellido;
        }
        else if (TextUtils.isEmpty(txtPersona2doApellido.getText())){
            cancel = true;
            txtPersona2doApellido.setError(getString(R.string.error_field_required));
            focus = txtPersona2doApellido;
        }
        else if (this.isUser)
        {
            txtPersonaUsuario.setError(null);
            txtPersonaPassword.setError(null);
            txtPersonaPasswordConfirm.setError(null);
            if (TextUtils.isEmpty(txtPersonaUsuario.getText())){
                cancel = true;
                txtPersonaUsuario.setError(getString(R.string.error_field_required));
                focus = txtPersonaUsuario;
            }
            else if (TextUtils.isEmpty(txtPersonaPassword.getText())){
                cancel = true;
                txtPersonaPassword.setError(getString(R.string.error_field_required));
                focus = txtPersonaPassword;
            }
            else if (TextUtils.isEmpty(txtPersonaPasswordConfirm.getText())){
                cancel = true;
                txtPersonaPasswordConfirm.setError(getString(R.string.error_field_required));
                focus = txtPersonaPasswordConfirm;
            }
            else if (!(txtPersonaPassword.getText().toString().equals(txtPersonaPasswordConfirm.getText().toString())))
            {
                cancel = true;
                focus = txtPersonaPasswordConfirm;
                txtPersonaPasswordConfirm.setError(getString(R.string.error_mismatch_password));

            }
            else if (idPersona == -1)
            {
                cancel = true;
                focus = txtPersonaCedula;
                txtPersonaCedula.setError(getString(R.string.error_field_required));
            }

        }
        else
        {
            if (idSelectedSemestre == -1 || idSelectedGrupo == -1)
            {
                cancel = true;
                focus = txtPersonaPasswordConfirm;
                msg = getString(R.string.error_field_required);
                msg_toast = true;
            }
        }

        if (cancel)
        {
            if (msg_toast) {
                JsonUtils.makeMessage(this, msg);
            }
            else {
                focus.requestFocus();
            }
        }
        else
        {
            //All is fine
            JSONObject jsonObject=new JSONObject();
            String url = getString(R.string.base_url);
            try {
                jsonObject.put("idPersona", idPersona);
                jsonObject.put("cedula",txtPersonaCedula.getText().toString());
                jsonObject.put("primerNombre",txtPersona1erNombre.getText().toString());
                jsonObject.put("segundoNombre",txtPersona2doNombre.getText().toString());
                jsonObject.put("primerApellido",txtPersona1erApellido.getText().toString());
                jsonObject.put("segundoApellido",txtPersona2doApellido.getText().toString());
                jsonObject.put("activo", chPersonaActivo.isChecked());
                if (isUser)
                {
                    jsonObject.put("nombreUsuario",txtPersonaUsuario.getText().toString());
                    jsonObject.put("claveUsuario",JsonUtils.makeMD5(txtPersonaPassword.getText().toString()));
                    url += "usuario/update";
                }
                else
                {
                    jsonObject.put("idCurso",this.idSelectedGrupo);
                    url += "estudiante/update";
                }
                serviceClass = new ServiceClass(this, url, HttpRequestType.POST,
                        HttpResponseType.NONE, jsonObject);
                JSONObject response =  serviceClass.execute().get();
                if (response.isNull("error")) {
                    JsonUtils.makeMessage(this, getString(R.string.success_msg));
                }
                else
                {
                    JsonUtils.makeMessage(this, response.getString("error"));
                }
            } catch (JSONException e) {
                JsonUtils.makeMessage(this, e.getMessage());
            } catch (InterruptedException e) {
                JsonUtils.makeMessage(this, e.getMessage());
            } catch (ExecutionException e) {
                JsonUtils.makeMessage(this, e.getMessage());
            }
        }
        serviceClass = null;
    }


    private void fillSemestre() {
        if (this.serviceClass != null) return;
        String url = getString(R.string.base_url) + "curso/getSemestre";
        serviceClass = new ServiceClass(this, url,
                HttpRequestType.GET, HttpResponseType.JSONARRAYOBJECT);
        JSONObject response;
        listAllSemestre = null;
        try {
            response = serviceClass.execute().get();
            if (response.isNull("error")) {
                listAllSemestre = new ArrayList<String>();
                this.jsonArraySemestre = response.getJSONArray("result");
                for (int i = 0; i < this.jsonArraySemestre.length(); i++)
                {
                    JSONObject tmpJsonObject = jsonArraySemestre.getJSONObject(i);
                    listAllSemestre.add(tmpJsonObject.getString("semestre"));
                }
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

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,listAllSemestre);
        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        this.spEstudianteSemestre.setAdapter(dataAdapter);
        this.serviceClass = null;
    }

    private void fillGrupo() {
        if (this.serviceClass != null) return;
        String url = getString(R.string.base_url) + "curso/getAllCursoBSemestre/" + idSelectedSemestre;
        serviceClass = new ServiceClass(this, url,
                HttpRequestType.GET, HttpResponseType.JSONARRAYOBJECT);
        JSONObject response;
        listAllGrupo = null;
        try {
            response = serviceClass.execute().get();
            if (response.isNull("error")) {
                listAllGrupo = new ArrayList<String>();
                this.jsonArrayGrupo = response.getJSONArray("result");
                for (int i = 0; i < this.jsonArrayGrupo.length(); i++)
                {
                    JSONObject tmpJsonObject = jsonArrayGrupo.getJSONObject(i);
                    listAllGrupo.add(tmpJsonObject.getString("nombreCurso"));
                }
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

        if (listAllGrupo != null) {
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                    (this, android.R.layout.simple_spinner_item, listAllGrupo);
            dataAdapter.setDropDownViewResource
                    (android.R.layout.simple_spinner_dropdown_item);
            this.spEstudianteCurso.setAdapter(dataAdapter);
        }
        this.serviceClass = null;
    }



    private void onItemSelectedSemestre(AdapterView<?> parent, View view, int position, long id) {
        JSONObject jsonObject = JsonUtils.getJsonObjectFromJsonArray(this.jsonArraySemestre,
                "semestre",
                Integer.parseInt(parent.getItemAtPosition(position).toString()));
        if (jsonObject != null)
        {
            try {
                this.idSelectedSemestre = jsonObject.getInt("semestre");
                fillGrupo();
            } catch (JSONException e) {
                Log.e("JSONException ", e.getMessage());
            }
        }

    }
    private void onItemSelectedGrupo(AdapterView<?> parent, View view, int position, long id) {
        JSONObject jsonObject = JsonUtils.getJsonObjectFromJsonArray(this.jsonArrayGrupo,
                "nombreCurso",
                parent.getItemAtPosition(position).toString());
        if (jsonObject != null)
        {
            try {
                this.idSelectedGrupo = jsonObject.getInt("idCurso");
            } catch (JSONException e) {
                Log.e("JSONException ", e.getMessage());
            }
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId())
        {
            case R.id.spEstudianteSemestre:
                this.onItemSelectedSemestre(parent, view, position, id);
                break;
            case R.id.spEstudianteCurso:
                this.onItemSelectedGrupo(parent, view, position, id);
                break;
            default:
                Log.e("MSG", "RARO");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.e("MSG", "UMMM");
    }


}

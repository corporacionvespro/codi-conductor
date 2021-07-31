package codi.drive.conductor.chiclayo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import codi.drive.conductor.chiclayo.compartido.PrefUtil;
import codi.drive.conductor.chiclayo.compartido.StringRequestApp;
import static codi.drive.conductor.chiclayo.compartido.Funciones.primero;

/**
 * By: el-bryant
 */

public class Registro2Activity extends AppCompatActivity implements Response.ErrorListener, Response.Listener<String> {
    @BindView(R.id.btnFinalizarRegistro) Button btnFinalizarRegistro;
    @BindView(R.id.chkPoliticas) CheckBox chkPoliticas;
    @BindView(R.id.etAnio) EditText etAnio;
    @BindView(R.id.etCiudad) EditText etCiudad;
    @BindView(R.id.etColor) EditText etColor;
    @BindView(R.id.etEmpresa) EditText etEmpresa;
    @BindView(R.id.etMarca) EditText etMarca;
    @BindView(R.id.etModelo) EditText etModelo;
    @BindView(R.id.etPlaca) EditText etPlaca;
    @BindView(R.id.flayCargando) FrameLayout flayCargando;
    @BindView(R.id.flayPoliticas) FrameLayout flayPoliticas;
    @BindView(R.id.tvCondiciones) TextView tvCondiciones;
    @BindView(R.id.tvContenido) TextView tvContenido;
    AnimatedCircleLoadingView animatedCircleLoadingView;
    PrefUtil prefUtil;
    RequestQueue requestQueue;
    String apellidos, celular, clave, correo, dni, nombres, foto;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro2);
        ButterKnife.bind(this);
        animatedCircleLoadingView = (AnimatedCircleLoadingView) findViewById(R.id.circle_loading_view);
        prefUtil = new PrefUtil(this);
        requestQueue = Volley.newRequestQueue(this);
        if (getIntent().getExtras() != null) {
            apellidos = getIntent().getStringExtra("apellidos");
            celular = getIntent().getStringExtra("celular");
            clave = getIntent().getStringExtra("clave");
            correo = getIntent().getStringExtra("correo");
            dni = getIntent().getStringExtra("dni");
            nombres = getIntent().getStringExtra("nombres");
            foto = getIntent().getStringExtra("foto");
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            boolean correcto = jsonObject.getBoolean("correcto");
            if (correcto) {
                animatedCircleLoadingView.stopOk();
                flayCargando.removeAllViews();
                prefUtil.saveGenericValue("foto", jsonObject.getString("foto"));
                Intent intent = new Intent(Registro2Activity.this, Acceso2Activity.class);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                startActivity(intent);
                Toast.makeText(this, "Cuenta creada satisfactoriamente, comunicarse al 997627162 para habilitar su cuenta y empezar a ganar dinero con CODI", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Log.i("PerfilActivity",  "onResponse - " + jsonObject.getString("error"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btnAceptar) void ocultarTerminos() {
        flayPoliticas.setVisibility(View.GONE);
    }

    @OnClick(R.id.btnFinalizarRegistro) void registrarPersona() {
        Log.i("registrarPersona", "Registro2Activity");
        if (animatedCircleLoadingView.getParent() != null) {
            ((ViewGroup) animatedCircleLoadingView.getParent()).removeView(animatedCircleLoadingView);
            flayCargando.addView(animatedCircleLoadingView);
            animatedCircleLoadingView.startIndeterminate();
        }
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/registro_persona.php?apellidos=" + prefUtil.getStringValue("apellidos") + "&nombres="
                        + prefUtil.getStringValue("nombres") + "&nro_documento=" + prefUtil.getStringValue("dni") + "&email=" + prefUtil.getStringValue("correo")
                        + "&telefono=" + prefUtil.getStringValue("celular"));
                Log.i("registrarPersona", result);
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.length() > 0) {
                            boolean correcto = jsonObject.getBoolean("correcto");
                            if (correcto) {
                                prefUtil.saveGenericValue("apellidos", apellidos);
                                prefUtil.saveGenericValue("nombres", nombres);
                                prefUtil.saveGenericValue("dni", dni);
                                prefUtil.saveGenericValue("correo", correo);
                                prefUtil.saveGenericValue("telefono", celular);
                                obtenerIdPersona();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    @OnClick(R.id.chkPoliticas) void mostrarBoton() {
        if (chkPoliticas.isChecked()) {
            btnFinalizarRegistro.setVisibility(View.VISIBLE);
            btnFinalizarRegistro.setEnabled(true);
        } else {
            btnFinalizarRegistro.setVisibility(View.GONE);
            btnFinalizarRegistro.setEnabled(false);
        }
    }

    @OnClick(R.id.tvCondiciones) void condiciones() {
        Log.i("condiciones", "Registro1Activity");
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/obtener_terminos.php");
                Log.i("condiciones", result);
                runOnUiThread(() -> {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                String p_c = jsonArray.getJSONObject(i).getString("p_c");
                                if (p_c.equals("C")) {
                                    String terminos = jsonArray.getJSONObject(i).getString("contenido");
                                    flayPoliticas.setVisibility(View.VISIBLE);
                                    tvContenido.setText(terminos);
                                    break;
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    private void agregarLlamada(Map<String, String> params){
        StringRequestApp jsObjRequest = new StringRequestApp(Request.Method.POST, "http://46.101.226.155/vespro/logica/modificar_foto_conductor.php", this,this);
        jsObjRequest.setParametros(params);
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsObjRequest);
    }

    public void obtenerIdConductor() {
        Log.i("obtenerIdConductor", "Registro2Activity");
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/obtener_id_conductor.php?idpersona=" + prefUtil.getStringValue("idPersona"));
                Log.i("obtenerIdConductor", result);
                runOnUiThread(() -> {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        if (jsonArray.length() > 0) {
                            String idConductor = jsonArray.getJSONObject(0).getString("idconductor");
                            prefUtil.saveGenericValue("idConductor", idConductor);
                            registrarUsuario();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    public void obtenerIdPersona() {
        Log.i("obtenerIdPersona", "Registro2Activity");
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/obtener_id_persona.php?apellidos=" + prefUtil.getStringValue("apellidos") + "&nombres="
                        + prefUtil.getStringValue("nombres") + "&nro_documento=" + prefUtil.getStringValue("dni") + "&email=" + prefUtil.getStringValue("correo")
                        + "&telefono=" + prefUtil.getStringValue("celular"));
                Log.i("obtenerIdPersona", result);
                runOnUiThread(() -> {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        if (jsonArray.length() > 0) {
                            String idPersona = jsonArray.getJSONObject(0).getString("idpersona");
                            prefUtil.saveGenericValue("idPersona", idPersona);
                            registrarConductor();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    public void registrarConductor() {
        Log.i("registrarConductor", "Registro2Activity");
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/registrar_conductor.php?idpersona=" + prefUtil.getStringValue("idPersona")
                        + "&estado=T&finscripcion=" + df.format(date) + "&appaterno=" + prefUtil.getStringValue("apellidos").substring(0,
                        prefUtil.getStringValue("apellidos").indexOf(" ")) + "&apmaterno="
                        + prefUtil.getStringValue("apellidos").substring(prefUtil.getStringValue("apellidos").indexOf(" ") + 1) + "&nombres="
                        + prefUtil.getStringValue("nombres") + "&saldo=0.0&promociones=NO");
                Log.i("registrarConductor", result);
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.length() > 0) {
                            boolean correcto = jsonObject.getBoolean("correcto");
                            if (correcto) {
                                obtenerIdConductor();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    public void registrarUsuario() {
        Log.i("registrarUsuario", "Registro2Activity");
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/registrar_usuario.php?login=" + prefUtil.getStringValue("dni") + "&password="
                        + prefUtil.getStringValue("clave") + "&idperfil=2&idpersona=" + prefUtil.getStringValue("idPersona") + "&estado=T");
                Log.i("registrarUsuario", result);
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.length() > 0) {
                            boolean correcto = jsonObject.getBoolean("correcto");
                            if (correcto) {
                                registrarVehiculo();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    public void registrarVehiculo() {
        Log.i("registrarVehiculo", "Registro2Activity");
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/registrar_vehiculo.php?marca=" + etMarca.getText().toString() + "&modelo=" + etModelo.getText().toString()
                        + "&placa=" + etPlaca.getText().toString() + "&color=" + etColor.getText().toString() + "&anio=" + etAnio.getText().toString() + "&idconductor="
                        + prefUtil.getStringValue("idConductor"));
                Log.i("registrarVehiculo", result);
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.length() > 0) {
                            boolean correcto = jsonObject.getBoolean("correcto");
                            if (correcto) {
                                subirFoto();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    public void subirFoto() {
        Log.i("subirFoto", "Registro2Activity");
        Map<String, String> params = new HashMap<String, String>();
        params.put("idconductor", prefUtil.getStringValue("idConductor"));
        params.put("foto", foto);
        agregarLlamada(params);
    }
}

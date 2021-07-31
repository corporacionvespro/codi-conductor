package codi.drive.conductor.chiclayo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;
import org.json.JSONArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import codi.drive.conductor.chiclayo.compartido.PrefUtil;
import static codi.drive.conductor.chiclayo.compartido.Funciones.primero;

/**
 * By: el-bryant
 */

public class Acceso2Activity extends AppCompatActivity {
    @BindView(R.id.circle_loading_view) AnimatedCircleLoadingView animatedCircleLoadingView;
    @BindView(R.id.etClave) EditText etClave;
    @BindView(R.id.etDni) EditText etDni;
    @BindView(R.id.flayCargando) FrameLayout flayCargando;
    PrefUtil prefUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceso2);
        ButterKnife.bind(this);
        prefUtil = new PrefUtil(this);
    }

    @OnClick(R.id.btnIngresar) void ingresar() {
        InputMethodManager immDni = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        immDni.hideSoftInputFromWindow(etDni.getWindowToken(), 0);
        InputMethodManager immClave = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        immClave.hideSoftInputFromWindow(etClave.getWindowToken(), 0);
        flayCargando.setVisibility(View.VISIBLE);
        etDni.setEnabled(false);
        etClave.setEnabled(false);
        if (animatedCircleLoadingView.getParent() != null) {
            ((ViewGroup) animatedCircleLoadingView.getParent()).removeView(animatedCircleLoadingView);
            flayCargando.addView(animatedCircleLoadingView);
            animatedCircleLoadingView.startDeterminate();
        }
        final String login = etDni.getText().toString();
        final String pass = etClave.getText().toString();
        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
                new Thread() {
                    @Override
                    public void run() {
                        final String result = primero("http://codidrive.com/vespro/logica/acceder_conductor.php?login=" + login + "&pass=" + pass);
                        Log.i("ingresar", result);
                        runOnUiThread(() -> {
                            try {
                                JSONArray jsonArray = new JSONArray(result);
                                if (jsonArray.length() > 0) {
                                    String nombres = jsonArray.getJSONObject(0).getString("nombres");
                                    String apellidos = jsonArray.getJSONObject(0).getString("apellidos");
                                    String email = jsonArray.getJSONObject(0).getString("email");
                                    String telefono = jsonArray.getJSONObject(0).getString("telefono");
                                    String foto = jsonArray.getJSONObject(0).getString("foto");
                                    String idUsuario = jsonArray.getJSONObject(0).getString("idusuario");
                                    String idPersona = jsonArray.getJSONObject(0).getString("idpersona");
                                    String idConductor = jsonArray.getJSONObject(0).getString("idconductor");
                                    String saldo = jsonArray.getJSONObject(0).getString("saldo");
                                    String marca = jsonArray.getJSONObject(0).getString("marca");
                                    String modelo = jsonArray.getJSONObject(0).getString("modelo");
                                    String color = jsonArray.getJSONObject(0).getString("color");
                                    String anio = jsonArray.getJSONObject(0).getString("anio");
                                    String numUnidad = jsonArray.getJSONObject(0).getString("numunidad");
                                    String placa = jsonArray.getJSONObject(0).getString("placa");
                                    String estado = jsonArray.getJSONObject(0).getString("estado");
                                    String empresa = jsonArray.getJSONObject(0).getString("empresa");
                                    switch (estado) {
                                        case "T":
                                            Toast.makeText(Acceso2Activity.this, "Comunicarse al 997627162 para habilitar su cuenta y empezar a ganar dinero con CODI",
                                                    Toast.LENGTH_SHORT).show();
                                            animatedCircleLoadingView.stopOk();
                                            flayCargando.setVisibility(View.GONE);
                                            etDni.setEnabled(true);
                                            etClave.setEnabled(true);
                                            break;
                                        case "D":
                                            Toast.makeText(Acceso2Activity.this, "Su cuenta ha sido deshabilitada", Toast.LENGTH_SHORT).show();
                                            animatedCircleLoadingView.stopOk();
                                            flayCargando.setVisibility(View.GONE);
                                            etDni.setEnabled(true);
                                            etClave.setEnabled(true);
                                            break;
                                        default:
                                            prefUtil.saveGenericValue("nombres", nombres);
                                            prefUtil.saveGenericValue("apellidos", apellidos);
                                            prefUtil.saveGenericValue("correo", email);
                                            prefUtil.saveGenericValue("telefono", telefono);
                                            prefUtil.saveGenericValue("foto", foto);
                                            prefUtil.saveGenericValue("idUsuario", idUsuario);
                                            prefUtil.saveGenericValue("idPersona", idPersona);
                                            prefUtil.saveGenericValue("idConductor", idConductor);
                                            prefUtil.saveGenericValue("dni", login);
                                            prefUtil.saveGenericValue("saldo", saldo);
                                            prefUtil.saveGenericValue("marca", marca);
                                            prefUtil.saveGenericValue("modelo", modelo);
                                            prefUtil.saveGenericValue("color", color);
                                            prefUtil.saveGenericValue("anio", anio);
                                            prefUtil.saveGenericValue("numUnidad", numUnidad);
                                            prefUtil.saveGenericValue("placa", placa.toUpperCase());
                                            prefUtil.saveGenericValue("empresa", empresa);
                                            prefUtil.saveGenericValue(PrefUtil.LOGIN_STATUS, "1");
                                            animatedCircleLoadingView.stopOk();
                                            flayCargando.setVisibility(View.GONE);
                                            etDni.setEnabled(true);
                                            etClave.setEnabled(true);
                                            Intent intent = new Intent(Acceso2Activity.this, MainActivity.class);
                                            intent.putExtra("login", etDni.getText().toString());
                                            intent.putExtra("pass", etClave.getText().toString());
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                            finish();
                                            break;
                                    }
                                } else {
                                    animatedCircleLoadingView.stopFailure();
                                    flayCargando.setVisibility(View.GONE);
                                    etDni.setEnabled(true);
                                    etClave.setEnabled(true);
                                    Toast.makeText(Acceso2Activity.this, "Las credenciales proporcionadas son incorrectas, por favor intente de nuevo.", Toast.LENGTH_LONG).show();
                                    etDni.setText("");
                                    etClave.setText("");
                                    etDni.requestFocus();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }.start();
            }
        }.start();

    }
}

package codi.drive.conductor.chiclayo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import butterknife.BindView;
import butterknife.ButterKnife;
import codi.drive.conductor.chiclayo.adapters.PendientesAdapter;
import codi.drive.conductor.chiclayo.entity.SolicitudTaxi;
import static codi.drive.conductor.chiclayo.compartido.Funciones.primero;

/**
 * By: el-bryant
 */

public class PedidosActivity extends AppCompatActivity {
    @BindView(R.id.flayUpdates) FrameLayout flayUpdates;
    @BindView(R.id.rvPendientes) RecyclerView rvPendientes;
    ArrayList<SolicitudTaxi> solicitudTaxis;
    CountDownTimer countDownTimer;
    public static PendientesAdapter pendientesAdapter;
    int cantidadPedidos = 0;
    public static boolean active = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);
        ButterKnife.bind(this);
        rvPendientes.setLayoutManager(new LinearLayoutManager(this));
        active = true;
    }

    public void checkUpdates() {
        Log.i("checkUpdates", "SplashActivity");
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/version_conductor.php");
                Log.i("checkUpdates", result);
                runOnUiThread(() -> {
                    try {
                        if (!result.equals("" + getPackageManager().getPackageInfo(getPackageName(), 0).versionCode)) {
                            flayUpdates.setVisibility(View.VISIBLE);
                        } else {
                            flayUpdates.setVisibility(View.GONE);
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    public void cargarPendientes() {
        Log.i("cargarPendientes", "PedidosActivity");
        solicitudTaxis = new ArrayList<>();
        new Thread() {
            @Override
            public void run() {
                Date date = new Date();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                final String result = primero("http://codidrive.com/vespro/logica/obtener_pendientes.php?fecha=" + dateFormat.format(date));
                Log.i("cargarPendientes", result);
                runOnUiThread(() -> {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String idSolicitud = jsonArray.getJSONObject(i).getString("idsolicitud");
                            String estado = jsonArray.getJSONObject(i).getString("estado");
                            Double tarifa = jsonArray.getJSONObject(i).getDouble("tarifa");
                            Double pagoFinal = jsonArray.getJSONObject(i).getDouble("pagofinal");
                            String apellidosPasajero = jsonArray.getJSONObject(i).getString("apellidos");
                            String nombresPasajero = jsonArray.getJSONObject(i).getString("nombres");
                            String celularPasajero = jsonArray.getJSONObject(i).getString("telefono");
                            String foto = jsonArray.getJSONObject(i).getString("foto");
                            Double latitudRecojo = jsonArray.getJSONObject(i).getDouble("latitud_recojo");
                            Double longitudRecojo = jsonArray.getJSONObject(i).getDouble("longitud_recojo");
                            Double latitudDestino = jsonArray.getJSONObject(i).getDouble("latitud_destino");
                            Double longitudDestino = jsonArray.getJSONObject(i).getDouble("longitud_destino");
                            String direccionOrigen = jsonArray.getJSONObject(i).getString("direccion_origen");
                            String direccionDestino = jsonArray.getJSONObject(i).getString("direccion_destino");
                            String referencia = jsonArray.getJSONObject(i).getString("referencia_recojo");
                            String codigo = jsonArray.getJSONObject(i).getString("codigo");
                            String fechaHora = jsonArray.getJSONObject(i).getString("fhsolicitud");
                            solicitudTaxis.add(new SolicitudTaxi(idSolicitud, estado, tarifa, pagoFinal, apellidosPasajero, nombresPasajero, celularPasajero, foto, latitudRecojo, longitudRecojo,
                                    latitudDestino, longitudDestino, direccionOrigen, direccionDestino, codigo, referencia, fechaHora));
                        }
                        if (jsonArray.length() != cantidadPedidos) {
                            cantidadPedidos = jsonArray.length();
                            pendientesAdapter = new PendientesAdapter(PedidosActivity.this, solicitudTaxis);
                            rvPendientes.setAdapter(pendientesAdapter);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarPendientes();
        countDownTimer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
                cargarPendientes();
                this.start();
                if (cantidadPedidos < 1) {
                    this.cancel();
                    finish();
                }
            }
        }.start();
        checkUpdates();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        cargarPendientes();
    }

    @Override
    protected void onDestroy() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        active = false;
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    protected void onStop() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        active = false;
        super.onStop();
    }

    @Override
    protected void onPause() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        active = false;
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        active = false;
        startActivity(new Intent(PedidosActivity.this, MainActivity.class));
        finish();
    }
}

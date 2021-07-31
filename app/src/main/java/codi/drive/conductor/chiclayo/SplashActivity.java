package codi.drive.conductor.chiclayo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import codi.drive.conductor.chiclayo.compartido.PrefUtil;
import static codi.drive.conductor.chiclayo.compartido.Funciones.primero;

/**
 * By : el-bryant
 */

public class SplashActivity extends AppCompatActivity {
    @BindView(R.id.btnAceptarPermisos) Button btnAceptarPermisos;
    @BindView(R.id.flayUpdates) FrameLayout flayUpdates;
    @BindView(R.id.flayPermisos) FrameLayout flayPermisos;
    @BindView(R.id.ivAnimacion1) ImageView ivAnimacion1;
    @BindView(R.id.ivAnimacion2) ImageView ivAnimacion2;
    AlertDialog alert = null;
    CountDownTimer animacion;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationManager locationManager;
    PrefUtil prefUtil;
    public static int i = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        prefUtil = new PrefUtil(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        btnAceptarPermisos.setOnClickListener(v -> {
            flayPermisos.setVisibility(View.GONE);
            if (ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SplashActivity.this, new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.SYSTEM_ALERT_WINDOW,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, 1);
            } else {
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    AlertNoGps();
                } else {
                    checkUpdates();
                }
            }
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(SplashActivity.this);
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    prefUtil.saveGenericValue("latitud", String.valueOf(location.getLatitude()));
                    prefUtil.saveGenericValue("longitud", String.valueOf(location.getLongitude()));
                    Log.i("coordenadas", prefUtil.getStringValue("latitud") + ", " + prefUtil.getStringValue("longitud"));
                }
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (prefUtil.getStringValue(PrefUtil.LOGIN_STATUS).equals("1")) {
            enviarModelo(obtenerNombreDeDispositivo());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    AlertNoGps();
                } else {
                    checkUpdates();
                }
            } else {
                Log.i("Permiso denegado", "ubicación");
            }
        }
    }

    @OnClick(R.id.btnAceptar) void actualizar() {
        Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=codi.drive.conductor.chiclayo");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
        finish();
    }

    public void AlertNoGps() {
        if (alert == null) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("El GPS está desactivado, ¿desea activarlo?")
                    .setCancelable(false)
                    .setPositiveButton("Sí", (dialog, which) -> {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        CountDownTimer countDownTimer = new CountDownTimer(3000, i) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                i = 1000;
                            }
                            @Override
                            public void onFinish() {
                                animacion();
                            }
                        };
                        countDownTimer.start();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.cancel());
            alert = builder.create();
            alert.show();
        }
    }

    public void animacion() {
        ivAnimacion1.setAlpha(1.0f);
        ivAnimacion2.setAlpha(0.2f);
        animacion = new CountDownTimer(5000, 300) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (ivAnimacion1.getAlpha() == 1.0) {
                    ivAnimacion2.setAlpha(0.2f);
                    ivAnimacion2.setVisibility(View.VISIBLE);
                    ivAnimacion2.animate()
                            .alpha(1.0f)
                            .setDuration(450)
                            .setListener(null);
                    ivAnimacion1.setAlpha(1.0f);
                    ivAnimacion1.setVisibility(View.VISIBLE);
                    ivAnimacion1.animate()
                            .alpha(0.2f)
                            .setDuration(450)
                            .setListener(null);
                } else if (ivAnimacion2.getAlpha() == 1.0) {
                    ivAnimacion2.setAlpha(1.0f);
                    ivAnimacion2.setVisibility(View.VISIBLE);
                    ivAnimacion2.animate()
                            .alpha(0.2f)
                            .setDuration(450)
                            .setListener(null);
                    ivAnimacion1.setAlpha(0.2f);
                    ivAnimacion1.setVisibility(View.VISIBLE);
                    ivAnimacion1.animate()
                            .alpha(1.0f)
                            .setDuration(450)
                            .setListener(null);
                }
            }
            @Override
            public void onFinish() {
                animacion.start();
                continuar();
            }
        }.start();
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
                            animacion();
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    public void continuar() {
        Intent intent;
        animacion.cancel();
        if (!prefUtil.getStringValue("idSolicitud").equals("0") && !prefUtil.getStringValue("idSolicitud").equals("")) {
            intent = new Intent(this, SolicitudTaxiActivity.class);
        } else {
            if (prefUtil.getStringValue(PrefUtil.LOGIN_STATUS).equals("1")) {
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                intent = new Intent(this, Acceso1Activity.class);
            }
        }
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    public String obtenerNombreDeDispositivo() {
        String fabricante = Build.MANUFACTURER;
        String modelo = Build.MODEL;
        if (modelo.startsWith(fabricante)) {
            return modelo;
        } else {
            return fabricante + " " + modelo;
        }
    }

    public void enviarModelo(String modelo) {
        Log.i("enviarModelo", "SplashActivity");
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/enviar_modelo.php?id_conductor=" + prefUtil.getStringValue("idConductor") + "&modelo=" + modelo);
                Log.i("enviarModelo", result);
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.length() > 0) {
                            boolean correcto = jsonObject.getBoolean("correcto");
                            if (correcto) {
                                Log.i("enviarModelo", modelo);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }
}
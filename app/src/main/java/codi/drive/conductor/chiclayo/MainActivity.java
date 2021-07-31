package codi.drive.conductor.chiclayo;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sinaseyfi.advancedcardview.AdvancedCardView;
import com.squareup.picasso.Picasso;
import org.json.JSONObject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import codi.drive.conductor.chiclayo.adapters.PendientesAdapter;
import codi.drive.conductor.chiclayo.compartido.BorderedCircleTransform;
import codi.drive.conductor.chiclayo.compartido.LocationUpdatesBroadcastReceiver;
import codi.drive.conductor.chiclayo.compartido.LocationUpdatesIntentService;
import codi.drive.conductor.chiclayo.compartido.PrefUtil;
import codi.drive.conductor.chiclayo.compartido.ServiceLocation;
import codi.drive.conductor.chiclayo.compartido.Utils;
import static codi.drive.conductor.chiclayo.compartido.Funciones.primero;

/**
 * By: el-bryant
 */

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener, NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.acvMenu) AdvancedCardView acvMenu;
    @BindView(R.id.acvMenuNoche) AdvancedCardView acvMenuNoche;
    @BindView(R.id.acvPendientes) AdvancedCardView acvPendientes;
    @BindView(R.id.acvPendientesNoche) AdvancedCardView acvPendientesNoche;
    @BindView(R.id.btnOcupado) Button btnOcupado;
    @BindView(R.id.dlayMenu) DrawerLayout dlayMenu;
    @BindView(R.id.ivAnimacion1) ImageView ivAnimacion1;
    @BindView(R.id.ivAnimacion2) ImageView ivAnimacion2;
    @BindView(R.id.llayCargando) LinearLayout llayCargando;
    @BindView(R.id.nvMenu) NavigationView nvMenu;
    AlertDialog alert = null;
    FusedLocationProviderClient fusedLocationClient;
    GoogleMap mMap;
    ImageView ivFotoUsuario;
    LocationCallback locationCallback;
    LocationManager locationManager;
    LocationRequest locationRequest;
    Marker markerMiUbicacion;
    PrefUtil prefUtil;
    SwitchCompat swNoche;
    TextView tvNombreUsuario, tvSaldo;
    private static final long UPDATE_INTERVAL = 1000; // Every 1 second.
    private static final long FASTEST_UPDATE_INTERVAL = 2000; // Every 2 seconds.
    private static final long MAX_WAIT_TIME = UPDATE_INTERVAL * 30; // Every 30 seconds.
    public static double latitud = 0.0, longitud = 0.0;
    public static CountDownTimer animacion, ubicaciones;
    private MyReceiver mMessageReceiver;

    LocationListener locListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            actualizarUbicacion(location);
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
        @Override
        public void onProviderEnabled(String provider) {
        }
        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, LocationUpdatesBroadcastReceiver.class);
        intent.setAction(LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );
        ButterKnife.bind(this);
        prefUtil = new PrefUtil(this);
        insertarToken(prefUtil.getStringValue("idConductor"));
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ivFotoUsuario = (ImageView) nvMenu.getHeaderView(0).findViewById(R.id.ivFotoUsuario);
        tvNombreUsuario = (TextView) nvMenu.getHeaderView(0).findViewById(R.id.tvNombreUsuario);
        tvSaldo = (TextView) nvMenu.getHeaderView(0).findViewById(R.id.tvSaldo);
        Menu menu = nvMenu.getMenu();
        swNoche = (SwitchCompat) MenuItemCompat.getActionView(menu.findItem(R.id.nav_noche)).findViewById(R.id.switch_id);
        swNoche.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                prefUtil.saveGenericValue("noche", "SI");
                try {
                    if (mMap != null) {
                        boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapa_noche));
                        if (!success) {
                            Log.i("estilo de mapa", "Style parsing failed");
                        }
                        btnOcupado.setBackground(getDrawable(R.drawable.boton_blanco));
                        btnOcupado.setTextColor(Color.rgb(1, 40, 107));
                        acvMenu.setVisibility(View.GONE);
                        acvMenuNoche.setVisibility(View.VISIBLE);
                        acvPendientes.setVisibility(View.GONE);
                        acvPendientesNoche.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                prefUtil.saveGenericValue("noche", "NO");
                try {
                    if (mMap != null) {
                        boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_gris));
                        if (!success) {
                            Log.i("estilo de mapa", "Style parsing failed");
                        }
                        btnOcupado.setBackground(getDrawable(R.drawable.boton_azul));
                        btnOcupado.setTextColor(Color.rgb(255, 255, 255));
                        acvMenu.setVisibility(View.VISIBLE);
                        acvMenuNoche.setVisibility(View.GONE);
                        acvPendientes.setVisibility(View.VISIBLE);
                        acvPendientesNoche.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        String nombre_completo = prefUtil.getStringValue("nombres") + " " + prefUtil.getStringValue("apellidos");
        char[] caracteres = (nombre_completo.toLowerCase()).toCharArray();
        caracteres[0] = Character.toUpperCase(caracteres[0]);
        for (int i = 0; i < (nombre_completo.toLowerCase()).length(); i ++) {
            if (caracteres[i] == ' ') {
                if (i < nombre_completo.length() - 1) {
                    caracteres[i + 1] = Character.toUpperCase(caracteres[i + 1]);
                }
            }
            tvNombreUsuario.setText("" + tvNombreUsuario.getText().toString() + caracteres[i]);
        }
        Picasso.get().load(prefUtil.getStringValue("foto")).transform(new BorderedCircleTransform(15, Color.rgb(0, 214, 209))).into(ivFotoUsuario);
        tvSaldo.setText("S/ " + String.format("%.2f", Double.parseDouble(prefUtil.getStringValue("saldo"))));
        nvMenu.setNavigationItemSelectedListener(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                modalActivarGPS();
            } else {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null) {
                        prefUtil.saveGenericValue("latitud", String.valueOf(location.getLatitude()));
                        prefUtil.saveGenericValue("longitud", String.valueOf(location.getLongitude()));
                    }
                });
            }
        }
        createLocationRequest();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    actualizarUbicacion(location);
                }
            }
        };
        animacion();
        disponible();
        ubicaciones = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
                Intent intent = new Intent(MainActivity.this, LocationUpdatesIntentService.class);
                startService(intent);
                this.start();
            }
        }.start();
        mMessageReceiver = new MyReceiver(new Handler());
        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver), new IntentFilter(PrefUtil.BROADCAST_SOLICITUD_TAXI)
        );
    }

    public class MyReceiver extends BroadcastReceiver {
        private final Handler handler;
        public MyReceiver(Handler handler) {
            this.handler = handler;
        }
        @Override
        public void onReceive(Context context, final Intent intent) {
            handler.post(() -> {
                if (!PedidosActivity.active) {
                    startActivity(new Intent(MainActivity.this, PedidosActivity.class));
                }
            });
        }
    }

    @Override
    public void onCameraIdle() {
        new CountDownTimer(7000, 1000) {
            @Override
            public void onTick(long l) {
            }
            @Override
            public void onFinish() {
                centrarPosicion();
            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        if (dlayMenu.isDrawerOpen(GravityCompat.START)) {
            dlayMenu.closeDrawer(GravityCompat.START);
        } else {
            finishAffinity();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (prefUtil.getStringValue("noche").equals("SI")) {
            swNoche.setChecked(true);
            try {
                if (mMap != null) {
                    boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapa_noche));
                    if (!success) {
                        Log.i("estilo de mapa", "Style parsing failed");
                    }
                    btnOcupado.setBackground(getDrawable(R.drawable.boton_blanco));
                    btnOcupado.setTextColor(Color.rgb(1, 40, 107));
                    acvMenu.setVisibility(View.GONE);
                    acvMenuNoche.setVisibility(View.VISIBLE);
                    acvPendientes.setVisibility(View.GONE);
                    acvPendientesNoche.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            swNoche.setChecked(false);
            try {
                if (mMap != null) {
                    boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_gris));
                    if (!success) {
                        Log.i("estilo de mapa", "Style parsing failed");
                    }
                    btnOcupado.setBackground(getDrawable(R.drawable.boton_azul));
                    btnOcupado.setTextColor(Color.rgb(255, 255, 255));
                    acvMenu.setVisibility(View.VISIBLE);
                    acvMenuNoche.setVisibility(View.GONE);
                    acvPendientes.setVisibility(View.VISIBLE);
                    acvPendientesNoche.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setOnCameraIdleListener(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 111);
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        actualizarUbicacion(location);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locListener);
        if (mMap != null) {
            if (markerMiUbicacion != null) {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(markerMiUbicacion.getPosition())
                        .zoom(16)
                        .bearing(mMap.getCameraPosition().bearing)
                        .tilt(mMap.getCameraPosition().tilt)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_acerca_app:
                startActivity(new Intent(MainActivity.this, AcercaAppActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.nav_cerrar:
                enviarEstadoOcupado();
                prefUtil.clearAll();
                stopService(new Intent(MainActivity.this, ServiceLocation.class));
                stopService(new Intent(MainActivity.this, LocationUpdatesIntentService.class));
                stopService(new Intent(MainActivity.this, LocationUpdatesBroadcastReceiver.class));
                MainActivity.ubicaciones.cancel();
                Utils.setRequestingLocationUpdates(this, false);
                startActivity(new Intent(MainActivity.this, Acceso1Activity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.nav_compartir:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                String msj = "Te invito a descargar la App CODI CONDUCTOR y pertenecer a la red más grande de conductores https://play.google.com/store/apps/details?id=codi.drive.conductor.chiclayo";
                shareIntent.putExtra(Intent.EXTRA_TEXT, msj);
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, "Compartir en"));
                break;
            case R.id.nav_historial:
                startActivity(new Intent(MainActivity.this, HistorialActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.nav_inicio:
                dlayMenu.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_lugares:
                startActivity(new Intent(MainActivity.this, CategoriaActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.nav_noche:
                swNoche.setChecked(!swNoche.isChecked());
                break;
            case R.id.nav_ocupado:
                ocupado();
                break;
            case R.id.nav_perfil:
                startActivity(new Intent(MainActivity.this, PerfilActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.nav_promociones:
                startActivity(new Intent(MainActivity.this, PromocionesActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
        }
        dlayMenu.closeDrawer(GravityCompat.START);
        return true;
    }

    @OnClick({R.id.acvMenu, R.id.acvMenuNoche}) void abrirMenu() {
        dlayMenu.openDrawer(GravityCompat.START);
    }

    @OnClick({R.id.acvPendientes, R.id.acvPendientesNoche}) void abrirPendientes() {
        Intent intent = new Intent(MainActivity.this, PedidosActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btnOcupado) void ocupado() {
        prefUtil.saveGenericValue("disponible", "NO");
        startActivity(new Intent(MainActivity.this, OcupadoActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    public void actualizarUbicacion(Location location) {
        if (location != null) {
            latitud = location.getLatitude();
            longitud = location.getLongitude();
            prefUtil.saveGenericValue("latitud", "" + latitud);
            prefUtil.saveGenericValue("longitud", "" + longitud);
            if (markerMiUbicacion != null) {
                markerMiUbicacion.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
                llayCargando.setVisibility(View.GONE);
                animacion.cancel();
            } else {
                MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.mipmap.carro));
                markerMiUbicacion = mMap.addMarker(markerOptions);
            }
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
            }
        }.start();
    }

    public void centrarPosicion() {
        if (mMap != null) {
            if (markerMiUbicacion != null) {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(markerMiUbicacion.getPosition())
                        .zoom(mMap.getCameraPosition().zoom)
                        .bearing(mMap.getCameraPosition().bearing)
                        .tilt(mMap.getCameraPosition().tilt)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
    }

    public void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setMaxWaitTime(MAX_WAIT_TIME);
    }

    public void disponible() {
        Intent intent = new Intent(MainActivity.this, ServiceLocation.class);
        prefUtil.saveGenericValue("disponible", "SI");
        requestLocationUpdates();
        startLocationUpdates();
        startService(intent);
        enviarEstado("D");
    }

    public void enviarEstado(String estado) {
        Log.i("enviarEstado", "MainActivity");
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/cambiar_estado.php?estado=" + estado + "&idconductor=" + prefUtil.getStringValue("idConductor"));
                Log.i("enviarEstado", result);
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.length() > 0) {
                            boolean correcto = jsonObject.getBoolean("correcto");
                            if (correcto) {
                                Log.i("enviarEstado", "ok");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    public void enviarEstadoOcupado() {
        Log.i("enviarEstadoOcupado", "OcupadoActivity");
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/enviar_estado_ocupado.php?idConductor=" + prefUtil.getStringValue("idConductor"));
                Log.i("enviarEstadoOcupado", result);
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.length() > 0) {
                            boolean correcto = jsonObject.getBoolean("correcto");
                            if (correcto) {
                                Log.i("enviarEstadoOcupado", "OcupadoActivity");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    public void insertarToken(String idconductor) {
        Log.i("insertarToken", "MainActivity");
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.i("insertarToken", "" + task.getException());
            }
            String token = task.getResult().getToken();
            new Thread() {
                @Override
                public void run() {
                    final String result = primero("http://codidrive.com/vespro/logica/insertar_token.php?idconductor=" + idconductor + "&token=" + token);
                    Log.i("insertarToken", result);
                    runOnUiThread(() -> {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            if (jsonObject.length() > 0) {
                                boolean correcto = jsonObject.getBoolean("correcto");
                                if (correcto) {
                                    Log.i("insertarToken", "todo OK");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }.start();
        });
    }

    public void modalActivarGPS() {
        if (alert == null) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("El GPS está desactivado, ¿desea activarlo?")
                    .setCancelable(false)
                    .setPositiveButton("Sí", (dialog, which) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                    .setNegativeButton("No", (dialog, which) -> dialog.cancel());
            alert = builder.create();
            alert.show();
        }
    }

    public void requestLocationUpdates() {
        try {
            Log.i("MainActivity", "Starting location updates");
            Utils.setRequestingLocationUpdates(this, true);
            fusedLocationClient.requestLocationUpdates(locationRequest, getPendingIntent());
        } catch (SecurityException e) {
            Utils.setRequestingLocationUpdates(this, false);
            e.printStackTrace();
        }
    }

    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }
}

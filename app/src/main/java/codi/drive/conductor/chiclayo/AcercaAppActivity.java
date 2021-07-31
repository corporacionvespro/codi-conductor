package codi.drive.conductor.chiclayo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.navigation.NavigationView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import codi.drive.conductor.chiclayo.adapters.AcercaAppAdapter;
import codi.drive.conductor.chiclayo.compartido.LocationUpdatesBroadcastReceiver;
import codi.drive.conductor.chiclayo.compartido.LocationUpdatesIntentService;
import codi.drive.conductor.chiclayo.compartido.PrefUtil;
import codi.drive.conductor.chiclayo.compartido.ServiceLocation;
import codi.drive.conductor.chiclayo.compartido.Utils;
import codi.drive.conductor.chiclayo.entity.AcercaApp;
import static codi.drive.conductor.chiclayo.compartido.Funciones.primero;

/**
 * By: el-bryant
 */

public class AcercaAppActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.dlayMenu) DrawerLayout dlayMenu;
    @BindView(R.id.nvMenu) NavigationView nvMenu;
    @BindView(R.id.rvAcercaApp) RecyclerView rvAcercaApp;
    AcercaAppAdapter acercaAppAdapter;
    ArrayList<AcercaApp> acercaApps;
    PrefUtil prefUtil;
    SwitchCompat swNoche;
    public static FrameLayout flayAcercaApp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acerca_app);
        ButterKnife.bind(this);
        prefUtil = new PrefUtil(this);
        flayAcercaApp = (FrameLayout) findViewById(R.id.flayAcercaApp);
        rvAcercaApp.setLayoutManager(new LinearLayoutManager(this));
        cargarAcercaApp();
        nvMenu.setNavigationItemSelectedListener(this);
        Menu menu = nvMenu.getMenu();
        swNoche = (SwitchCompat) MenuItemCompat.getActionView(menu.findItem(R.id.nav_noche)).findViewById(R.id.switch_id);
        swNoche.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                prefUtil.saveGenericValue("noche", "SI");
            } else {
                prefUtil.saveGenericValue("noche", "NO");
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_acerca_app:
                dlayMenu.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_cerrar:
                enviarEstadoOcupado();
                prefUtil.clearAll();
                stopService(new Intent(AcercaAppActivity.this, ServiceLocation.class));
                stopService(new Intent(AcercaAppActivity.this, LocationUpdatesIntentService.class));
                stopService(new Intent(AcercaAppActivity.this, LocationUpdatesBroadcastReceiver.class));
                MainActivity.ubicaciones.cancel();
                Utils.setRequestingLocationUpdates(this, false);
                startActivity(new Intent(AcercaAppActivity.this, Acceso1Activity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.nav_compartir:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                String msj = "Te invito a descargar la App CODI CONDUCTOR y pertenercer a la red más grande de conductores https://play.google.com/store/apps/details?id=codi.drive.conductor.chiclayo";
                shareIntent.putExtra(Intent.EXTRA_TEXT, msj);
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, "Compartir en"));
                break;
            case R.id.nav_historial:
                startActivity(new Intent(AcercaAppActivity.this, HistorialActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.nav_inicio:
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.nav_lugares:
                startActivity(new Intent(AcercaAppActivity.this, CategoriaActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.nav_noche:
                swNoche.setChecked(!swNoche.isChecked());
                break;
            case R.id.nav_ocupado:
                prefUtil.saveGenericValue("disponible", "NO");
                startActivity(new Intent(AcercaAppActivity.this, OcupadoActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.nav_perfil:
                startActivity(new Intent(AcercaAppActivity.this, PerfilActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.nav_promociones:
                startActivity(new Intent(AcercaAppActivity.this, PromocionesActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
        }
        dlayMenu.closeDrawer(GravityCompat.START);
        return true;
    }

    @OnClick(R.id.acvMenu) void abrirMenu() {
        dlayMenu.openDrawer(GravityCompat.START);
    }

    public void cargarAcercaApp() {
        Log.i("cargarAcercaApp", "AcercaAppActivity");
        acercaApps = new ArrayList<>();
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/obtener_acerca_app.php");
                Log.i("cargarAcercaApp", result);
                runOnUiThread(() -> {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String idAcercaApp = jsonArray.getJSONObject(i).getString("id_acerca_app");
                            String nombre = jsonArray.getJSONObject(i).getString("nombre");
                            String contenido = jsonArray.getJSONObject(i).getString("contenido");
                            String PC = jsonArray.getJSONObject(i).getString("p_c");
                            if (PC.equals("C")) {
                                acercaApps.add(new AcercaApp(idAcercaApp, nombre, contenido, PC));
                            }
                        }
                        acercaAppAdapter = new AcercaAppAdapter(AcercaAppActivity.this, acercaApps);
                        rvAcercaApp.setAdapter(acercaAppAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    public void enviarEstadoOcupado() {
        Log.i("enviarEstadoOcupado", "AcercaAppActivity");
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
                                Log.i("enviarEstadoOcupado", "todo OK");
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

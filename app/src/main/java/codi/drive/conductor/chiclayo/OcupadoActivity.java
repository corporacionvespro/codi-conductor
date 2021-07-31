package codi.drive.conductor.chiclayo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;
import org.json.JSONObject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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

public class OcupadoActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.dlayMenu) DrawerLayout dlayMenu;
    @BindView(R.id.nvMenu) NavigationView nvMenu;
    ImageView ivFotoUsuario;
    PrefUtil prefUtil;
    SwitchCompat swNoche;
    TextView tvNombreUsuario, tvSaldo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocupado);
        ButterKnife.bind(this);
        prefUtil = new PrefUtil(this);
        ivFotoUsuario = (ImageView) nvMenu.getHeaderView(0).findViewById(R.id.ivFotoUsuario);
        tvNombreUsuario = (TextView) nvMenu.getHeaderView(0).findViewById(R.id.tvNombreUsuario);
        tvSaldo = (TextView) nvMenu.getHeaderView(0).findViewById(R.id.tvSaldo);
        Picasso.get().load(prefUtil.getStringValue("foto")).transform(new BorderedCircleTransform(5, Color.rgb(0, 214, 209))).into(ivFotoUsuario);
        char[] caracteres = (prefUtil.getStringValue("nombres").toLowerCase() + " " + prefUtil.getStringValue("apellidos").toLowerCase()).toCharArray();
        caracteres[0] = Character.toUpperCase(caracteres[0]);
        if ((prefUtil.getStringValue("nombres") + " " + prefUtil.getStringValue("apellidos")).length() > 2) {
            for (int i = 0; i < (prefUtil.getStringValue("nombres").toLowerCase() + " " + prefUtil.getStringValue("apellidos").toLowerCase()).length(); i ++) {
                if (caracteres[i] == ' ') {
                    caracteres[i + 1] = Character.toUpperCase(caracteres[i + 1]);
                }
                tvNombreUsuario.setText("" + tvNombreUsuario.getText() + caracteres[i]);
            }
        }
        tvSaldo.setText("S/ " + prefUtil.getStringValue("saldo").substring(0, prefUtil.getStringValue("saldo").indexOf(".") + 2));
        nvMenu.setNavigationItemSelectedListener(this);
        stopService(new Intent(OcupadoActivity.this, ServiceLocation.class));
        stopService(new Intent(OcupadoActivity.this, LocationUpdatesIntentService.class));
        stopService(new Intent(OcupadoActivity.this, LocationUpdatesBroadcastReceiver.class));
        MainActivity.ubicaciones.cancel();
        Utils.setRequestingLocationUpdates(this, false);
        enviarEstadoOcupado();
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_acerca_app:
                startActivity(new Intent(OcupadoActivity.this, AcercaAppActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.nav_cerrar:
                prefUtil.clearAll();
                startActivity(new Intent(OcupadoActivity.this, Acceso1Activity.class));
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
                startActivity(new Intent(OcupadoActivity.this, HistorialActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.nav_inicio:
                startActivity(new Intent(OcupadoActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.nav_lugares:
                startActivity(new Intent(OcupadoActivity.this, RecomendadosActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.nav_noche:
                swNoche.setChecked(!swNoche.isChecked());
                break;
            case R.id.nav_ocupado:
                dlayMenu.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_perfil:
                startActivity(new Intent(OcupadoActivity.this, PerfilActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.nav_promociones:
                startActivity(new Intent(OcupadoActivity.this, PromocionesActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
        }
        dlayMenu.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (dlayMenu.isDrawerOpen(GravityCompat.START)) {
            dlayMenu.closeDrawer(GravityCompat.START);
        } else {
            finishAffinity();
        }
    }

    @OnClick(R.id.acvMenu) void abrirMenu() {
        dlayMenu.openDrawer(GravityCompat.START);
    }

    @OnClick(R.id.btnDisponible) void disponible() {
        startActivity(new Intent(OcupadoActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        MainActivity.ubicaciones.start();
        finish();
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
}

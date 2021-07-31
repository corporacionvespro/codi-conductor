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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import codi.drive.conductor.chiclayo.adapters.CategoriaAdapter;
import codi.drive.conductor.chiclayo.compartido.BorderedCircleTransform;
import codi.drive.conductor.chiclayo.compartido.LocationUpdatesBroadcastReceiver;
import codi.drive.conductor.chiclayo.compartido.LocationUpdatesIntentService;
import codi.drive.conductor.chiclayo.compartido.PrefUtil;
import codi.drive.conductor.chiclayo.compartido.ServiceLocation;
import codi.drive.conductor.chiclayo.entity.Categoria;
import static codi.drive.conductor.chiclayo.compartido.Funciones.primero;

/**
 * By: el-bryant
 */

public class CategoriaActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.dlayMenu) DrawerLayout dlayMenu;
    @BindView(R.id.nvMenu) NavigationView nvMenu;
    @BindView(R.id.rvCategoria) RecyclerView rvCategorias;
    ArrayList<Categoria> categorias;
    CategoriaAdapter categoriaAdapter;
    ImageView ivFotoUsuario;
    PrefUtil prefUtil;
    SwitchCompat swNoche;
    TextView tvNombreUsuario, tvSaldo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria);
        ButterKnife.bind(this);
        prefUtil = new PrefUtil(this);
        rvCategorias.setLayoutManager(new LinearLayoutManager(this));
        cargarCategorias();
        nvMenu.setNavigationItemSelectedListener(this);
        ivFotoUsuario = (ImageView) nvMenu.getHeaderView(0).findViewById(R.id.ivFotoUsuario);
        tvNombreUsuario = (TextView) nvMenu.getHeaderView(0).findViewById(R.id.tvNombreUsuario);
        tvSaldo = (TextView) nvMenu.getHeaderView(0).findViewById(R.id.tvSaldo);
        Picasso.get().load(prefUtil.getStringValue("foto")).transform(new BorderedCircleTransform(5, Color.rgb(0, 214, 209))).into(ivFotoUsuario);
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
        tvSaldo.setText("S/ " + prefUtil.getStringValue("saldo").substring(0, prefUtil.getStringValue("saldo").indexOf(".") + 2));
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

    @OnClick(R.id.acvMenu) void abrirMenu() {
        dlayMenu.openDrawer(GravityCompat.START);
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
                startActivity(new Intent(CategoriaActivity.this, AcercaAppActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.nav_cerrar:
                enviarEstadoOcupado();
                stopService(new Intent(CategoriaActivity.this, ServiceLocation.class));
                stopService(new Intent(CategoriaActivity.this, LocationUpdatesBroadcastReceiver.class));
                stopService(new Intent(CategoriaActivity.this, LocationUpdatesIntentService.class));
                prefUtil.saveGenericValue("disponible", "NO");
                prefUtil.clearAll();
                startActivity(new Intent(CategoriaActivity.this, Acceso1Activity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.nav_compartir:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                String msj = "Te invito a descargar la App CODI CONDUCTOR y pertenecer a la red más grande de conductores" +
                        " https://play.google.com/store/apps/details?id=codi.drive.conductor.chiclayo ";
                intent.putExtra(Intent.EXTRA_TEXT, msj);
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, "Compartir en"));
                break;
            case R.id.nav_historial:
                startActivity(new Intent(CategoriaActivity.this, HistorialActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.nav_inicio:
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.nav_lugares:
                dlayMenu.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_noche:
                swNoche.setChecked(!swNoche.isChecked());
                break;
            case R.id.nav_ocupado:
                prefUtil.saveGenericValue("disponible", "NO");
                startActivity(new Intent(CategoriaActivity.this, OcupadoActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.nav_perfil:
                startActivity(new Intent(CategoriaActivity.this, PerfilActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.nav_promociones:
                startActivity(new Intent(CategoriaActivity.this, PromocionesActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
        }
        dlayMenu.closeDrawer(GravityCompat.START);
        return true;
    }

    public void cargarCategorias() {
        Log.i("cargarCategorias", "CategoriaActivity");
        categorias = new ArrayList<>();
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/ws/obtener_categorias.php");
                Log.i("cargarCategorias", result);
                runOnUiThread(() -> {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Categoria categoria = new Categoria();
                            String idCategoria = jsonArray.getJSONObject(i).getString("idcategoria");
                            String nombre = jsonArray.getJSONObject(i).getString("nombre");
                            String imagen = jsonArray.getJSONObject(i).getString("foto");
                            categoria.setIdCategoria(idCategoria);
                            categoria.setImagen(imagen);
                            categoria.setNombre(nombre);
                            categorias.add(categoria);
                        }
                        categoriaAdapter = new CategoriaAdapter(CategoriaActivity.this, categorias);
                        rvCategorias.setAdapter(categoriaAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    public void enviarEstadoOcupado() {
        Log.i("enviarEstadoOcupado", "CategoriaActivity");
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

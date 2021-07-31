package codi.drive.conductor.chiclayo;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import codi.drive.conductor.chiclayo.adapters.RecomendadosAdapter;
import codi.drive.conductor.chiclayo.entity.Recomendado;
import static codi.drive.conductor.chiclayo.compartido.Funciones.primero;

/**
 * By: el-bryant
 */

public class RecomendadosActivity extends AppCompatActivity {
    @BindView(R.id.ivCategoria) ImageView ivCategoria;
    @BindView(R.id.rvRecomendados) RecyclerView rvRecomendados;
    @BindView(R.id.tvCategoria) TextView tvCategoria;
    ArrayList<Recomendado> recomendados;
    RecomendadosAdapter recomendadosAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recomendados);
        ButterKnife.bind(this);
        rvRecomendados.setLayoutManager(new LinearLayoutManager(this));
        if (getIntent().getExtras() != null) {
            tvCategoria.setText(getIntent().getStringExtra("nombre"));
            Picasso.get().load(getIntent().getStringExtra("imagen")).into(ivCategoria);
            cargarRecomendados(getIntent().getStringExtra("idCategoria"));
        }
    }

    public void cargarRecomendados(String idCategoria) {
        Log.i("cargarRecomendados", "RecomendadosActivity");
        recomendados = new ArrayList<>();
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/ws/obtener_lugares_recomendados.php?idcategoria=" + idCategoria);
                Log.i("cargarRecomendados", result);
                runOnUiThread(() -> {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Recomendado recomendado = new Recomendado();
                            String idRecomendado = jsonArray.getJSONObject(i).getString("idlugar");
                            String nombre = jsonArray.getJSONObject(i).getString("nombre");
                            Double latitud = jsonArray.getJSONObject(i).getDouble("latitud");
                            Double longitud = jsonArray.getJSONObject(i).getDouble("longitud");
                            String foto = jsonArray.getJSONObject(i).getString("foto");
                            String direccion = jsonArray.getJSONObject(i).getString("direccion");
                            recomendado.setIdRecomendado(idRecomendado);
                            recomendado.setNombre(nombre);
                            recomendado.setLatitud(latitud);
                            recomendado.setLongitud(longitud);
                            recomendado.setFoto(foto);
                            recomendado.setDireccion(direccion);
                            recomendados.add(recomendado);
                        }
                        recomendadosAdapter = new RecomendadosAdapter(RecomendadosActivity.this, recomendados);
                        rvRecomendados.setAdapter(recomendadosAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }
}

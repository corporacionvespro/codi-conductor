package codi.drive.conductor.chiclayo;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;
import butterknife.BindView;
import butterknife.ButterKnife;
import codi.drive.conductor.chiclayo.compartido.BorderedCircleTransform;
import codi.drive.conductor.chiclayo.compartido.PrefUtil;

/**
 * By: el-bryant
 */

public class PromocionDetalleActivity extends AppCompatActivity {
    @BindView(R.id.ivLogo) ImageView ivLogo;
    @BindView(R.id.tvDescripcion) TextView tvDescripcion;
    @BindView(R.id.tvNombreEmpresa) TextView tvNombreEmpresa;
    @BindView(R.id.tvPlaca) TextView tvPlaca;
    PrefUtil prefUtil;
    String apellidos = "", nombres = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promocion_detalle);
        ButterKnife.bind(this);
        prefUtil = new PrefUtil(this);
        if (getIntent().getExtras() != null) {
            String logo = getIntent().getExtras().getString("logo");
            String nombreEmpresa = getIntent().getExtras().getString("nombre");
            String descripcion = getIntent().getExtras().getString("descripcion");
            Picasso.get().load(prefUtil.getStringValue("foto")).transform(new BorderedCircleTransform(15, Color.rgb(1, 40, 107))).into(ivLogo);
            char[] caracteresa = (prefUtil.getStringValue("apellidos").toLowerCase()).toCharArray();
            caracteresa[0] = Character.toUpperCase(caracteresa[0]);
            if (prefUtil.getStringValue("apellidos").length() > 2) {
                for (int i = 0; i < (prefUtil.getStringValue("apellidos").toLowerCase()).length(); i ++) {
                    if (caracteresa[i] == ' ') {
                        caracteresa[i + 1] = Character.toUpperCase(caracteresa[i + 1]);
                    }
                    apellidos = apellidos + caracteresa[i];
                }
            }
            char[] caracteresn = (prefUtil.getStringValue("nombres").toLowerCase()).toCharArray();
            caracteresn[0] = Character.toUpperCase(caracteresn[0]);
            if (prefUtil.getStringValue("nombres").length() > 2) {
                for (int i = 0; i < (prefUtil.getStringValue("nombres").toLowerCase()).length(); i ++) {
                    if (caracteresn[i] == ' ') {
                        caracteresn[i + 1] = Character.toUpperCase(caracteresn[i + 1]);
                    }
                    nombres = nombres + caracteresn[i];
                }
            }
            tvNombreEmpresa.setText(nombres + "\n" + apellidos);
            tvDescripcion.setText(descripcion);
            tvPlaca.setText("Placa: " + prefUtil.getStringValue("placa").toUpperCase());
        }
    }
}

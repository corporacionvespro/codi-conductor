package codi.drive.conductor.chiclayo;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;
import butterknife.BindView;
import butterknife.ButterKnife;
import codi.drive.conductor.chiclayo.compartido.BorderedCircleTransform;

/**
 * By: el-bryant
 */

public class HistorialDetalleActivity extends AppCompatActivity {
    @BindView(R.id.ivFotoPasajero) ImageView ivFotoPasajero;
    @BindView(R.id.rbValoracion) RatingBar rbValoracion;
    @BindView(R.id.tvApellidosPasajero) TextView tvApellidosPasajero;
    @BindView(R.id.tvDireccionDestino) TextView tvDireccionDestino;
    @BindView(R.id.tvDireccionOrigen) TextView tvDireccionOrigen;
    @BindView(R.id.tvFechaHora) TextView tvFechaHora;
    @BindView(R.id.tvNombresPasajero) TextView tvNombresPasajero;
    @BindView(R.id.tvPagoFinal) TextView tvPagoFinal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_detalle);
        ButterKnife.bind(this);
        if (getIntent().getExtras() != null) {
            Picasso.get().load(getIntent().getStringExtra("foto")).transform(new BorderedCircleTransform(5, Color.rgb(0, 214, 209))).into(ivFotoPasajero);
            rbValoracion.setRating(getIntent().getFloatExtra("valoracion", 0));
            String apellidos = getIntent().getStringExtra("apellidos");
            char[] caracteres_apellidos = (apellidos.toLowerCase()).toCharArray();
            caracteres_apellidos[0] = Character.toUpperCase(caracteres_apellidos[0]);
            for (int i = 0; i < (apellidos.toLowerCase()).length(); i ++) {
                if (caracteres_apellidos[i] == ' ') {
                    if (i < apellidos.length() - 1) {
                        caracteres_apellidos[i + 1] = Character.toUpperCase(caracteres_apellidos[i + 1]);
                    }
                }
                tvApellidosPasajero.setText("" + tvApellidosPasajero.getText().toString() + caracteres_apellidos[i]);
            }
            String nombres = getIntent().getStringExtra("nombres");
            char[] caracteres_nombres = (nombres.toLowerCase()).toCharArray();
            caracteres_nombres[0] = Character.toUpperCase(caracteres_nombres[0]);
            for (int i = 0; i < (nombres.toLowerCase()).length(); i ++) {
                if (caracteres_nombres[i] == ' ') {
                    if (i < nombres.length() - 1) {
                        caracteres_nombres[i + 1] = Character.toUpperCase(caracteres_nombres[i + 1]);
                    }
                }
                tvNombresPasajero.setText("" + tvNombresPasajero.getText().toString() + caracteres_nombres[i]);
            }
            tvDireccionDestino.setText(getIntent().getStringExtra("direccionDestino"));
            tvDireccionOrigen.setText(getIntent().getStringExtra("direccionOrigen"));
            tvFechaHora.setText(getIntent().getStringExtra("fecha") + " " + getIntent().getStringExtra("hora"));
            tvPagoFinal.setText("S/ " + String.format("%.2f", getIntent().getDoubleExtra("pagoFinal", 0)));
        }
    }
}

package codi.drive.conductor.chiclayo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * By: el-bryant
 */

public class ContenidoActivity extends AppCompatActivity {
    @BindView(R.id.tvContenido) TextView tvContenido;
    @BindView(R.id.tvTitulo) TextView tvTitulo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contenido);
        ButterKnife.bind(this);
        if (getIntent().getExtras() != null) {
            String titulo = getIntent().getStringExtra("nombre");
            String contenido = getIntent().getStringExtra("contenido");
            tvTitulo.setText(titulo);
            tvContenido.setText(contenido);
        }
    }

    @OnClick(R.id.btnAceptar) void aceptar() {
        AcercaAppActivity.flayAcercaApp.setVisibility(View.GONE);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        AcercaAppActivity.flayAcercaApp.setVisibility(View.GONE);
    }
}

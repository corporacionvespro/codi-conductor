package codi.drive.conductor.chiclayo;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * By: el-bryant
 */

public class Acceso1Activity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceso1);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnNuevoUsuario) void nuevoUsuario() {
        startActivity(new Intent(Acceso1Activity.this, Registro1Activity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @OnClick(R.id.btnTengoCuenta) void tengoCuenta() {
        startActivity(new Intent(Acceso1Activity.this, Acceso2Activity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}

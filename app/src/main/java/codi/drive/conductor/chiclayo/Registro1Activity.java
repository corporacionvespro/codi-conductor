package codi.drive.conductor.chiclayo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import codi.drive.conductor.chiclayo.compartido.CircleTransform;
import codi.drive.conductor.chiclayo.compartido.PrefUtil;
import static codi.drive.conductor.chiclayo.compartido.Funciones.primero;

/**
 * By: el-bryant
 */

public class Registro1Activity extends AppCompatActivity {
    public static Button btnVerificar;
    public static Button btnSiguiente;
    @BindView(R.id.etApellidos) EditText etApellidos;
    @BindView(R.id.etCelular) EditText etCelular;
    @BindView(R.id.etClave) EditText etClave;
    @BindView(R.id.etCorreo) EditText etCorreo;
    @BindView(R.id.etDni) EditText etDni;
    @BindView(R.id.etNombres) EditText etNombres;
    @BindView(R.id.ivFoto) ImageView ivFoto;
    PrefUtil prefUtil;
    String contenidoImagen = "";
    public static String celular;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro1);
        ButterKnife.bind(this);
        btnVerificar = (Button) findViewById(R.id.btnVerificar);
        btnSiguiente = (Button) findViewById(R.id.btnSiguientePaso);
        btnVerificar.setOnClickListener(v -> validar());
        btnSiguiente.setOnClickListener(v -> siguiente());
        prefUtil = new PrefUtil(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5675 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                contenidoImagen = getStringImage(bitmap);
                Picasso.get().load(getImageUri(this, bitmap)).transform(new CircleTransform()).into(ivFoto);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void validar() {
        celular = etCelular.getText().toString();
        startActivity(new Intent(Registro1Activity.this, CodigoCelularActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void siguiente() {
        Intent intent = new Intent(Registro1Activity.this, Registro2Activity.class);
        intent.putExtra("apellidos", etApellidos.getText().toString());
        intent.putExtra("celular", etCelular.getText().toString());
        intent.putExtra("clave", etClave.getText().toString());
        intent.putExtra("correo", etCorreo.getText().toString());
        intent.putExtra("dni", etDni.getText().toString());
        intent.putExtra("nombres", etNombres.getText().toString());
        intent.putExtra("foto", contenidoImagen);
        prefUtil.saveGenericValue("apellidos", etApellidos.getText().toString());
        prefUtil.saveGenericValue("celular", etCelular.getText().toString());
        prefUtil.saveGenericValue("clave", etClave.getText().toString());
        prefUtil.saveGenericValue("correo", etCorreo.getText().toString());
        prefUtil.saveGenericValue("dni", etDni.getText().toString());
        prefUtil.saveGenericValue("nombres", etNombres.getText().toString());
        prefUtil.saveGenericValue("foto", contenidoImagen);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @OnClick({R.id.ivFoto, R.id.tvSubirFoto}) void EditarFotoUsuario() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccione Imagen"), 5675);
    }

    public void comprobarDatos() {
        Log.i("comprobarDatos", "click");
        new Thread() {
            @Override
            public void run() {
                final String result = primero("https://.../consulta_dni.php?dni=" + etDni.getText().toString());
                Log.i("comprobarDatos", result);
                runOnUiThread(() -> {
                    if (!result.equals(" No se encontró el DNI o falló conexion con JNE")) {
                        try {
                            JSONObject jsonArray = new JSONObject(result);
                            String nombres = jsonArray.getString("nombres");
                            String apellido_paterno = jsonArray.getString("apellido_paterno");
                            String apellido_materno = jsonArray.getString("apellido_materno");
                            if (!nombres.equals("") && !apellido_paterno.equals("") && !apellido_materno.equals("")) {
                                etNombres.setText(nombres);
                                etApellidos.setText(apellido_paterno + " " + apellido_materno);
                                etNombres.setEnabled(false);
                                etApellidos.setEnabled(false);
                            } else {
                                etNombres.setEnabled(true);
                                etApellidos.setEnabled(true);
                                Toast.makeText(Registro1Activity.this, "No se pudo encontrar los datos, por favor registre manualmente", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        etNombres.setEnabled(true);
                        etApellidos.setEnabled(true);
                        Toast.makeText(Registro1Activity.this, "No se pudo encontrar los datos, por favor registre manualmente", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }.start();
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "IMG_" + Calendar.getInstance().getTime(), null);
        return Uri.parse(path);
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
}

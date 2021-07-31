package codi.drive.conductor.chiclayo;

import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * By: el-bryant
 */

public class HistorialEntreFechasActivity extends AppCompatActivity {
    @BindView(R.id.ivEntreFechas) ImageView ivIcono;
    @BindView(R.id.tvFechaDesde) TextView tvFechaDesde;
    @BindView(R.id.tvFechaHasta) TextView tvFechaHasta;
    @BindView(R.id.tvTitular) TextView tvTitular;
    int mDiaDesde, mMesDesde, mAnioDesde, mDiaHasta, mMesHasta, mAnioHasta;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entre_fechas);
        ButterKnife.bind(this);
        Calendar newCalendar = Calendar.getInstance();
        mDiaDesde = newCalendar.get(Calendar.DAY_OF_MONTH);
        mMesDesde = newCalendar.get(Calendar.MONTH);
        mAnioDesde = newCalendar.get(Calendar.YEAR);
        mDiaHasta = newCalendar.get(Calendar.DAY_OF_MONTH);
        mMesHasta = newCalendar.get(Calendar.MONTH);
        mAnioHasta = newCalendar.get(Calendar.YEAR);
        String fechaDesde = mDiaDesde + "/" + (mMesDesde + 1) + "/" + mAnioDesde;
        String fechaHasta = mDiaHasta + "/" + (mMesHasta + 1) + "/" + mAnioHasta;
        tvFechaDesde.setText(fechaDesde);
        tvFechaHasta.setText(fechaHasta);
    }

    @OnClick(R.id.btnConsultar) void consultarHistorial() {
        String fechaDesde = mAnioDesde + "-" + (mMesDesde + 1) + "-" + mDiaDesde;
        String fechaHasta = mAnioHasta + "-" + (mMesHasta + 1) + "-" + mDiaHasta;
        Log.i("consultarHistorial", fechaDesde + ", " + fechaHasta);
        Intent intent = new Intent(this, HistorialListaActivity.class);
        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(this,
                Pair.create(tvTitular, tvTitular.getTransitionName()),
                Pair.create(ivIcono, ivIcono.getTransitionName())
        );
        intent.putExtra("fecha_desde", fechaDesde);
        intent.putExtra("fecha_hasta", fechaHasta);
        intent.putExtra("titular", tvTitular.getText().toString());
        intent.putExtra("icono", R.drawable.ic_entre_fechas);
        startActivity(intent, activityOptions.toBundle());
        finish();
    }

    @OnClick(R.id.ivFechaDesde) void getFechaDesde() {
        DatePickerDialog dialogPicker = new DatePickerDialog(this, (datePicker, year, month, day) -> MostrarDatosFechaDesde(year, month, day), mAnioDesde, mMesDesde, mDiaDesde);
        dialogPicker.show();
    }

    @OnClick(R.id.ivFechaHasta) void getFechaHasta() {
        DatePickerDialog dialogPicker = new DatePickerDialog(this, (datePicker, year, month, day) -> MostrarDatosFechaHasta(year, month, day), mAnioHasta, mMesHasta, mDiaHasta);
        dialogPicker.show();
    }

    public void MostrarDatosFechaDesde(int year, int month, int day) {
        mDiaDesde = day;
        mMesDesde = month;
        mAnioDesde = year;
        tvFechaDesde.setText(day + "/" + (month + 1) + "/" + year);
    }

    public void MostrarDatosFechaHasta(int year, int month, int day) {
        mDiaHasta = day;
        mMesHasta = month;
        mAnioHasta = year;
        tvFechaHasta.setText(day + "/" + (month + 1) + "/" + year);
    }
}

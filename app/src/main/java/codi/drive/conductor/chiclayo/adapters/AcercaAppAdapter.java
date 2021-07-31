package codi.drive.conductor.chiclayo.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.sinaseyfi.advancedcardview.AdvancedCardView;
import java.util.ArrayList;
import codi.drive.conductor.chiclayo.AcercaAppActivity;
import codi.drive.conductor.chiclayo.ContenidoActivity;
import codi.drive.conductor.chiclayo.R;
import codi.drive.conductor.chiclayo.entity.AcercaApp;

/**
 * By: el-bryant
 */

public class AcercaAppAdapter extends RecyclerView.Adapter<AcercaAppAdapter.viewHolder> {
    Activity activity;
    ArrayList<AcercaApp> acercaApps;

    public AcercaAppAdapter(Activity activity, ArrayList<AcercaApp> acercaApps) {
        this.activity = activity;
        this.acercaApps = acercaApps;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(activity).inflate(R.layout.item_acerca_app, parent, false);
        return new AcercaAppAdapter.viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        AcercaApp acercaApp = acercaApps.get(position);
        String idAcercaApp = acercaApp.getIdAcercaApp();
        String nombre = acercaApp.getNombre();
        String contenido = acercaApp.getContenido();
        holder.tvNombre.setText(nombre);
        holder.acvAcercaApp.setOnClickListener(v -> {
            AcercaAppActivity.flayAcercaApp.setVisibility(View.VISIBLE);
            ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(activity, Pair.create(holder.tvNombre, holder.tvNombre.getTransitionName()));
            Intent intent = new Intent(activity, ContenidoActivity.class);
            intent.putExtra("idAcercaApp", idAcercaApp);
            intent.putExtra("nombre", nombre);
            intent.putExtra("contenido", contenido);
            activity.startActivity(intent, activityOptions.toBundle());
            activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }

    @Override
    public int getItemCount() {
        return acercaApps.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        AdvancedCardView acvAcercaApp;
        TextView tvNombre;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            acvAcercaApp = (AdvancedCardView) itemView.findViewById(R.id.acvAcercaApp);
            tvNombre = (TextView) itemView.findViewById(R.id.tvTitulo);
        }
    }
}

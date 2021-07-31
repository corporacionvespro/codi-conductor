package codi.drive.conductor.chiclayo.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.sinaseyfi.advancedcardview.AdvancedCardView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import codi.drive.conductor.chiclayo.PromocionDetalleActivity;
import codi.drive.conductor.chiclayo.R;
import codi.drive.conductor.chiclayo.entity.Promocion;

/**
 * By: El Bryant
 */

public class PromocionesListaAdapter extends RecyclerView.Adapter<PromocionesListaAdapter.ViewHolder> {
    Activity activity;
    ArrayList<Promocion> listas;

    public PromocionesListaAdapter(Activity activity, ArrayList<Promocion> listas) {
        this.activity = activity;
        this.listas = listas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(activity).inflate(R.layout.item_recomendado, parent, false);
        return new PromocionesListaAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Promocion lista = listas.get(position);
        String nombre = lista.getEmpresa();
        String logo = lista.getLogo();
        String descripcion = lista.getDescripcion();
        holder.tvNombre.setText(nombre);
        Picasso.get().load(logo).into(holder.ivLogo);
        holder.acvRecomendado.setOnClickListener(view -> {
            Intent intent = new Intent(activity, PromocionDetalleActivity.class);
            intent.putExtra("nombre", nombre);
            intent.putExtra("logo", logo);
            intent.putExtra("descripcion", descripcion);
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }

    @Override
    public int getItemCount() {
        return listas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivLogo;
        AdvancedCardView acvRecomendado;
        TextView tvNombre;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivLogo = (ImageView) itemView.findViewById(R.id.ivRecomendado);
            acvRecomendado = (AdvancedCardView) itemView.findViewById(R.id.acvRecomendado);
            tvNombre = (TextView) itemView.findViewById(R.id.tvNombre);
        }
    }
}

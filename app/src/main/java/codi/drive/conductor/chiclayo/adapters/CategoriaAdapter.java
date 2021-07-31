package codi.drive.conductor.chiclayo.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.util.Pair;
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
import codi.drive.conductor.chiclayo.R;
import codi.drive.conductor.chiclayo.RecomendadosActivity;
import codi.drive.conductor.chiclayo.entity.Categoria;

/**
 * By: el-bryant
 */

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.viewHolder> {
    Activity activity;
    ArrayList<Categoria> categorias;

    public CategoriaAdapter(Activity activity, ArrayList<Categoria> categorias) {
        this.activity = activity;
        this.categorias = categorias;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(activity).inflate(R.layout.item_categoria, parent, false);
        return new CategoriaAdapter.viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Categoria categoria = categorias.get(position);
        String idCategoria = categoria.getIdCategoria();
        String nombre = categoria.getNombre();
        String imagen = categoria.getImagen();
        holder.tvNombre.setText(nombre);
        Picasso.get().load(imagen).into(holder.ivCategoria);
        holder.acvCategoria.setOnClickListener(v -> {
            ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(activity,
                    Pair.create(holder.tvNombre, holder.tvNombre.getTransitionName()),
                    Pair.create(holder.ivCategoria, holder.ivCategoria.getTransitionName())
            );
            Intent intent = new Intent(activity, RecomendadosActivity.class);
            intent.putExtra("idCategoria", idCategoria);
            intent.putExtra("nombre", nombre);
            intent.putExtra("imagen", imagen);
            activity.startActivity(intent, activityOptions.toBundle());
        });
    }

    @Override
    public int getItemCount() {
        return categorias.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        AdvancedCardView acvCategoria;
        ImageView ivCategoria;
        TextView tvNombre;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            acvCategoria = (AdvancedCardView) itemView.findViewById(R.id.acvCategoria);
            ivCategoria = (ImageView) itemView.findViewById(R.id.ivCategoria);
            tvNombre = (TextView) itemView.findViewById(R.id.tvNombre);
        }
    }
}

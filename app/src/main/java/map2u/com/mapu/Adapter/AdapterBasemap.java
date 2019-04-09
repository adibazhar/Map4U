package map2u.com.mapu.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import map2u.com.mapu.BasemapModel;
import map2u.com.mapu.R;
import map2u.com.mapu.RecyclerViewListener;


public class AdapterBasemap extends RecyclerView.Adapter<AdapterBasemap.Holder> {

    ArrayList<BasemapModel> arrayList;
    RecyclerViewListener listener;

    public AdapterBasemap(ArrayList<BasemapModel> arrayList, RecyclerViewListener listener) {
        this.arrayList = arrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.basemap_row,viewGroup,false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        BasemapModel current = (BasemapModel) arrayList.get(i);

        holder.textView.setText(current.getBasemap());
        holder.itemView.setOnClickListener(v -> {listener.onItemClick(i);});
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class Holder extends RecyclerView.ViewHolder{

        private TextView textView;
        public Holder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.basemapText);
        }
    }

}

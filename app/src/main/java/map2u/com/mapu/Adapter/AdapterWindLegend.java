package map2u.com.mapu.Adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import map2u.com.mapu.Legend;
import map2u.com.mapu.R;


public class AdapterWindLegend extends RecyclerView.Adapter<AdapterWindLegend.Holder> {

    private ArrayList<Legend> arrayList;

    public AdapterWindLegend(ArrayList<Legend> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_wind_legend,viewGroup,false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        Legend current = (Legend) arrayList.get(i);

        holder.textView.setText(current.getDesc());
        holder.colorLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(current.getColor())));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class Holder extends RecyclerView.ViewHolder{
        private TextView textView;
        private RelativeLayout colorLayout;
        public Holder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.description);
            colorLayout = itemView.findViewById(R.id.arrow);
        }
    }
}
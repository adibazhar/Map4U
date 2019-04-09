package map2u.com.mapu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.esri.arcgisruntime.mapping.Basemap;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import map2u.com.mapu.Adapter.AdapterBasemap;


public class FragmentBasemapDialog extends DialogFragment {

    public FragmentBasemapDialog(){

    }
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    AdapterBasemap adapter;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_basemap_dialog,null);

        builder.setView(view).setTitle("Select Basemap").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        final ArrayList<BasemapModel> arrayList = new ArrayList<>();
        String[] basemapList = getResources().getStringArray(R.array.Basemaps);
        for (int i=0;i<basemapList.length;i++){
            String basemap = getResources().getStringArray(R.array.Basemaps)[i];
            arrayList.add(new BasemapModel(basemap));
        }

        adapter = new AdapterBasemap(arrayList,(position -> {

          //  BasemapModel current = arrayList.get(position);
            LoadBasemap loadBasemap = new LoadBasemap((SecondActivity) getActivity());
            loadBasemap.execute(position);
            dismiss();
        }));
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        return builder.create();
    }

    private static class LoadBasemap extends AsyncTask<Integer, Basemap, Basemap>{
        WeakReference<SecondActivity> activityWeakReference;
        Basemap basemap;

        public LoadBasemap(SecondActivity activity){
            activityWeakReference = new WeakReference<>(activity);
        }


        @Override
        protected Basemap doInBackground(Integer... integers) {

            switch (integers[0]){
                case 0:basemap = Basemap.createDarkGrayCanvasVector();
                break;
                case 1:basemap = Basemap.createImagery();
                break;
                case 2:basemap = Basemap.createImageryWithLabels();
                break;
                case 3:basemap = Basemap.createImageryWithLabelsVector();
                 break;
                case 4:basemap = Basemap.createLightGrayCanvas();
                 break;
                case 5:basemap = Basemap.createLightGrayCanvasVector();
                 break;
                case 6:basemap = Basemap.createNationalGeographic();
                 break;
                case 7:basemap = Basemap.createNavigationVector();
                 break;
                case 8:basemap = Basemap.createOceans();
                 break;
                case 9:basemap = Basemap.createOpenStreetMap();
                 break;
                case 10:basemap = Basemap.createStreets();
                 break;
                case 11:basemap = Basemap.createStreetsNightVector();
                 break;
                case 12:basemap = Basemap.createStreetsVector();
                 break;
                case 13:basemap = Basemap.createStreetsWithReliefVector();
                 break;
                 case 14:basemap = Basemap.createTerrainWithLabels();
                 break;
                 case 15:basemap = Basemap.createTerrainWithLabelsVector();
                 break;
                 case 16:basemap = Basemap.createTopographic();
                 break;
                 case 17:basemap = Basemap.createTopographicVector();
                 break;
            }

            return basemap;
        }


        @Override
        protected void onPostExecute(Basemap newBasemap) {
            super.onPostExecute(newBasemap);
            SecondActivity activity = activityWeakReference.get();
            if (activity==null || activity.isFinishing()){
                return;
            }

            activity.baseMap.setBasemap(newBasemap);
            activity.mapView.setMap(activity.baseMap);
        }
    }

}

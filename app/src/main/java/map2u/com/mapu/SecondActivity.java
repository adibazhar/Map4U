package map2u.com.mapu;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.layers.ArcGISSublayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.tooltip.Tooltip;

import java.util.ArrayList;
import java.util.List;

import map2u.com.mapu.Adapter.AdapterLegend;
import map2u.com.mapu.Adapter.AdapterWindLegend;


public class SecondActivity extends AppCompatActivity implements FragmentDayDialog.OnItemSelectListener, NavigationView.OnNavigationItemSelectedListener {

    MapView mapView;
    ArcGISMapImageLayer tempImageLayer,rainImageLayer,humidImageLayer,windImageLayer;
    List<ArcGISSublayer> tempLayer,rainLayer,humidLayer,windLayer;
    public ArcGISMap baseMap;
    private LocationDisplay locationDisplay;
    DrawerLayout drawerLayout;

    private FloatingActionButton fabMenu,fabDay,fabTemp,fabRain,fabHumid,fabWind,fabGPS,fabBasemap;
    Boolean isFabTempClicked = true;    Boolean isTempToggled = false;
    Boolean isFabRainClicked = true;    Boolean isRainToggled = false;
    Boolean isFabHumidClicked = true;   Boolean isHumidToggled = false;
    Boolean isFabWindClicked =true;     Boolean isWindToggled = false;

    private TextView timeTv,dateTv,windText,tempText,humidText,rainText;
    Boolean isLegendOpen = false;

    private AppCompatSeekBar windBar,tempBar,humidBar,rainBar;

    LinearLayout windLay, tempLay, humidityLay,rainLay,windOpacityLay,tempOpacityLay,humidOpacityLay,rainOpacityLay;
    Float translationY =100f;
    OvershootInterpolator interpolator = new OvershootInterpolator();
    Boolean isFabOpen = false;

    private int selectedLayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        mapView = findViewById(R.id.myMap);
        timeTv = findViewById(R.id.timeTv);
        dateTv = findViewById(R.id.dateTv);
        fabDay = findViewById(R.id.fabDay);
        fabGPS = findViewById(R.id.fabGPS);
        fabBasemap = findViewById(R.id.fabBasemap);

        fabTemp = findViewById(R.id.fabTemp);   fabRain = findViewById(R.id.fabRainfall);
        fabHumid = findViewById(R.id.fabHumidity);  fabWind = findViewById(R.id.fabWind);

        double latitude = 4.210484;
        double longitude = 101.97576600000002;


        drawerLayout = findViewById(R.id.drawer_layouts);
        NavigationView navigationView = findViewById(R.id.drawers);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.drawer_open,R.string.drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        //set ArcGIS license
        ArcGISRuntimeEnvironment.setLicense("runtimelite,1000,rud9194204946,none,FA0RJAY3FL9BY7ZPM102");

        baseMap = new ArcGISMap(Basemap.Type.OPEN_STREET_MAP,
                latitude,longitude,6);


        //temperature layer
        tempImageLayer = new ArcGISMapImageLayer(
                "https://bpp1.met.gov.my/agssvr2/rest/services/temperature/Suhu/MapServer");
        tempLayer =tempImageLayer.getSublayers();

        rainImageLayer = new ArcGISMapImageLayer("https://bpp1.met.gov.my/agssvr2/rest/services/rainfall/Hujan_backup/MapServer");
        rainLayer = rainImageLayer.getSublayers();

        humidImageLayer = new ArcGISMapImageLayer("https://bpp1.met.gov.my/agssvr2/rest/services/humidity/Kelembapan/MapServer");
        humidLayer = humidImageLayer.getSublayers();

        windImageLayer = new ArcGISMapImageLayer(("https://bpp1.met.gov.my/agssvr2/rest/services/wind/Angin/MapServer"));
        windLayer = windImageLayer.getSublayers();
        initFabMenu();
        openDialog();
        setLegendView();
        setupSeekBar();

        List<ArcGISMapImageLayer> listMapImage = new ArrayList<>();
        listMapImage.add(tempImageLayer);
        listMapImage.add(rainImageLayer);
        listMapImage.add(humidImageLayer);
        listMapImage.add(windImageLayer);

        baseMap.getOperationalLayers().addAll(listMapImage);
        mapView.setMap(baseMap);

        fabGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(SecondActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(SecondActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            123);
                }
                setupLocationDisplay();
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            locationDisplay.startAsync();
        }
        else Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
    }

    //private static class LoadLayer extends AsyncTask<>

    private void setupLocationDisplay(){
        locationDisplay = mapView.getLocationDisplay();
        locationDisplay.addDataSourceStatusChangedListener(dataSourceStatusChangedEvent ->{
            if (dataSourceStatusChangedEvent.isStarted() || dataSourceStatusChangedEvent.getError()==null){
                return;
            }
        } );

        locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.RECENTER);
        locationDisplay.startAsync();
    }

    private void setLegendView(){
        AdapterLegend adapterLegend;
        AdapterLegend adapterLegend1;
        AdapterLegend adapterLegend2;

        /*LinearLayout layout = findViewById(R.id.legendLayout);*/
        NestedScrollView layout = findViewById(R.id.legendLayout);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        try {
            display.getRealSize(size);
        }catch (NoSuchMethodError error){
            display.getSize(size);
        }
        int height = size.y;
        float newHeight = (float) (0.7*height);
        RelativeLayout relativeLayout = findViewById(R.id.relativeLay);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                (int) newHeight);
        relativeLayout.setLayoutParams(params);

        Button button = findViewById(R.id.legendBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLegendOpen){
                    isLegendOpen = !isLegendOpen;
                    layout.setVisibility(View.INVISIBLE);
                }
                else {
                    isLegendOpen = !isLegendOpen;
                    layout.setVisibility(View.VISIBLE);
                }
            }
        });

        ArrayList<Legend> tempLegend = new ArrayList<>();
        String[] tempColor = getResources().getStringArray(R.array.temp_color);
        String[] tempDesc = getResources().getStringArray(R.array.temp_desc);
        for (int i=0;i<tempColor.length;i++){
            tempLegend.add(new Legend(tempColor[i],tempDesc[i]));
        }

        ArrayList<Legend> humidLegend = new ArrayList<>();
        String[] humidColor = getResources().getStringArray(R.array.humidityColor);
        String[] humidDesc = getResources().getStringArray(R.array.humidityDesc);
        for (int i=0;i<humidColor.length;i++){
            humidLegend.add(new Legend(humidColor[i],humidDesc[i]));
        }

        ArrayList<Legend> rainLegend = new ArrayList<>();
        String[] rainColor = getResources().getStringArray(R.array.rainColor);
        String[] rainDesc = getResources().getStringArray(R.array.rainDesc);
        for (int i=0;i<rainColor.length;i++){
            rainLegend.add(new Legend(rainColor[i],rainDesc[i]));
        }

        ArrayList<Legend> windLegend = new ArrayList<>();
        String[] windColor = getResources().getStringArray(R.array.windColor);
        String[] windDesc = getResources().getStringArray(R.array.windDesc);
        for (int i=0;i<windColor.length;i++){
            windLegend.add(new Legend(windColor[i],windDesc[i]));
        }

        adapterLegend = new AdapterLegend(tempLegend);
        RecyclerView tempRv = findViewById(R.id.tempRecycler);
        tempRv.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        tempRv.setLayoutManager(layoutManager);
        tempRv.setAdapter(adapterLegend);

        RecyclerView humidRv = findViewById(R.id.humidRecycler);
        humidRv.setHasFixedSize(true);
        adapterLegend1 = new AdapterLegend(humidLegend);
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        humidRv.setLayoutManager(layoutManager1);
        humidRv.setAdapter(adapterLegend1);

        RecyclerView rainRv = findViewById(R.id.rainRecycler);
        rainRv.setHasFixedSize(true);
        adapterLegend2 = new AdapterLegend(rainLegend);
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        rainRv.setLayoutManager(layoutManager2);
        rainRv.setAdapter(adapterLegend2);

        AdapterWindLegend windAdapter;
        RecyclerView windRv = findViewById(R.id.windRecycler);
        windRv.setHasFixedSize(true);
        windAdapter = new AdapterWindLegend(windLegend);
        RecyclerView.LayoutManager layoutManager3 = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        windRv.setLayoutManager(layoutManager3);
        windRv.setAdapter(windAdapter);

        tempRv.setNestedScrollingEnabled(false);
        humidRv.setNestedScrollingEnabled(false);
        rainRv.setNestedScrollingEnabled(false);
        windRv.setNestedScrollingEnabled(false);

    }

    private void setupSeekBar(){

        windOpacityLay = findViewById(R.id.windOpacityLay);
        tempOpacityLay = findViewById(R.id.tempOpacityLay);
        humidOpacityLay = findViewById(R.id.humidOpacityLay);
        rainOpacityLay = findViewById(R.id.rainOpacityLay);

        windBar = findViewById(R.id.windSeekbar);
        tempBar = findViewById(R.id.tempSeekbar);
        humidBar = findViewById(R.id.humidSeekbar);
        rainBar = findViewById(R.id.rainSeekbar);

        windText = findViewById(R.id.windOpacityText);
        tempText = findViewById(R.id.tempOpacityText);
        humidText = findViewById(R.id.humidOpacityText);
        rainText = findViewById(R.id.rainOpacityText);

        windBar.setProgress(100);
        tempBar.setProgress(100);
        humidBar.setProgress(100);
        rainBar.setProgress(100);

        windOpacityLay.setVisibility(View.GONE);
        tempOpacityLay.setVisibility(View.GONE);
        humidOpacityLay.setVisibility(View.GONE);
        rainOpacityLay.setVisibility(View.GONE);

    }

    private void initFabMenu() {
        fabMenu = findViewById(R.id.fabMenu);
        windLay = findViewById(R.id.windLay);
        tempLay = findViewById(R.id.tempLay);
        humidityLay = findViewById(R.id.humidityLay);
        rainLay = findViewById(R.id.rainLay);


        windLay.setAlpha(0f);
        tempLay.setAlpha(0f);
        humidityLay.setAlpha(0f);
        rainLay.setAlpha(0f);

        fabMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFabOpen){
                    closeFab();
                }
                else openFab();
            }
        });
    }

    private void openFab() {
        isFabOpen = !isFabOpen;
        if (isWindToggled)  {
            windOpacityLay.setVisibility(View.VISIBLE);
        }
        if (isTempToggled)  {
            tempOpacityLay.setVisibility(View.VISIBLE);
        }
        if (isHumidToggled)  {
            humidOpacityLay.setVisibility(View.VISIBLE);
        }
        if (isRainToggled)  {
            rainOpacityLay.setVisibility(View.VISIBLE);
        }

        fabMenu.animate().setInterpolator(interpolator).rotation(90f).setDuration(300).start();
        tempLay.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(400).start();
        windLay.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(450).start();
        humidityLay.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(350).start();
        rainLay.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
    }

    private void closeFab() {
        isFabOpen = !isFabOpen;

        windOpacityLay.setVisibility(View.GONE);
        tempOpacityLay.setVisibility(View.GONE);
        humidOpacityLay.setVisibility(View.GONE);
        rainOpacityLay.setVisibility(View.GONE);
        fabMenu.animate().setInterpolator(interpolator).rotation(0f).setDuration(300).start();
        tempLay.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        windLay.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        humidityLay.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        rainLay.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
    }

    public void openDialog(){
        fabDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentDayDialog dialog = new FragmentDayDialog();
                dialog.show(getSupportFragmentManager(),"Day Dialog");
            }
        });

        fabBasemap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentBasemapDialog dialog = new FragmentBasemapDialog();
                dialog.show(getSupportFragmentManager(),"Select Basemap Dialog");
            }
        });
    }

    public void setLayer(int day,int time,String format){
        tempImageLayer.resetSublayers();
        rainImageLayer.resetSublayers();
        humidImageLayer.resetSublayers();
        windImageLayer.resetSublayers();

        if (format.equals("GMT")){
            switch (day){
                case 0:
                    selectedLayer=time;
                    break;

                case 1:
                    selectedLayer = time + 3;
                    break;

                case 2:
                    selectedLayer = time + 27;
                    break;

                case 3:
                    selectedLayer = time + 51;
                    break;

                case 4:
                    selectedLayer = time + 75;
                    break;

                case 5:
                    selectedLayer = time + 99;
                    break;

                case 6:
                    selectedLayer = time + 123;
                    break;
            }
        }

        else {
            switch (day){
                case 0:
                    selectedLayer=time;
                    break;

                case 1:
                    selectedLayer = time + 12;
                    break;

                case 2:
                    selectedLayer = time + 35;
                    break;

                case 3:
                    selectedLayer = time + 59;
                    break;

                case 4:
                    selectedLayer = time + 83;
                    break;

                case 5:
                    selectedLayer = time + 107;
                    break;

                case 6:
                    selectedLayer = time + 131;
                    break;
            }
        }


        // TODO:error handling klau x bole access server
        if (tempLayer.size()==0 || rainLayer.size()==0){
            return;
        }

        if (isTempToggled){
            tempLayer.get(selectedLayer).setVisible(true);
        }
        else tempLayer.get(selectedLayer).setVisible(false);

        if (isRainToggled){
            rainLayer.get(selectedLayer).setVisible(true);
        }
        else rainLayer.get(selectedLayer).setVisible(false);

        if (isHumidToggled){
            humidLayer.get(selectedLayer).setVisible(true);
        }
        else humidLayer.get(selectedLayer).setVisible(false);

        if (isWindToggled){
            windLayer.get(selectedLayer).setVisible(true);
        }
        else{
            windLayer.get(selectedLayer).setVisible(false);
        }
        tempFunction();
        rainFunction();
        humidFunction();
        windFunction();

    }



    public void tempFunction(){
        tempImageLayer.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                if (tempImageLayer.getLoadStatus()== LoadStatus.LOADED){

                    fabTemp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isFabTempClicked){

                                setVisibility(tempLayer,isFabTempClicked);
                                fabTemp.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                                fabTemp.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.design_default_color_primary)));
                                isFabTempClicked =false;
                                isTempToggled = true;
                                tempOpacityLay.setVisibility(View.VISIBLE);
                                setOpacity(tempText,tempBar,tempImageLayer);
                            }
                            else  {
                                setVisibility(tempLayer,isFabTempClicked);
                                fabTemp.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.design_default_color_primary)));
                                fabTemp.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                                isFabTempClicked =true;
                                isTempToggled = false;
                                tempOpacityLay.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });

        fabTemp.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setTooltip(v,"Temperature");
                return true;
            }
        });
    }

    public void rainFunction(){
        rainImageLayer.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                if (rainImageLayer.getLoadStatus()== LoadStatus.LOADED){

                    fabRain.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isFabRainClicked){

                                setVisibility(rainLayer,isFabRainClicked);
                                fabRain.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                                fabRain.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.design_default_color_primary)));
                                isFabRainClicked =false;
                                isRainToggled = true;
                                rainOpacityLay.setVisibility(View.VISIBLE);
                                setOpacity(rainText,rainBar,rainImageLayer);
                            }
                            else  {
                                setVisibility(rainLayer,isFabRainClicked);
                                fabRain.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.design_default_color_primary)));
                                fabRain.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                                isFabRainClicked =true;
                                isRainToggled = false;
                                rainOpacityLay.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });

        fabRain.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setTooltip(v,"Rainfall");
                return true;
            }
        });
    }

    public void humidFunction(){
        humidImageLayer.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                if (humidImageLayer.getLoadStatus()== LoadStatus.LOADED){

                    fabHumid.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isFabHumidClicked){

                                setVisibility(humidLayer,isFabHumidClicked);
                                fabHumid.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                                fabHumid.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.design_default_color_primary)));
                                isFabHumidClicked =false;
                                isHumidToggled = true;
                                humidOpacityLay.setVisibility(View.VISIBLE);
                                setOpacity(humidText,humidBar,humidImageLayer);
                            }
                            else  {
                                setVisibility(humidLayer,isFabHumidClicked);
                                fabHumid.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.design_default_color_primary)));
                                fabHumid.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                                isFabHumidClicked =true;
                                isHumidToggled = false;
                                humidOpacityLay.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });

        fabHumid.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setTooltip(v,"Humidity");
                return true;
            }
        });
    }

    public void windFunction(){

        windImageLayer.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                if (windImageLayer.getLoadStatus()== LoadStatus.LOADED){

                    fabWind.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isFabWindClicked){

                                setVisibility(windLayer,isFabWindClicked);
                                fabWind.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                                fabWind.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.design_default_color_primary)));
                                isFabWindClicked =false;
                                isWindToggled = true;
                                windOpacityLay.setVisibility(View.VISIBLE);
                                setOpacity(windText,windBar,windImageLayer);
                            }
                            else  {
                                setVisibility(windLayer,isFabWindClicked);
                                fabWind.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.design_default_color_primary)));
                                fabWind.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                                isFabWindClicked =true;
                                isWindToggled = false;
                                windOpacityLay.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });

        fabWind.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setTooltip(v,"wind");
                return true;
            }
        });

    }

    private void setTooltip(View view,String text){
        Tooltip tooltip = new Tooltip.Builder(view)
                .setText(text)
                .setBackgroundColor(getResources().getColor(R.color.white))
                .setGravity(Gravity.LEFT)
                .setCancelable(true)
                .show();
    }

    private void setOpacity(TextView textView, SeekBar seekBar, ArcGISMapImageLayer imageLayer){


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textView.setText(String.valueOf(progress)+"%");
                float fprogress = (float) progress/100;
                imageLayer.setOpacity((float) fprogress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void setVisibility(List<ArcGISSublayer> layer,  Boolean isBtnClicked){
        if (isBtnClicked){
            layer.get(selectedLayer).setVisible(true);
            return;
        }
        layer.get(selectedLayer).setVisible(false);
    }
    @Override
    protected void onPause() {
        mapView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.dispose();
    }

    @Override
    public void onItemSelect(int dayPosition, int timePosition,String day,String time,String format) {
        setLayer(dayPosition,timePosition,format);
        String newDay = day;
        newDay = newDay.replace("(","\n");
        newDay = newDay.replace(")","");
        dateTv.setText(newDay);
        timeTv.setText(time + format);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        // String itemName = (String) menuItem.getTitle();
        // int itemName = menuItem.getItemId();
        //tvInfo.setText(itemName);


        /*if(itemName==R.id.item_a){
            Toast.makeText(this,"Basemap selected", Toast.LENGTH_SHORT).show();
        }
        if(itemName==R.id.item_b){
            startActivity(new Intent(MainActivity.this,SecondActivity.class));
        }*/

        switch (menuItem.getItemId()){
            case R.id.item_a:
                Intent mapp = new Intent(this,MainActivity.class);
                mapp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mapp);
                Toast.makeText(getApplicationContext(),"Home",Toast.LENGTH_SHORT).show();
                break;

           // case R.id.item_c:
                //getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,
                //  new MapFragment()).commit();
                //Intent map = new Intent(SecondActivity.this,SecondActivity.class);
                //startActivity(map);
                //Toast.makeText(getApplicationContext(),"StartActivity 2",Toast.LENGTH_SHORT).show();
                //break;

        }
        closeDrawer();
        return true;




    }

    private void closeDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            closeDrawer();
        }
        else super.onBackPressed();

    }
}

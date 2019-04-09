package map2u.com.mapu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;



public class Tab2Fragment extends Fragment {

    TextView selectCity, cityField, detailsField, currentTemperatureField, humidity_field, pressure_field, weatherIcon, updatedField,t1_time,t2_day,t3_date,weatherTv,weatherDescTv;
    ProgressBar loader;
    Typeface weatherFont, myfont;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    String city = "";
    /* Please Put your API KEY here */
    String OPEN_WEATHER_MAP_API = "27d6f76ebfd1a4059451db62fc555b68";
    /* Please Put your API KEY here */

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_tab2, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        t1_time=(TextView)view.findViewById(R.id.timeTv);
        //t2_day = (TextView)view.findViewById(R.id.dayTv);
        //t3_date = (TextView)view.findViewById(R.id.dateTv);
        // loader = (ProgressBar) view.findViewById(R.id.loader);
        selectCity = (TextView) view.findViewById(R.id.selectCity);
        cityField = (TextView) view.findViewById(R.id.city_field);
        //updatedField = (TextView) view.findViewById(R.id.updated_field);
        //detailsField = (TextView) view.findViewById(R.id.details_field);
        currentTemperatureField = (TextView) view.findViewById(R.id.weatherTv);
        //humidity_field = (TextView) view.findViewById(R.id.humidity_field);
        // pressure_field = (TextView) view.findViewById(R.id.pressure_field);
        weatherIcon = (TextView) view.findViewById(R.id.weather_icon);
        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weathericons-regular-webfont.ttf");
        weatherIcon.setTypeface(weatherFont);

        //weatherTv = view.findViewById(R.id.weatherTv);
        //weatherDescTv = view.findViewById(R.id.weatherDesc);

        taskLoadUp(city);

        selectCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setTitle("Search City");
                final EditText input = new EditText(getContext());
                input.setText(city);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);

                alertDialog.setPositiveButton("Search",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                city = input.getText().toString();
                                taskLoadUp(city);
                            }
                        });
                alertDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertDialog.show();

               /* if(savedInstanceState == null){
                    mSubFragment = new subFragment();

                    getChildFragmentManager()
                            .beginTransaction()
                            .add(R.id.fragment_sub, mSubFragment, "tag")
                            .commit();
                }*/
         }
        });

        return view;
    }

    public void taskLoadUp(String query) {
        if (Function.isNetworkAvailable(getActivity().getApplicationContext())) {
            DownloadWeather task = new DownloadWeather();
            task.execute(query);
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    class DownloadWeather extends AsyncTask< String, Void, String > {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //   loader.setVisibility(View.VISIBLE);

        }
        protected String doInBackground(String...args) {
            String xml = Function.excuteGet("http://api.openweathermap.org/data/2.5/weather?q=" + args[0] +
                    "&units=metric&appid=" + OPEN_WEATHER_MAP_API);
            return xml;
        }
        @Override
        protected void onPostExecute(String xml) {

            try {
                if (xml==null){
                    return;
                }
                JSONObject json = new JSONObject(xml);
                if (json != null) {
                    JSONObject details = json.getJSONArray("weather").getJSONObject(0);
                    JSONObject main = json.getJSONObject("main");
                    DateFormat df = DateFormat.getDateTimeInstance();
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.US);
                    String formatted_time = sdf.format(calendar.getTime());
                    SimpleDateFormat sdf1 = new SimpleDateFormat("EEEE", Locale.US);
                    String formatted_day = sdf1.format(calendar.getTime());
                    SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy",Locale.US);
                    String formatted_date = sdf2.format(calendar.getTime());

                    cityField.setText(json.getString("name").toUpperCase(Locale.US) + ", " + json.getJSONObject("sys").getString("country"));
                    //weatherDescTv.setText(details.getString("description").toUpperCase(Locale.US));
                    currentTemperatureField.setText(String.format("%.0f", main.getDouble("temp")) + "°C");
                    // humidity_field.setText("Humidity: " + main.getString("humidity") + "%");
                    //pressure_field.setText("Pressure: " + main.getString("pressure") + " hPa");
                    t1_time.setText(df.format(new Date(json.getLong("dt") * 1000)));
                    //t2_day.setText(formatted_day);
                    //t3_date.setText(formatted_date);
                    weatherIcon.setText(Html.fromHtml(Function.setWeatherIcon(details.getInt("id"),
                            json.getJSONObject("sys").getLong("sunrise") * 1000,
                            json.getJSONObject("sys").getLong("sunset") * 1000)));

                    // loader.setVisibility(View.GONE);

                }
            } catch (JSONException e) {
                //detect error city takde dlm search
                //Toast.makeText(getActivity().getApplicationContext(), "", Toast.LENGTH_SHORT).show();
            }


        }



    }
}

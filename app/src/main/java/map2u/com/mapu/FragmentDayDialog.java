package map2u.com.mapu;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class FragmentDayDialog extends DialogFragment implements AdapterView.OnItemSelectedListener {

    public interface OnItemSelectListener{
         void onItemSelect(int dayPosition, int timePosition, String day, String time, String format);
    }

    OnItemSelectListener listener;

    public int dayPostion, timePosition;
    public String day,time, timeZoneFormat;
    private Spinner formatSpinner,daySpinner,timeSpinner;
    private ArrayList dayList;
    boolean gmtSelected ;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),android.R.style.Theme_Material_Dialog_Alert);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_day_dialog,null);


        builder.setView(view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onItemSelect(dayPostion,timePosition,day,time,timeZoneFormat);
                    }
                });

        dayList = new ArrayList();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat dayFormat = new SimpleDateFormat("EEEE");
        for (int i=-1;i<7;i++){
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, +i);
            String date = dateFormat.format(cal.getTime());
            String day = dayFormat.format(cal.getTime());
            dayList.add(day + "(" +date +")");
        }

        formatSpinner = view.findViewById(R.id.formatSpinner);
        ArrayAdapter<CharSequence> formatAdapter = ArrayAdapter.createFromResource(getContext(),R.array.format,android.R.layout.simple_spinner_item);
        formatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        formatSpinner.setAdapter(formatAdapter);

        daySpinner = view.findViewById(R.id.daySpinner);
        timeSpinner = view.findViewById(R.id.timeSpinner);
        formatSpinner.setOnItemSelectedListener(this);
        daySpinner.setOnItemSelectedListener(this);
        timeSpinner.setOnItemSelectedListener(this);

        return builder.create();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (parent.getId() == R.id.formatSpinner){
            if (position ==0){
                ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(getActivity(),android.R.layout.simple_spinner_item,dayList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                daySpinner.setAdapter(adapter);
                timeZoneFormat="GMT";
                gmtSelected=true;
            }
            else if (position==1){
                ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(getActivity(),android.R.layout.simple_spinner_item,dayList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                daySpinner.setAdapter(adapter);
                timeZoneFormat="UTC";
                gmtSelected= !gmtSelected;
            }
        }
       else if (parent.getId() == R.id.daySpinner){
            ArrayAdapter<CharSequence> adapter1;
            if (gmtSelected){
                if (position==0){
                    adapter1 = ArrayAdapter.createFromResource(getActivity(),R.array.GMT_first_day_time,android.R.layout.simple_spinner_item);
                }

                else if(position==7){
                    adapter1 = ArrayAdapter.createFromResource(getActivity(),R.array.Gmt_last_day_time,android.R.layout.simple_spinner_item);
                }
                else {
                    adapter1 = ArrayAdapter.createFromResource(getActivity(),R.array.time,android.R.layout.simple_spinner_item);
                }
                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                timeSpinner.setAdapter(adapter1);
                dayPostion = position;
                day = parent.getItemAtPosition(position).toString();
            }
           else {
                if (position==0){
                    adapter1 = ArrayAdapter.createFromResource(getActivity(),R.array.utc_first_day,android.R.layout.simple_spinner_item);
                }

                else if(position==7){
                    adapter1 = ArrayAdapter.createFromResource(getActivity(),R.array.utc_last_day,android.R.layout.simple_spinner_item);
                }
                else {
                    adapter1 = ArrayAdapter.createFromResource(getActivity(),R.array.time,android.R.layout.simple_spinner_item);
                }
                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                timeSpinner.setAdapter(adapter1);
                dayPostion = position;
                day = parent.getItemAtPosition(position).toString();
            }

        }

        else if (parent.getId() == R.id.timeSpinner){
            timePosition = position;
            time = parent.getItemAtPosition(position).toString();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (OnItemSelectListener) getActivity();
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()+"must implement onItemSelect listener");
        }
    }



}

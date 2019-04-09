package com.andrewkoloskov.northlord.RentWorker;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.andrewkoloskov.northlord.CarWorker.CarInfoActivity;
import com.andrewkoloskov.northlord.CarWorker.EditCar;
import com.andrewkoloskov.northlord.InitialData;
import com.andrewkoloskov.northlord.R;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.time.Period;
import java.util.Calendar;
import java.util.GregorianCalendar;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddRent extends DialogFragment {
    EditText start;
    EditText EditEnd;
    EditText Cost;
    EditText Name;
    Rent rent;
    GregorianCalendar dateAndTime = new GregorianCalendar();
    GregorianCalendar end = new GregorianCalendar();
    AddRentTask mAuthTask;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.addrent, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        EditEnd = view.findViewById(R.id.AddRentEndDate);
        rent = new Rent();
        Name=view.findViewById(R.id.AddRentName);
        Cost = view.findViewById(R.id.AddRentCost);
        Cost.setEnabled(false);
        start = view.findViewById(R.id.AddRentStartDate);
        view.findViewById(R.id.AddRentYes).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // When button is clicked, call up to owning activity.
                mAuthTask=new AddRentTask();
                mAuthTask.execute((Void) null);
            }
        });
        view.findViewById(R.id.AddRentNo).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // When button is clicked, call up to owning activity.
                getDialog().cancel();
            }
        });
        view.findViewById(R.id.AddRentStartDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = 0;
                setstart(v);

            }
        });
        view.findViewById(R.id.AddRentEndDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setend();

            }
        });
        return view;
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        getActivity().setTitle("Профиль");
//        Label=getView().findViewById(R.id.EditCarLabel);
//        Model=getView().findViewById(R.id.EditCarModel);
//        Cost=getView().findViewById(R.id.EditCarCost);
//        Rent=getView().findViewById(R.id.EditCarRent);
//        c=((CarInfoActivity)getActivity()).car;
//        Label.setText(c.Label);
//        Model.setText(c.Model);
//        Cost.setText(c.cost+"");
//        Rent.setText(c.rentcost+"");

    }

    void updateCost() {
        if (end.after(dateAndTime)) {
            int c = 0;
            int r = ((CarInfoActivity) getActivity()).car.rentcost;
//            Period p=new Period(dateAndTime.getTimeInMillis(),end.getTimeInMillis());
            c = (int) (r * (end.getTimeInMillis() - dateAndTime.getTimeInMillis()) / 3600 / 1000);
            Cost.setText(c + "");
            rent.cost = c;
        }
    }

    public void setstart(View v) {
        new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dateAndTime.set(year, month, dayOfMonth);
                new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        dateAndTime.set(dateAndTime.get(Calendar.YEAR), dateAndTime.get(Calendar.MONTH), dateAndTime.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
//                        start.setText(dateAndTime.get(Calendar.YEAR) + " " + dateAndTime.get(Calendar.MONTH) + " " + dateAndTime.get(Calendar.DAY_OF_MONTH) + " " + dateAndTime.get(Calendar.HOUR_OF_DAY) + " " + dateAndTime.get(Calendar.MINUTE) + " ");
                        start.setText(dateAndTime.get(Calendar.DAY_OF_MONTH) + "." + dateAndTime.get(Calendar.MONTH) + "." + dateAndTime.get(Calendar.YEAR) + " " + dateAndTime.get(Calendar.HOUR_OF_DAY) + ":" + dateAndTime.get(Calendar.MINUTE));
                        rent.start = dateAndTime;
                        updateCost();
                    }
                },
                        dateAndTime.get(Calendar.HOUR_OF_DAY),
                        dateAndTime.get(Calendar.MINUTE), true)
                        .show();
            }
        },
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void setend() {
        new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                end.set(year, month, dayOfMonth);
                new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        end.set(end.get(Calendar.YEAR), end.get(Calendar.MONTH), end.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
                        EditEnd.setText(end.get(Calendar.DAY_OF_MONTH) + "." + end.get(Calendar.MONTH) + "." + end.get(Calendar.YEAR) + " " + end.get(Calendar.HOUR_OF_DAY) + ":" + end.get(Calendar.MINUTE));
                        rent.end = end;
                        updateCost();
                    }
                },
                        dateAndTime.get(Calendar.HOUR_OF_DAY),
                        dateAndTime.get(Calendar.MINUTE), true)
                        .show();
            }
        },
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    public class AddRentTask extends AsyncTask<Void, Void, Boolean> {


        JSONObject obj = new JSONObject();

        AddRentTask() {

        }
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if(rent.cost>0&!Name.getText().toString().equals("")){
                    OkHttpClient client = new OkHttpClient();
                    RequestBody formBody = new FormBody.Builder()
                            .add("login", URLEncoder.encode(((CarInfoActivity) getActivity()).login,"UTF-8"))
                            .add("password", URLEncoder.encode(((CarInfoActivity) getActivity()).password,"UTF-8"))
                            .add("id", ((CarInfoActivity) getActivity()).car.id+"")
                            .add("name", URLEncoder.encode(Name.getText().toString(),"UTf-8"))
                            .add("startdate", ""+dateAndTime.get(Calendar.DAY_OF_MONTH)+" "+dateAndTime.get(Calendar.MONTH)+" "+dateAndTime.get(Calendar.YEAR))
                            .add("starttime", ""+dateAndTime.get(Calendar.HOUR_OF_DAY)+" "+dateAndTime.get(Calendar.MINUTE))
                            .add("enddate", ""+end.get(Calendar.DAY_OF_MONTH)+" "+end.get(Calendar.MONTH)+" "+end.get(Calendar.YEAR))
                            .add("endtime", ""+end.get(Calendar.HOUR_OF_DAY)+" "+end.get(Calendar.MINUTE))
                            .add("cost", Cost.getText().toString())
                            .build();
                    Request request = new Request.Builder()
                            .url(InitialData.url + "/rent")
                            .post(formBody)
                            .build();
                    String serverAnswer = "";
                    try {
                        Response response = client.newCall(request).execute();
                        serverAnswer = response.body().string();
                        obj = new JSONObject();
                        if (serverAnswer != null) {
                            if (serverAnswer.equals("succ")) return true;
                        }
                        return false;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
                return false;

            } catch (Exception e) {
                return false;
            }

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
//            ((MainActivity)getActivity()).showProgress(false);

            if (success) {
                ((CarInfoActivity) getActivity()).update();
                ((CarInfoActivity) getActivity()).updatelist();
                getDialog().dismiss();
            } else {
                Snackbar.make(getView(), "no connection", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}

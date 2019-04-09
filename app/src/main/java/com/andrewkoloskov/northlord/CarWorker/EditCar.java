package com.andrewkoloskov.northlord.CarWorker;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import com.andrewkoloskov.northlord.InitialData;
import com.andrewkoloskov.northlord.Main.MainActivity;
import com.andrewkoloskov.northlord.Profile.EditprofileFragment;
import com.andrewkoloskov.northlord.Profile.profileFragment;
import com.andrewkoloskov.northlord.R;

import org.json.JSONObject;

import java.net.URLDecoder;
import java.net.URLEncoder;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditCar extends DialogFragment {
    EditText Label;
    EditText Model;
    EditText Cost;
    EditText Rent;
    Car c;
    UserInfoTask mAuthTask;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.editcar, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        view.findViewById(R.id.EditCarYes).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // When button is clicked, call up to owning activity.
                mAuthTask=new UserInfoTask();
                mAuthTask.execute((Void) null);
            }
        });
        view.findViewById(R.id.EditCarNo).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // When button is clicked, call up to owning activity.
                getDialog().cancel();
            }
        });
        return view;
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //getActivity().setTitle("Профиль");
        Label=getView().findViewById(R.id.EditCarLabel);
        Model=getView().findViewById(R.id.EditCarModel);
        Cost=getView().findViewById(R.id.EditCarCost);
        Rent=getView().findViewById(R.id.EditCarRent);
        c=((CarInfoActivity)getActivity()).car;
        Label.setText(c.Label);
        Model.setText(c.Model);
        Cost.setText(c.cost+"");
        Rent.setText(c.rentcost+"");

    }
    public class UserInfoTask extends AsyncTask<Void, Void, Boolean> {


        JSONObject obj=new JSONObject();
        UserInfoTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("login", URLEncoder.encode(((CarInfoActivity)getActivity()).login,"UTF-8"))
                        .add("password",URLEncoder.encode(((CarInfoActivity)getActivity()).password,"UTF-8"))
                        .add("id",c.id+"")
                        .add("label", URLEncoder.encode(Label.getText().toString(),"UTF-8"))
                        .add("model", URLEncoder.encode(Model.getText().toString(),"UTF-8"))
                        .add("cost",Cost.getText().toString())
                        .add("rentcost",Rent.getText().toString())
                        .build();
                Request request = new Request.Builder()
                        .url(InitialData.url+"/car")
                        .post(formBody)
                        .build();
                String serverAnswer="";
                try {
                    Response response = client.newCall(request).execute();
                    serverAnswer = response.body().string();
                    obj=new JSONObject();
                    if(serverAnswer!=null){
                        if(serverAnswer.equals("succ"))return true;
                    }
                    return false;
                }catch (Exception e){
                    e.printStackTrace();
                    return false;}
            }catch (Exception e){
                return false;
            }

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
//            ((MainActivity)getActivity()).showProgress(false);

            if (success) {
                ((CarInfoActivity)getActivity()).update();
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

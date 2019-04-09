package com.andrewkoloskov.northlord.Main;

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
import com.andrewkoloskov.northlord.Profile.EditprofileFragment;
import com.andrewkoloskov.northlord.Profile.profileFragment;
import com.andrewkoloskov.northlord.R;

import org.json.JSONObject;

import java.net.URLEncoder;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class AddCarDialog extends DialogFragment {
    UserInfoTask mAuthTask;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.add_car_dialog, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        view.findViewById(R.id.AddCarYes).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mAuthTask = new UserInfoTask();
                mAuthTask.execute((Void) null);
            }
        });
        view.findViewById(R.id.AddCarNo).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getDialog().cancel();
            }
        });
        return view;
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public class UserInfoTask extends AsyncTask<Void, Void, Boolean> {
        UserInfoTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {

                OkHttpClient client = new OkHttpClient();
                EditText label = getView().findViewById(R.id.AddCarLabel);
                EditText model = getView().findViewById(R.id.AddCarModel);
                EditText cost = getView().findViewById(R.id.AddCarCost);
                EditText rent = getView().findViewById(R.id.AddCarRent);
                RequestBody formBody = new FormBody.Builder()
                        .add("login", URLEncoder.encode(((MainActivity) getActivity()).Login,"UTF-8"))
                        .add("password",URLEncoder.encode(((MainActivity) getActivity()).pas,"UTF-8"))
                        .add("label",URLEncoder.encode(label.getText().toString(),"UTF-8"))
                        .add("model",URLEncoder.encode(model.getText().toString(),"UTF-8"))
                        .add("cost",URLEncoder.encode(cost.getText().toString()+"","UTF-8"))
                        .add("rentcost",URLEncoder.encode(rent.getText().toString()+"","UTF-8"))
                        .build();
                Request request = new Request.Builder()
                        .url(InitialData.url+"/car")
                        .put(formBody)
                        .build();
                String serverAnswer="";
                try {
                    Response response = client.newCall(request).execute();
                    serverAnswer = response.body().string();

                    if (serverAnswer != null) {
                        if (serverAnswer.equals("succ")) return true;
                    }
                    return false;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            } catch (Exception e) {
                return false;
            }

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            if (success) {
                ((main_page_fragment)((MainActivity)getActivity()).mainfrag).update();
                getDialog().dismiss();
            } else {
                Snackbar.make(getView(), "no connection", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
//            ((MainActivity)getActivity()).showProgress(false);
        }
    }
}

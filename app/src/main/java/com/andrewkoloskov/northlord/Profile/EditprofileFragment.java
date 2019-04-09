package com.andrewkoloskov.northlord.Profile;

import android.content.DialogInterface;
import android.content.UriMatcher;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.andrewkoloskov.northlord.R;

import org.json.JSONObject;

import java.net.URLEncoder;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditprofileFragment extends DialogFragment implements DialogInterface.OnClickListener,View.OnClickListener {
    String Name;
    String Surname;
    UserInfoTask mAuthTask;
    EditText nameedit;
    EditText surnameedit;
    EditText emailedit;
    String email;
    profileFragment fragment;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.edit_profile, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        view.findViewById(R.id.EditProfileYes).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // When button is clicked, call up to owning activity.
                mAuthTask=new UserInfoTask();
                mAuthTask.execute((Void) null);
            }
        });
        view.findViewById(R.id.EditProfileNo).setOnClickListener(new View.OnClickListener() {
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

        //Window window=getDialog().getWindow();
        //window.setBackgroundDrawableResource(R.color.background);
        ///getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //getDialog().getWindow().setDimAmount(0);
        fragment=(profileFragment) ((MainActivity)getActivity()).profile;
        Name=fragment.name1;
        Surname=fragment.surname1;
        email=fragment.email1;
        nameedit=((EditText)getView().findViewById(R.id.EditProfileName));nameedit.setText(Name);
        surnameedit=((EditText)getView().findViewById(R.id.EditProfileSurname));surnameedit.setText(Surname);
        emailedit=((EditText)getView().findViewById(R.id.EditProfileEmail));emailedit.setText(email);
        //((MainActivity)getActivity()).showProgress(false);

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        int i=0;
    }

    @Override
    public void onClick(View v) {
        int i=0;
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
                        .add("login", URLEncoder.encode( ((MainActivity)getActivity()).Login,"UTF-8"))
                        .add("password",URLEncoder.encode(((MainActivity)getActivity()).pas,"UTF-8"))
                        .add("email",URLEncoder.encode(emailedit.getText().toString(),"UTF-8"))
                        .add("name",URLEncoder.encode(nameedit.getText().toString(),"UTF-8"))
                        .add("surname",URLEncoder.encode(surnameedit.getText().toString(),"UTF-8"))
                        .build();
                Request request = new Request.Builder()
                        .url(InitialData.url+"/profile")
                        .put(formBody)
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
                fragment.update();
                getDialog().dismiss();
//                TextView Name=getActivity().findViewById(R.id.ProfileName);
//                TextView Surname=getActivity().findViewById(R.id.ProfileSurname);
//                TextView Money=getActivity().findViewById(R.id.ProfileMoney);
//                TextView Count=getActivity().findViewById(R.id.ProfileCount);
//                Name.setText(name);
//                Surname.setText(surname);
            } else {
                Snackbar.make(getView(), "no connection", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
//                Name.setText("name");
//                Surname.setText("sur");
            }
        }
        @Override
        protected void onCancelled() {
            mAuthTask = null;
//            ((MainActivity)getActivity()).showProgress(false);
        }
    }
}

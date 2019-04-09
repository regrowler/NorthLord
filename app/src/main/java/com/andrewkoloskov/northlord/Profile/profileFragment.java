package com.andrewkoloskov.northlord.Profile;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrewkoloskov.northlord.InitialData;
import com.andrewkoloskov.northlord.Main.MainActivity;
import com.andrewkoloskov.northlord.R;

import org.json.JSONObject;

import java.net.URLDecoder;
import java.net.URLEncoder;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class profileFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public UserInfoTask mAuthTask;
    String name1;
    String surname1;
    String email1;
    TextView Name;
    TextView Surname;
    TextView Money;
    TextView Count;
    private View mProgressView;
    private View mProfileFormView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        setInitialData();

        return inflater.inflate(R.layout.profile2, container, false);
    }
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //getActivity().setTitle("Профиль");
        String st="";
        getActivity().findViewById(R.id.editprofilebutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment d=new EditprofileFragment();
                d.show(getFragmentManager(),"d");
            }
        });
        mSwipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        Name=getActivity().findViewById(R.id.ProfileName);
        Surname=getActivity().findViewById(R.id.ProfileSurname);
        Money=getActivity().findViewById(R.id.ProfileMoney);
        Count=getActivity().findViewById(R.id.ProfileCount);
        mProfileFormView=getActivity().findViewById(R.id.login_form);
        mProgressView = getActivity().findViewById(R.id.login_progress);
        //((MainActivity)getActivity()).showProgress(false);
        update();

    }
    public void update(){
//        ((MainActivity)getActivity()).showProgress(true);
        mAuthTask = new UserInfoTask(((MainActivity)getActivity()).Login,((MainActivity)getActivity()).pas);
        mAuthTask.execute((Void) null);
    }

    @Override
    public void onRefresh() {
        update();
    }

    public class UserInfoTask extends AsyncTask<Void, Void, Boolean> {

        private final String login;
        private final String pas;
        private String name="";
        private String surname="";
        private int profit=0;
        private int cars=0;
        private String email="";
        JSONObject obj=new JSONObject();
        UserInfoTask(String log,String pas) {
            login=log;this.pas=pas;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                OkHttpClient client = new OkHttpClient();
//                RequestBody formBody = new FormBody.Builder()
//                        .add("login", URLEncoder.encode( mLogin,"UTF-8"))
//                        .add("password",URLEncoder.encode(mPassword,"UTF-8"))
//                        .build();
                Request request = new Request.Builder()
                        .url(InitialData.url+"/profile")
                        .get()
                        .addHeader("login",URLEncoder.encode( login,"UTF-8"))
                        .addHeader("password",URLEncoder.encode( pas,"UTF-8"))
                        .build();
                String serverAnswer="";
                try {
                    Response response = client.newCall(request).execute();
                    serverAnswer = response.body().string();
                    obj=new JSONObject();
                    if(serverAnswer!=null){
                        obj=new JSONObject(serverAnswer);
                    }
                    name= URLDecoder.decode(obj.getString("name"),"UTF-8");
                    surname=URLDecoder.decode(obj.getString("surname"),"UTF-8");
                    profit=obj.getInt("profit");
                    cars=obj.getInt("cars");
                    email=URLDecoder.decode(obj.getString("email"),"UTF-8");
                    return true;
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
            if (success) {
                TextView Name=getActivity().findViewById(R.id.ProfileName);
                //TextView Surname=getActivity().findViewById(R.id.ProfileSurname);
                TextView Money=getActivity().findViewById(R.id.ProfileMoney);
                TextView Count=getActivity().findViewById(R.id.ProfileCount);
                TextView Email=getActivity().findViewById(R.id.ProfileEmail);
//                ((MainActivity)getActivity()).showProgress(false);
                if(mSwipeRefreshLayout.isRefreshing())mSwipeRefreshLayout.setRefreshing(false);
                Name.setText(name+" "+surname);
                //Surname.setText(surname);
                Money.setText(profit+"");
                Count.setText(cars+"");
                Email.setText(email);
                name1=name;
                surname1=surname;
                email1=email;
                //showProgress(false);
            } else {
                Name.setText("name");
                Surname.setText("sur");
                Money.setText("money");
                Count.setText("count");
                //showProgress(false);
            }

        }
        @Override
        protected void onCancelled() {
            ((MainActivity)getActivity()).showProgress(false);
            mAuthTask = null;
            //((MainActivity)getActivity()).showProgress(false
            // );
        }
    }
}


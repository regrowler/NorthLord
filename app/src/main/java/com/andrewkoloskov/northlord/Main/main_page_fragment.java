package com.andrewkoloskov.northlord.Main;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.JsonReader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andrewkoloskov.northlord.CarWorker.Car;
import com.andrewkoloskov.northlord.InitialData;
import com.andrewkoloskov.northlord.ListView.Card;
import com.andrewkoloskov.northlord.ListView.RecyclerViewMain.Adapter;
import com.andrewkoloskov.northlord.ListView.RecyclerViewMain.DataAdapter;
import com.andrewkoloskov.northlord.ListView.RecyclerViewMain.LastRentDataAdapter;
import com.andrewkoloskov.northlord.R;
import com.andrewkoloskov.northlord.RentWorker.LastRent;
import com.andrewkoloskov.northlord.RentWorker.Rent;
import com.andrewkoloskov.northlord.ListView.RecyclerViewMain.RentDataAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class main_page_fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    UserInfoTask mAuthTask;
    ArrayList<Card> carlist;
    RecyclerView listView;
    Adapter dataAdapter;
    UpdateRentTask mRentTask;
    ArrayList<Card> rentlist;
    int m=0;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_page_fragment, container, false);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity) getActivity()).showProgress(true);
        ((MainActivity) getActivity()).fab.hide();
        carlist = new ArrayList<Card>();
        rentlist=new ArrayList<Card>();
        mSwipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        update();
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                Snackbar.make(view, "нажата позиция"+position, Snackbar.LENGTH_LONG)
////                        .setAction("Action", null).show();
//                ShowFragment();
//
//                MainActivity mainActivity=(MainActivity)getActivity();
////                mainActivity.showInfo(dataAdapter.getByPos(position).Label,dataAdapter.getByPos(position).Model,dataAdapter.getByPos(position).cost+"",dataAdapter.getByPos(position).rentcost+"",dataAdapter.getByPos(position).id);
//                mainActivity.hidemenu=false;
//                mainActivity.invalidateOptionsMenu();
//            }
//        });

    }
    public void showRents(){
        m=1;
        //update();
    }
    public void showMain(){
        m=0;
        //update();
    }
    public void showProfile(){m=2;}
    public void delete(){
        if(m==0){
            DeleteTask task=new DeleteTask();
            task.execute((Void)null);
        }else if(m==1){
            DeleteRentTask task=new DeleteRentTask();
            task.execute((Void)null);
        }

    }
    public void update(){
        if(((MainActivity)getActivity())!=null&m!=2){
            ((MainActivity)getActivity()).hidemenu=true;
            ((MainActivity)getActivity()).invalidateOptionsMenu();
            if(m==0){
                mAuthTask = new UserInfoTask();
                mAuthTask.execute((Void) null);
            }else if(m==1){
                mRentTask=new UpdateRentTask(((MainActivity) getActivity()).Login,((MainActivity) getActivity()).pas);
                mRentTask.execute((Void)null);
            }
        }


    }
    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Отменяем анимацию обновления
                update();
            }
        }, 0);
    }

    public class UserInfoTask extends AsyncTask<Void, Void, Boolean> {

        JSONArray array;
        JSONObject obj;

        UserInfoTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(InitialData.url+"/car")
                        .get()
                        .addHeader("login", URLEncoder.encode( ((MainActivity) getActivity()).Login,"UTF-8"))
                        .addHeader("password",URLEncoder.encode(((MainActivity) getActivity()).pas,"UTF-8"))
                        .build();
                String serverAnswer = "";
                try {
                    Response response = client.newCall(request).execute();
                    serverAnswer = response.body().string();
                    if (!serverAnswer.equals("")) {
                        carlist.clear();
                        if(serverAnswer.equals("fail"))return true;
                        array = new JSONArray(serverAnswer);
                        for (int i = array.length()-1; i >=0; i--) {
                            obj = new JSONObject(array.getString(i));
                            carlist.add(new Car(obj.getInt("id"), URLDecoder.decode(obj.getString("label"),"UTF-8"),URLDecoder.decode( obj.getString("model"),"UTF-8"), obj.getInt("cost"), obj.getInt("rent")));
                            int y = 0;
                        }
                        return true;
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
            ((MainActivity) getActivity()).showProgress(false);
            if (success) {
                if(mSwipeRefreshLayout.isRefreshing())mSwipeRefreshLayout.setRefreshing(false);
                ((MainActivity)getActivity()).fab.show();
                ((MainActivity)getActivity()).hidemenu=false;
                ((MainActivity)getActivity()).invalidateOptionsMenu();
                listView = (RecyclerView) (getActivity()).findViewById(R.id.list_item);
                dataAdapter = new DataAdapter(getContext(), carlist,((MainActivity) getActivity()).Login,((MainActivity) getActivity()).pas);
                listView.setAdapter(dataAdapter);
            } else {
                if(mSwipeRefreshLayout.isRefreshing())mSwipeRefreshLayout.setRefreshing(false);
                Snackbar.make(getView(), "no connection", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            //((MainActivity)getActivity()).showProgress(false);
        }
    }
    public class DeleteTask extends AsyncTask<Void, Void, Boolean> {

        JSONArray array;
        JSONObject obj;

        DeleteTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                OkHttpClient client = new OkHttpClient();
                ArrayList<Integer> ids=(ArrayList)dataAdapter.getSelected();
                JSONArray array=new JSONArray();
                for(int i=0;i<ids.size();i++){
                    array.put(ids.get(i));
                }
                Request request = new Request.Builder()
                        .url(InitialData.url+"/car")
                        .delete()
                        .addHeader("login", URLEncoder.encode( ((MainActivity) getActivity()).Login,"UTF-8"))
                        .addHeader("password",URLEncoder.encode(((MainActivity) getActivity()).pas,"UTF-8"))
                        .addHeader("mass",URLEncoder.encode(array.toString(),"UTF-8"))
                        .build();
                String serverAnswer = "";
                try {
                    Response response = client.newCall(request).execute();
                    serverAnswer = response.body().string();
                    if (serverAnswer.equals("succ")) {
                        return true;
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
                update();
            } else {
                Snackbar.make(getView(), "no connection", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            //((MainActivity)getActivity()).showProgress(false);
        }
    }
    public class DeleteRentTask extends AsyncTask<Void, Void, Boolean> {

        JSONArray array;
        JSONObject obj;

        DeleteRentTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                OkHttpClient client = new OkHttpClient();
                ArrayList<JSONObject> ids=(ArrayList)((LastRentDataAdapter)dataAdapter).getSelectedRents();
                JSONArray array=new JSONArray();
                for(int i=0;i<ids.size();i++){

                    array.put(ids.get(i).toString());
                }
                Request request = new Request.Builder()
                        .url(InitialData.url+"/rent")
                        .delete()
                        .addHeader("login", URLEncoder.encode( ((MainActivity) getActivity()).Login,"UTF-8"))
                        .addHeader("password",URLEncoder.encode(((MainActivity) getActivity()).pas,"UTF-8"))
                        .addHeader("mass",URLEncoder.encode(array.toString(),"UTF-8"))
                        .build();
                String serverAnswer = "";
                try {
                    Response response = client.newCall(request).execute();
                    serverAnswer = response.body().string();
                    if (serverAnswer.equals("succ")) {
                        return true;
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
                update();
            } else {
                Snackbar.make(getView(), "no connection", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            //((MainActivity)getActivity()).showProgress(false);
        }
    }
    public class UpdateRentTask extends AsyncTask<Void, Void, Boolean> {

        private final String login;
        private final String pas;
        JSONObject obj=new JSONObject();
        UpdateRentTask(String log,String pas) {
            login=log;this.pas=pas;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(InitialData.url+"/rent")
                        .get()
                        .addHeader("login", URLEncoder.encode( login,"UTF-8"))
                        .addHeader("password",URLEncoder.encode(pas,"UTF-8"))
                        .build();
                String serverAnswer="";
                JSONArray array;
                try {
                    Response response = client.newCall(request).execute();
                    serverAnswer = response.body().string();
                    if (!serverAnswer.equals("")) {
                        rentlist.clear();
                        if(serverAnswer.equals("fail"))return true;
                        array = new JSONArray(URLDecoder.decode(serverAnswer,"UTF-8"));
                        for (int i = array.length()-1; i >=0; i--) {
                            obj = new JSONObject(array.getString(i));
                            String name= URLDecoder.decode(obj.getString("name"),"UTF-8");
                            String startdate=obj.getString("startdate");
                            String starttime=obj.getString("starttime");
                            String enddate=obj.getString("enddate");
                            String endtime=obj.getString("endtime");
                            String label=obj.getString("label");
                            String model=obj.getString("model");
                            int cost=obj.getInt("cost");
                            String[] sd=startdate.split(" ");
                            String[] st=starttime.split(" ");
                            int ids=obj.getInt("id");
                            String[] ed=enddate.split(" ");
                            String[] et=endtime.split(" ");
                            GregorianCalendar s=new GregorianCalendar();
                            s.set(Integer.parseInt(sd[2]),Integer.parseInt(sd[1]),Integer.parseInt(sd[0]),Integer.parseInt(st[0]),Integer.parseInt(st[1]));
                            GregorianCalendar e=new GregorianCalendar();
                            e.set(Integer.parseInt(ed[2]),Integer.parseInt(ed[1]),Integer.parseInt(ed[0]),Integer.parseInt(et[0]),Integer.parseInt(et[1]));
                            LastRent r=new LastRent(name,s,e,cost,ids,label,model);
                            rentlist.add(r);
                        }
                        return true;
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
            mRentTask= null;
            if (success) {
                ((MainActivity)getActivity()).showProgress(false);
                ((MainActivity)getActivity()).hidemenu=false;
                ((MainActivity)getActivity()).invalidateOptionsMenu();
                if(mSwipeRefreshLayout.isRefreshing())mSwipeRefreshLayout.setRefreshing(false);
                listView=getActivity().findViewById(R.id.list_item);
                dataAdapter=new LastRentDataAdapter(getContext(),rentlist,getFragmentManager());
                listView.setAdapter(dataAdapter);

            } else {
                if(mSwipeRefreshLayout.isRefreshing())mSwipeRefreshLayout.setRefreshing(false);
                Snackbar.make(getView(), "err", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

        }
        @Override
        protected void onCancelled() {
//            ((MainActivity)getActivity()).showProgress(false);
            mRentTask = null;
            //((MainActivity)getActivity()).showProgress(false
            // );
        }
    }
}

package com.andrewkoloskov.northlord.RentWorker;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andrewkoloskov.northlord.InitialData;
import com.andrewkoloskov.northlord.ListView.Card;
import com.andrewkoloskov.northlord.ListView.RecyclerViewMain.RentDataAdapter;
import com.andrewkoloskov.northlord.Main.MainActivity;
import com.andrewkoloskov.northlord.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LastRentsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    public String login;
    public String password;
    ArrayList<Card> rentlist;
    RecyclerView recyclerRent;
    private UpdateRentTask mRentTask;
    RentDataAdapter dataAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.last_rents_frag, container, false);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity) getActivity()).showProgress(true);
        ((MainActivity) getActivity()).fab.hide();
        rentlist = new ArrayList<Card>();
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
    void update(){
        mRentTask=new UpdateRentTask(((MainActivity)getActivity()).Login,((MainActivity)getActivity()).pas);
        mRentTask.execute((Void)null);
    }

    @Override
    public void onRefresh() {
        update();
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
                            Rent r=new Rent(name,s,e,cost,ids);
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
                if(mSwipeRefreshLayout.isRefreshing())mSwipeRefreshLayout.setRefreshing(false);
                recyclerRent=getActivity().findViewById(R.id.list_rent);
                dataAdapter=new RentDataAdapter(getContext(),rentlist,getFragmentManager());
                recyclerRent.setAdapter(dataAdapter);

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

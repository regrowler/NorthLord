package com.andrewkoloskov.northlord.CarWorker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.andrewkoloskov.northlord.InitialData;
import com.andrewkoloskov.northlord.ListView.Card;
import com.andrewkoloskov.northlord.ListView.RecyclerViewMain.LastRentDataAdapter;
import com.andrewkoloskov.northlord.Main.MainActivity;
import com.andrewkoloskov.northlord.R;
import com.andrewkoloskov.northlord.RentWorker.AddRent;
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

public class CarInfoActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    public Car car;
    TextView Label;
    TextView Model;
    TextView Cost;
    TextView Rent;
    public String login;
    public String password;
    ArrayList<Card> rentlist;
    RecyclerView recyclerRent;
    View ac;
    Context context;
    RentDataAdapter dataAdapter;
    private UpdateInfoTask mAuthTask;
    private UpdateRentTask mRentTask;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_info);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        rentlist=new ArrayList<>();
        Intent i=getIntent();
        String ids=i.getStringExtra("id");
        String label=i.getStringExtra("label");
        String model=i.getStringExtra("model");
        String costs=i.getStringExtra("cost");
        String rents=i.getStringExtra("rent");
        login =i.getStringExtra("login");
        password=i.getStringExtra("pas");
        int id=Integer.parseInt(ids);
        int cost=Integer.parseInt(costs);
        int rent=Integer.parseInt(rents);
        context=this;
        car=new Car(id,
                label,
                model,
                cost,
                rent);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFragment=new AddRent();
                dialogFragment.show(getSupportFragmentManager(),"d");
//                Snackbar.make(view, "аренда", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab2);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFragment=new EditCar();
                dialogFragment.show(getSupportFragmentManager(),"d");
//                Snackbar.make(view, "edit", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
        FloatingActionButton fab2=findViewById(R.id.del);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertDialog=new AlertDialog.Builder(context);
                alertDialog.setTitle("Отменить сделки?");
                alertDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteRentTask d=new DeleteRentTask();
                        d.execute((Void)null);
                    }
                });
                alertDialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.show();

            }
        });
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        Label=findViewById(R.id.CarInfoLabel);
        Model=findViewById(R.id.CarInfoModel);
        Cost=findViewById(R.id.CarInfoCost);
        Rent=findViewById(R.id.CarInfoRent);
        Label.setText(car.Label);
        Model.setText(" "+car.Model);
        Cost.setText(""+car.cost);
        Rent.setText(""+car.rentcost);
        updatelist();
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Отменяем анимацию обновления
                update();
                updatelist();
            }
        }, 0);
    }
    public void update(){
        mAuthTask=new UpdateInfoTask(login,password);
        mAuthTask.execute((Void)null);
    }
    public void updatelist(){
        mRentTask=new UpdateRentTask(login,password);
        mRentTask.execute((Void)null);
    }
    public class UpdateInfoTask extends AsyncTask<Void, Void, Boolean> {

        private final String login;
        private final String pas;
        private String label="";
        private String model="";
        private int cost=0;
        private int rent=0;
        private String email="";
        JSONObject obj=new JSONObject();
        UpdateInfoTask(String log,String pas) {
            login=log;this.pas=pas;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(InitialData.url+"/car")
                        .get()
                        .addHeader("login", URLEncoder.encode( login,"UTF-8"))
                        .addHeader("password",URLEncoder.encode(pas,"UTF-8"))
                        .addHeader("id",URLEncoder.encode(car.id+"","UTF-8"))
                        .build();
                String serverAnswer="";
                try {
                    Response response = client.newCall(request).execute();
                    serverAnswer = response.body().string();
                    obj=new JSONObject();
                    if(serverAnswer!=null){
                        obj=new JSONObject(serverAnswer);
                    }
                    label=URLDecoder.decode(obj.getString("label"),"UTF-8");
                    model=URLDecoder.decode(obj.getString("model"),"UTF-8");
                    cost=obj.getInt("cost");
                    rent=obj.getInt("rent");
                    car=new Car(car.id,label,model,cost,rent);
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
              if(mSwipeRefreshLayout.isRefreshing())mSwipeRefreshLayout.setRefreshing(false);
                Label.setText(label);
                Model.setText(" "+model);
                Cost.setText(""+cost);
                Rent.setText(""+rent);
            } else {
                if(mSwipeRefreshLayout.isRefreshing())mSwipeRefreshLayout.setRefreshing(false);
                Snackbar.make(ac, "edit", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

        }
        @Override
        protected void onCancelled() {
//            ((MainActivity)getActivity()).showProgress(false);
            mAuthTask = null;
            //((MainActivity)getActivity()).showProgress(false
            // );
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
                        .addHeader("id",URLEncoder.encode(car.id+"","UTF-8"))
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
                            int ids=obj.getInt("id");
                            int cost=obj.getInt("cost");
                            String[] sd=startdate.split(" ");
                            String[] st=starttime.split(" ");
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
            mAuthTask = null;
            if (success) {
                if(mSwipeRefreshLayout.isRefreshing())mSwipeRefreshLayout.setRefreshing(false);
                recyclerRent=findViewById(R.id.list_rent);
                dataAdapter=new RentDataAdapter(context,rentlist,getSupportFragmentManager());
                recyclerRent.setAdapter(dataAdapter);

            } else {
                if(mSwipeRefreshLayout.isRefreshing())mSwipeRefreshLayout.setRefreshing(false);
//                Snackbar.make(, "edit", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }

        }
        @Override
        protected void onCancelled() {
//            ((MainActivity)getActivity()).showProgress(false);
            mAuthTask = null;
            //((MainActivity)getActivity()).showProgress(false
            // );
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
                ArrayList<JSONObject> ids=(ArrayList)((RentDataAdapter)dataAdapter).getSelectedRents();
                JSONArray array=new JSONArray();
                for(int i=0;i<ids.size();i++){

                    array.put(ids.get(i).toString());
                }
                Request request = new Request.Builder()
                        .url(InitialData.url+"/rent")
                        .delete()
                        .addHeader("login", URLEncoder.encode( login,"UTF-8"))
                        .addHeader("password",URLEncoder.encode(password,"UTF-8"))
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
                updatelist();
            } else {
//                Snackbar.make(context, "no connection", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }

        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            //((MainActivity)getActivity()).showProgress(false);
        }
    }


}

package com.andrewkoloskov.northlord.ListView.RecyclerViewMain;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andrewkoloskov.northlord.ListView.Card;
import com.andrewkoloskov.northlord.R;
import com.andrewkoloskov.northlord.RentWorker.LastRent;
import com.andrewkoloskov.northlord.RentWorker.Rent;
import com.andrewkoloskov.northlord.RentWorker.UpdateRent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LastRentDataAdapter extends Adapter {
    private final LayoutInflater inflater;
    public final List<Card> values;
    private Context context;
    FragmentManager fragmentManager;
    public LastRentDataAdapter(Context context, List<Card> rents,FragmentManager fragmentManager) {
        this.values = rents;
        this.inflater = LayoutInflater.from(context);
        this.fragmentManager=fragmentManager;
    }

    @NonNull
    @Override
    public ViewH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.rent_item_last, viewGroup, false);
        return new LastRentDataAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewH viewHolder, int i) {
        final LastRent r = (LastRent) values.get(i);
        final Card card=values.get(i);
        ViewHolder viewHolder1=(ViewHolder)viewHolder;
        viewHolder1.name.setText(r.name);
        viewHolder1.label.setText(r.label);
        viewHolder1.model.setText(" "+r.model);
        viewHolder1.start.setText(r.start.get(Calendar.DAY_OF_MONTH) + "." + r.start.get(Calendar.MONTH) + "." + r.start.get(Calendar.YEAR) + " " + r.start.get(Calendar.HOUR_OF_DAY) + ":" + r.start.get(Calendar.MINUTE));
        viewHolder1.end.setText(r.end.get(Calendar.DAY_OF_MONTH) + "." + r.end.get(Calendar.MONTH) + "." + r.end.get(Calendar.YEAR) + " " + r.end.get(Calendar.HOUR_OF_DAY) + ":" + r.end.get(Calendar.MINUTE));
        viewHolder1.cost.setText(r.cost+"");
        viewHolder.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                card.checked=!card.checked;
            }
        });
        viewHolder1.gen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args=new Bundle();
                args.putString("name",r.name);
                args.putInt("id",r.id);
                double re=r.end.getTimeInMillis()-r.start.getTimeInMillis();
                re/=1000;
                re/=3600;
                re=r.cost/re;
                int rent=(int)Math.round(re);
                args.putInt("rent",rent);
                DialogFragment dialogFragment=new UpdateRent();
                dialogFragment.setArguments(args);
                dialogFragment.show(fragmentManager,"d");
            }
        });
    }

    @Override
    public List<Integer> getSelected() {
        List<Integer> selected=new ArrayList<>();
        for(int i=0;i<values.size();i++){
            if(values.get(i).checked){
                selected.add(values.get(i).id());
            }
        }
        return selected;
    }
    public List<JSONObject> getSelectedRents(){
        List<JSONObject> selected=new ArrayList<>();
        for(int i=0;i<values.size();i++){
            if(values.get(i).checked){
                JSONObject object=new JSONObject();
                try {
                    object.put("id",values.get(i).id());
                    object.put("cost",((LastRent)values.get(i)).cost);
                    selected.add(object);
                }catch (Exception e){e.printStackTrace();}

            }
        }
        return selected;
    }


    @Override
    public int getItemCount() {
        return values.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        context = recyclerView.getContext();
    }
    public class ViewHolder extends ViewH {

        final TextView name,start,end,cost,label,model;
        RelativeLayout gen;
        ViewHolder(View view) {
            super(view);
            gen=view.findViewById(R.id.itemgen);
            super.check = view.findViewById(R.id.itemcheck);
            name = view.findViewById(R.id.rentitemName);
            start = view.findViewById(R.id.rentitemStartDate);
            end = view.findViewById(R.id.rentitemEndDate);
            cost = view.findViewById(R.id.rentitemCost);
            label=view.findViewById(R.id.rentitemLabel);
            model=view.findViewById(R.id.rentitemModel);
        }
    }
}

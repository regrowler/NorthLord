package com.andrewkoloskov.northlord.ListView.RecyclerViewMain;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andrewkoloskov.northlord.CarWorker.Car;
import com.andrewkoloskov.northlord.CarWorker.CarInfoActivity;
import com.andrewkoloskov.northlord.ListView.Card;
import com.andrewkoloskov.northlord.R;

import java.util.ArrayList;
import java.util.List;

public class DataAdapter extends Adapter{
    private final LayoutInflater inflater;
    private final List<Card> values;
    private Context context;
    public String login;
    public String pas;
    public DataAdapter(Context context, List<Card> phones,String log,String pass) {
        this.values = phones;
        this.inflater = LayoutInflater.from(context);
        login=log;
        pas=pass;
    }

    @NonNull
    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item, viewGroup, false);
        return new ViewHolder(view);
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        context = recyclerView.getContext();
    }
    @Override
    public void onBindViewHolder(@NonNull ViewH viewHolder, int i) {
        final Card card = values.get(i);
        final Car car=(Car)card;
        ViewHolder viewHolder1 =(ViewHolder)viewHolder;
        viewHolder.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                card.checked=!card.checked;
            }
        });
        viewHolder1.gen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(context,CarInfoActivity.class);
                i.putExtra("label",car.Label);
                i.putExtra("model",car.Model);
                i.putExtra("cost",car.cost+"");
                i.putExtra("rent",car.rentcost+"");
                i.putExtra("id",car.id+"");
                i.putExtra("login",login);
                i.putExtra("pas",pas);
                context.startActivity(i);
            }
        });
        viewHolder1.label.setText(car.Label);
        viewHolder1.model.setText(" "+car.Model);
        viewHolder1.rent.setText(""+car.rentcost);

    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public class ViewHolder extends ViewH {

        final TextView label, model, rent;
        final RelativeLayout gen;


        ViewHolder(View view) {
            super(view);
            gen=view.findViewById(R.id.itemgeneral);
            super.check = view.findViewById(R.id.itemcheck);
            label = view.findViewById(R.id.itemlabel);
            model = view.findViewById(R.id.itemmodel);
            rent = view.findViewById(R.id.itemrent);
        }
    }
    public List<Integer> getSelected(){
        List<Integer> selected=new ArrayList<>();
        for(int i=0;i<values.size();i++){
            if(values.get(i).checked){
                selected.add(values.get(i).id());
            }
        }
        return selected;
    }
}

package com.andrewkoloskov.northlord.CarWorker;

import android.widget.ListView;

import com.andrewkoloskov.northlord.ListView.Card;

public class Car extends Card {

    public int getId() {
        return id;
    }

    public String getLabel() {
        return Label;
    }

    public String getModel() {
        return Model;
    }

    public int getCost() {
        return cost;
    }

    public int getRentcost() {
        return rentcost;
    }
    public boolean isChecked(){return super.checked;}
    public void setChecked(boolean in){super.checked=in;}
    public String Label;
    public String Model;
    public int cost;
    public int rentcost;
    public int id;
    public Car(int id,String label,String model,int cost,int rentcost){
        this.id=id;
        Label=label;
        Model=model;
        this.cost=cost;
        this.rentcost=rentcost;
    }

    @Override
    public int id() {
        return id;
    }
}
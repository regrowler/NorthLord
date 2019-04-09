package com.andrewkoloskov.northlord.RentWorker;

import com.andrewkoloskov.northlord.ListView.Card;

import java.util.GregorianCalendar;

public class Rent extends Card{
    public int id;
    public String name;
    public GregorianCalendar start;
    public GregorianCalendar end;
    public int cost;
    public boolean isChecked(){return super.checked;}
    public void setChecked(boolean in){super.checked=in;}

    @Override
    public int id() {
        return id;
    }

    public Rent(){ }
    public Rent(String name){this.name=name;}
    public Rent(String name,GregorianCalendar start,GregorianCalendar end){this.start=start;this.end=end;this.name=name; }
    public Rent(String name,GregorianCalendar start,GregorianCalendar end,int cost,int id){this.start=start;this.end=end;this.cost=cost;this.name=name;this.id=id; }
}

package com.andrewkoloskov.northlord.ListView.RecyclerViewMain;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.andrewkoloskov.northlord.ListView.Card;

import java.util.List;

public abstract class Adapter extends RecyclerView.Adapter<Adapter.ViewH> {
    public abstract List<Integer> getSelected();
    public abstract int getItemCount();
    @NonNull
    @Override
    public abstract ViewH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i);

    @Override
    public void onBindViewHolder(@NonNull ViewH viewHolder, int i) {

    }
    public abstract class ViewH extends RecyclerView.ViewHolder{
        CheckBox check=null;
        public ViewH(@NonNull android.view.View itemView) {
            super(itemView);
        }
    }
}

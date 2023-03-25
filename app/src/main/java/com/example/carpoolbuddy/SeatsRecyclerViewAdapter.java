package com.example.carpoolbuddy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SeatsRecyclerViewAdapter extends RecyclerView.Adapter<SeatsRecyclerViewAdapter.MyViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;

    Context context;
    List<String> seats=new ArrayList<>();

    public SeatsRecyclerViewAdapter(Context context, List<String> seats, RecyclerViewInterface recyclerViewInterface){
        this.context = context;
        if(seats!=null) this.seats = seats;
        this.recyclerViewInterface=recyclerViewInterface;
    }

    @NonNull
    @Override
    public SeatsRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.seats_recycler_view_row, parent, false);
        return new SeatsRecyclerViewAdapter.MyViewHolder(view, recyclerViewInterface);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull SeatsRecyclerViewAdapter.MyViewHolder holder, int position) {

        holder.name.setText(seats.get(position));

    }

    @Override
    public int getItemCount() {

        return seats.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView name;

        public MyViewHolder(@NonNull View itemView, final RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            name = itemView.findViewById(R.id.name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerViewInterface != null){
                        int position = getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClick(position);
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View view) {
                    if(recyclerViewInterface != null){
                        int position = getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemLongClick(position);
                        }
                    }
                    return true;
                }
            });
        }
    }
}

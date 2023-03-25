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

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;

    Context context;
    ArrayList<Vehicle> vehicles=new ArrayList<>();

    public RecyclerViewAdapter(Context context, ArrayList<Vehicle> vehicles, RecyclerViewInterface recyclerViewInterface){
        this.context = context;
        if(vehicles!=null) this.vehicles = vehicles;
        this.recyclerViewInterface=recyclerViewInterface;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_row, parent, false);
        return new RecyclerViewAdapter.MyViewHolder(view, recyclerViewInterface);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.MyViewHolder holder, int position) {

        holder.name.setText(vehicles.get(position).getModel());

        if(!vehicles.get(position).isOpen())holder.seats.setText("CLOSED");
        else{holder.seats.setText("Seats: "+Integer.toString(vehicles.get(position).getCapacity()));}
        holder.type.setText("Type: "+vehicles.get(position).getVehicleType());
        holder.price.setText("Price: $"+Double.toString(vehicles.get(position).getBasePrice()));
        holder.driver.setText("Driver: "+vehicles.get(position).getOwner());

    }

    @Override
    public int getItemCount() {

        return vehicles.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView name, seats, type, price, driver;

        public MyViewHolder(@NonNull View itemView, final RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            seats = itemView.findViewById(R.id.seats);
            price = itemView.findViewById(R.id.price);
            type = itemView.findViewById(R.id.type);
            driver = itemView.findViewById(R.id.param);

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

package com.example.parking.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parking.R;
import com.example.parking.RecycleciewStructures.ParkingHistoryStructure;

import java.util.ArrayList;

public class ParkingHistoryAdapter extends RecyclerView.Adapter<ParkingHistoryAdapter.ViewHolder> {

    Context context;

    ArrayList<ParkingHistoryStructure>parkinghistory;

    public ParkingHistoryAdapter(Context context, ArrayList<ParkingHistoryStructure> parkinghistory) {
        this.context = context;
        this.parkinghistory = parkinghistory;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View item= LayoutInflater.from(context).inflate(R.layout.recycleview_parkinghistory,parent,false);

        ViewHolder viewHolder=new ViewHolder(item);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ParkingHistoryStructure historyItem = parkinghistory.get(position);

        holder.parkingStationNameTextView.setText(historyItem.getParkingstationname());
        holder.parkingDateTextView.setText(historyItem.getDate());
        holder.amountPaidTextView.setText(String.valueOf(historyItem.getAmount()));

    }

    @Override
    public int getItemCount() {
        return parkinghistory.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView parkingStationNameTextView;
        TextView parkingDateTextView;
        TextView amountPaidTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parkingStationNameTextView = itemView.findViewById(R.id.parkingstationname);
            parkingDateTextView = itemView.findViewById(R.id.parkingdate);
            amountPaidTextView = itemView.findViewById(R.id.amountpaid);
        }
    }
}

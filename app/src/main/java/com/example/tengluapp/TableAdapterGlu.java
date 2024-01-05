package com.example.tengluapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class TableAdapterGlu extends RecyclerView.Adapter<TableAdapterGlu.TableViewHolder> {

    private List<MyDataModelGlu> mDataGlu;

    public TableAdapterGlu(List<MyDataModelGlu> mDataGlu) {

        this.mDataGlu = mDataGlu;
    }

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout2, parent, false);
        return new TableViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
        MyDataModelGlu dataModel = mDataGlu.get(position);
        holder.datePrint.setText(dataModel.getDate());
        holder.hourPrint.setText(dataModel.getHour());
        holder.TDG.setText(dataModel.getTDG());
    }

    @Override
    public int getItemCount() {

        return mDataGlu.size();
    }

    public class TableViewHolder extends RecyclerView.ViewHolder {

        TextView datePrint;
        TextView hourPrint;
        TextView TDG;

        public TableViewHolder(@NonNull View itemView) {
            super(itemView);
            datePrint = itemView.findViewById(R.id.datePrint);
            hourPrint = itemView.findViewById(R.id.hourPrint);
            TDG = itemView.findViewById(R.id.TDG);
        }
    }
}

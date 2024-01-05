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
import java.util.Objects;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableViewHolder> {

    private List<MyDataModel> mData;

    public TableAdapter(List<MyDataModel> mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new TableViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
        MyDataModel dataModel = mData.get(position);
        holder.datePrint.setText(dataModel.getDate());
        holder.hourPrint.setText(dataModel.getHour());
        holder.SYSPrint.setText(dataModel.getSYS());
        holder.DIAPrint.setText(dataModel.getDIA());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class TableViewHolder extends RecyclerView.ViewHolder {

        TextView datePrint;
        TextView hourPrint;
        TextView SYSPrint;
        TextView DIAPrint;

        public TableViewHolder(@NonNull View itemView) {
            super(itemView);
            datePrint = itemView.findViewById(R.id.datePrint);
            hourPrint = itemView.findViewById(R.id.hourPrint);
            SYSPrint = itemView.findViewById(R.id.SYSPrint);
            DIAPrint = itemView.findViewById(R.id.DIAPrint);

        }
    }

}



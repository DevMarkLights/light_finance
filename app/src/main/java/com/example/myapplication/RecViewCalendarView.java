package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class RecViewCalendarView extends  RecyclerView.Adapter<RecViewCalendarView.MyCalendarViewHolder>{
    private final RecyclerViewInterface recyclerViewInterface;
    private static Context context = null;
    private final ArrayList symbol;
    private final ArrayList date;
    private final ArrayList frequency;
    private final ArrayList dividendAmount;
    private final ArrayList annualDividendAmount;
    DecimalFormat formatter = new DecimalFormat("#,###.00");

    public RecViewCalendarView(Context context, ArrayList symbol, ArrayList date, ArrayList frequency, ArrayList dividendAmount,
                               ArrayList annualDividendAmount,RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.symbol = symbol;
        this.date = date;
        this.frequency = frequency;
        this.dividendAmount = dividendAmount;
        this.annualDividendAmount = annualDividendAmount;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public MyCalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflator = LayoutInflater.from(context);
        View view = inflator.inflate(R.layout.calendar_view_rec_view,parent,false);
        return new MyCalendarViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MyCalendarViewHolder holder, int position) {
        holder.symbol.setText(String.valueOf(symbol.get(position)));
        holder.dateView.setText((CharSequence) date.get(position));
        holder.frequencyView.setText((CharSequence) frequency.get(position));
        holder.dividendAmount.setText(String.valueOf(dividendAmount.get(position)));
        holder.annualDividendAmountView.setText(String.format("$%s", annualDividendAmount.get(position)));
    }


    @Override
    public int getItemCount() {
        return symbol.size();
    }

    public class MyCalendarViewHolder extends RecyclerView.ViewHolder {
        TextView symbol,dateView,frequencyView,dividendAmount,annualDividendAmountView;

        public MyCalendarViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            symbol = itemView.findViewById(R.id.symbol);
            dateView = itemView.findViewById(R.id.dateView);
            frequencyView = itemView.findViewById(R.id.frequencyView);
            dividendAmount=itemView.findViewById(R.id.amountOfDividendView);
            annualDividendAmountView=itemView.findViewById(R.id.annualDividendAmountView);

            itemView.setOnClickListener(view -> {
                if (recyclerViewInterface != null){
                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        recyclerViewInterface.onItemClick(position);
                    }
                }
            });
        }
    }
}

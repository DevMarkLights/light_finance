package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecViewAdapter extends RecyclerView.Adapter<RecViewAdapter.MyViewHolder>{

   private final Context context;
    private final ArrayList symbol;
    private final ArrayList price;
    private final ArrayList profit_loss;
    private final ArrayList average_cost;
    private final ArrayList dividend_Yield;
    private final ArrayList marketValue;
    private final ArrayList frequency;

    RecViewAdapter(Context context, ArrayList symbol, ArrayList price, ArrayList profit_loss,
                   ArrayList average_cost, ArrayList dividend_Yield, ArrayList marketValue,
                   ArrayList frequency) {
        this.context = context;
        this.symbol = symbol;
        this.price = price;
        this.profit_loss = profit_loss;
        this.average_cost = average_cost;
        this.dividend_Yield = dividend_Yield;
        this.marketValue = marketValue;
        this.frequency = frequency;
    }




    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflator = LayoutInflater.from(context);
        View view = inflator.inflate(R.layout.portfolio_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecViewAdapter.MyViewHolder holder, int position) {
        holder.symbol.setText(String.valueOf(symbol.get(position)));
        holder.price.setText("$"+ price.get(position));
        holder.average_cost.setText(String.valueOf(average_cost.get(position)));
        holder.profit_loss.setText(String.valueOf(profit_loss.get(position)));
        holder.marketValue.setText("$"+ marketValue.get(position));
        holder.dividend_Yield.setText(dividend_Yield.get(position) + "%");
        holder.frequency.setText(String.valueOf(frequency.get(position)));
    }

    @Override
    public int getItemCount() {
        return symbol.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView symbol,price,average_cost,profit_loss,marketValue,dividend_Yield,frequency;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            symbol = itemView.findViewById(R.id.symbol);
            price = itemView.findViewById(R.id.price);
            average_cost = itemView.findViewById(R.id.averageCost);
            profit_loss = itemView.findViewById(R.id.profit_loss);
            marketValue = itemView.findViewById(R.id.marketValue);
            dividend_Yield = itemView.findViewById(R.id.dividend_Yield);
            frequency = itemView.findViewById(R.id.frequency);
        }
    }
}

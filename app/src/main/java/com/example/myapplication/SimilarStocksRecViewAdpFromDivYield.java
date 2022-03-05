package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SimilarStocksRecViewAdpFromDivYield extends RecyclerView.Adapter<SimilarStocksRecViewAdpFromDivYield.MyViewSimStocksHolder>{
    private final RecyclerViewInterface recyclerViewInterface;
    private final Context context;
    private final ArrayList symbols;
    private final ArrayList price;
    private final ArrayList price_change_percent_ytd;
    private final ArrayList dividend_yield_percent;
    private final ArrayList dividend_rate_Annual;

    SimilarStocksRecViewAdpFromDivYield(Context context, ArrayList symbols, ArrayList price, ArrayList price_change_percent_ytd,
                                        ArrayList dividend_yield_percent, ArrayList dividend_rate_annual,
                                        RecyclerViewInterface recyclerViewInterface) {
        this.recyclerViewInterface = recyclerViewInterface;
        this.context = context;
        this.symbols = symbols;
        this.price = price;
        this.price_change_percent_ytd = price_change_percent_ytd;
        this.dividend_yield_percent = dividend_yield_percent;
        this.dividend_rate_Annual = dividend_rate_annual;
    }

    @NonNull
    @Override
    public MyViewSimStocksHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflator = LayoutInflater.from(context);
        View view = inflator.inflate(R.layout.similar_stocks_rec_view_from_div_yield,parent,false);
        return new MyViewSimStocksHolder(view, recyclerViewInterface);
    }


    @Override
    public void onBindViewHolder(@NonNull SimilarStocksRecViewAdpFromDivYield.MyViewSimStocksHolder holder, int position) {
        holder.symbol.setText(String.valueOf(symbols.get(position)));
        holder.price.setText(String.format("$%s", price.get(position)));
        holder.DivYeild.setText(String.format("%s%%", dividend_yield_percent.get(position)));
        holder.AnnualDividendAmount.setText(String.format("$%s", dividend_rate_Annual.get(position)));
        holder.percent_change.setText(String.format("%s%%", price_change_percent_ytd.get(position)));

    }

    @Override
    public int getItemCount() {
        return symbols.size();
    }

    public static class MyViewSimStocksHolder extends RecyclerView.ViewHolder {
        TextView symbol,price,DivYeild,AnnualDividendAmount,percent_change;

        public MyViewSimStocksHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            symbol = itemView.findViewById(R.id.symbol);
            price = itemView.findViewById(R.id.price);
            DivYeild = itemView.findViewById(R.id.DivYeild);
            AnnualDividendAmount=itemView.findViewById(R.id.AnnualDividendAmount);
            percent_change=itemView.findViewById(R.id.percent_change);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    if (recyclerViewInterface != null){
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}

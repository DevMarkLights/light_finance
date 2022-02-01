package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecViewAdpSimStocks extends RecyclerView.Adapter<RecViewAdpSimStocks.MyViewSimHolder> {
    private final RecyclerViewInterface recyclerViewInterface;
    private final Context context;
    private final ArrayList symbol;
    private final ArrayList price;
    private final ArrayList ytd_return;
    private final ArrayList dividend_amount;
    private final ArrayList percent_Change;

    RecViewAdpSimStocks(Context context, ArrayList symbol, ArrayList price, ArrayList ytd_return,
                        ArrayList dividend_amount, ArrayList percent_Change, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.symbol = symbol;
        this.price = price;
        this.ytd_return = ytd_return;
        this.dividend_amount = dividend_amount;
        this.percent_Change = percent_Change;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public MyViewSimHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflator = LayoutInflater.from(context);
        View view = inflator.inflate(R.layout.similar_stocks_rec_view,parent,false);
        return new MyViewSimHolder(view, recyclerViewInterface);

    }



    @Override
    public void onBindViewHolder(@NonNull RecViewAdpSimStocks.MyViewSimHolder holder, int position) {
        holder.symbol.setText(String.valueOf(symbol.get(position)));
        holder.price.setText("$"+ price.get(position));
        holder.DivYeild.setText(ytd_return.get(position) + "%");
        holder.dividend_amount.setText((CharSequence) dividend_amount.get(position));
        holder.percent_Change.setText(percent_Change.get(position)+"%");
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class MyViewSimHolder extends RecyclerView.ViewHolder {
        TextView symbol,price,DivYeild,dividend_amount,percent_Change;

        public MyViewSimHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            symbol = itemView.findViewById(R.id.symbol);
            price = itemView.findViewById(R.id.price);
            DivYeild = itemView.findViewById(R.id.DivYeild);
            dividend_amount=itemView.findViewById(R.id.dividend_amount);
            percent_Change=itemView.findViewById(R.id.percent_change);

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

package com.example.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class RecViewAdapter extends  RecyclerView.Adapter<RecViewAdapter.MyViewHolder>{
    private final RecyclerViewInterface recyclerViewInterface;
    private static Context context = null;
    private final ArrayList symbol;
    private final ArrayList price;
    private final ArrayList profit_loss;
    private final ArrayList average_cost;
    private final ArrayList dividend_Yield;
    private final ArrayList marketValue;
    private final ArrayList frequency;
    static ApiCalls api = new ApiCalls();
    static portfolio_V2 pt = new portfolio_V2();
    DecimalFormat formatter = new DecimalFormat("#,###.00");

    RecViewAdapter(Context context, ArrayList symbol, ArrayList price, ArrayList profit_loss,
                   ArrayList average_cost, ArrayList dividend_Yield, ArrayList marketValue,
                   ArrayList frequency, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.symbol = symbol;
        this.price = price;
        this.profit_loss = profit_loss;
        this.average_cost = average_cost;
        this.dividend_Yield = dividend_Yield;
        this.marketValue = marketValue;
        this.frequency = frequency;
        this.recyclerViewInterface = recyclerViewInterface;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflator = LayoutInflater.from(context);
        View view = inflator.inflate(R.layout.portfolio_row,parent,false);
        return new MyViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull RecViewAdapter.MyViewHolder holder, int position) {
        holder.symbol.setText(String.valueOf(symbol.get(position)).toUpperCase());
        holder.price.setText("$"+formatter.format(Double.parseDouble(String.valueOf(price.get(position)))));
        holder.average_cost.setText("$"+formatter.format(Double.parseDouble(String.valueOf(average_cost.get(position)))));
        holder.profit_loss.setText("$"+formatter.format(Double.parseDouble(String.valueOf(profit_loss.get(position)))));
        holder.marketValue.setText("$"+ formatter.format(Double.parseDouble(String.valueOf(marketValue.get(position)))));
        holder.dividend_Yield.setText(dividend_Yield.get(position) + "%");
        holder.frequency.setText(String.valueOf(frequency.get(position)));
        if(Double.parseDouble(String.valueOf(profit_loss.get(position))) < 0){
            holder.profit_loss.setTextColor(Color.rgb(255, 0, 0));
        }else {
            holder.profit_loss.setTextColor(Color.rgb(80, 200, 120));
        }
    }

    @Override
    public int getItemCount() {
        return symbol.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
        TextView symbol,price,average_cost,profit_loss,marketValue,dividend_Yield,frequency;
        ImageButton editPortfolioRow;

        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface)   {
            super(itemView);
            symbol = itemView.findViewById(R.id.symbol);
            price = itemView.findViewById(R.id.price);
            average_cost = itemView.findViewById(R.id.averageCost);
            profit_loss = itemView.findViewById(R.id.profit_loss);
            marketValue = itemView.findViewById(R.id.marketValue);
            dividend_Yield = itemView.findViewById(R.id.dividend_Yield);
            frequency = itemView.findViewById(R.id.frequency);
            editPortfolioRow = itemView.findViewById(R.id.editPortfolioRow);
            editPortfolioRow.setOnClickListener(this);


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

        @Override
        public void onClick(View view) {
           showPopUpMenu(view);
           int position = getAdapterPosition();
        }

        private void showPopUpMenu(View view) {
            PopupMenu popup = new PopupMenu(view.getContext(), view);
            popup.inflate(R.menu.portfolio_row_update_delete);
            popup.setOnMenuItemClickListener(this);
            popup.show();

        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.updateStock2:
                    String sym2 = String.valueOf(symbol.getText());
                    Dialog update = new Dialog(context);
                    update.setContentView(R.layout.popup_input_dialog_updatestock);
                    update.show();
                    Button update2 = (Button) update.findViewById(R.id.update_Stock_popup);
                    Button cancel3 = (Button) update.findViewById(R.id.button_cancel_user_data);
                    DBHelper DB3 = new DBHelper(context);
                    update2.setOnClickListener(view -> {
                        EditText share = (EditText) update.findViewById(R.id.Shares_add_stock_popup);
                        EditText Avg = (EditText) update.findViewById(R.id.average_cost_popup);
                        String sha = share.getText().toString();
                        String avgg = Avg.getText().toString();
                        double shares = Double.parseDouble(sha);
                        double aveg = Double.parseDouble(avgg);
                        DB3.updateStock(sym2, shares, aveg);
                        update.dismiss();
                        ((portfolio_V2)context).reloadActivity(context);
                        ((portfolio_V2)context).updateValues();
                    });
                    cancel3.setOnClickListener(view -> update.dismiss());
                    return true;
                case R.id.delete2:
                    String sym = String.valueOf(symbol.getText());
                    Dialog delete = new Dialog(context);
                    delete.setContentView(R.layout.popup_input_dialog_deletestock);
                    delete.show();
                    Button delete2 = (Button) delete.findViewById(R.id.delete_Stock_popup);
                    Button cancel2 = (Button) delete.findViewById(R.id.button_cancel_user_data);
                    DBHelper DB2 = new DBHelper(context);
                    delete2.setOnClickListener(view -> {
                        DB2.deleteStock(sym);
                        delete.dismiss();
                        ((portfolio_V2)context).reloadActivity(context);
                    });
                    cancel2.setOnClickListener(view -> delete.dismiss());
                    return true;
                default:
                    return false;
            }

        }
    }


}

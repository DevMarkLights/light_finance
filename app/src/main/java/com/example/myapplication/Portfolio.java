package com.example.myapplication;

import android.app.Dialog;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Objects;


public class Portfolio extends AppCompatActivity implements OnMenuItemClickListener, RecyclerViewInterface {
    SwipeRefreshLayout swipeRefreshLayout;
    private PieChart pieChart;
    RecyclerView recyclerView;
    ArrayList<String> tmv, Symbol, price, profit_loss, average_cost, Dividend_Yield, marketValue, frequency;
    TextView total_market_value, totalProfitLossView, avgDyview, totalAnnualDiv;
    DBHelper DB;
    RecViewAdapter recViewAdapter;
    ArrayList<String> pieChartMV, pieChartSym;
    ArrayList<PieEntry> products;
    ArrayList<PieModel> mylist = new ArrayList<>();
    ArrayList<Double> total_Profit_Loss, average_Dividend_Yield;
    double totalMV, totalProfitLoss, avgDY, annualDividend;
    TotalDividends TD = new TotalDividends();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle("Portfolio");
        DB = new DBHelper(this);
        //---Initializing the arraylist-----------------
        Symbol = new ArrayList<>();
        price = new ArrayList<>();
        profit_loss = new ArrayList<>();
        average_cost = new ArrayList<>();
        Dividend_Yield = new ArrayList<>();
        marketValue = new ArrayList<>();
        frequency = new ArrayList<>();
        tmv = new ArrayList<>();
        pieChartMV = new ArrayList<>();
        pieChartSym = new ArrayList<>();
        products = new ArrayList<>();
        total_Profit_Loss = new ArrayList<>();
        average_Dividend_Yield = new ArrayList<>();
        //-----------------------------------------------
        //Getting the views to use them in the methods
        totalProfitLossView = findViewById(R.id.totalProfitLossView);
        pieChart = findViewById(R.id.PieChart);
        avgDyview = findViewById(R.id.AvgDyView);
        total_market_value = findViewById(R.id.total_market_value);
        recyclerView = findViewById(R.id.portfolio);
        totalAnnualDiv = findViewById(R.id.annualDividendView);
        //------------------------------------------------
        // to display portfolio
        recViewAdapter = new RecViewAdapter(this, Symbol, price, profit_loss, average_cost,
                Dividend_Yield, marketValue, frequency,this);
        recyclerView.setAdapter(recViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        totalMarketValue();
        // method throws and error
        totalProfitLoss();
        averageDividendYield();
        storeDataInArrays();
        loadDataForPie();
        annualDividend();
        for (int i = 0; i < mylist.size(); i++) {
            String product = mylist.get(i).getProduct();
            float quantity = Float.parseFloat(mylist.get(i).getQuantity());
            products.add(new PieEntry(quantity, "" + product));
        }

        PieDataSet pieDataSet = new PieDataSet(products, "");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(12f);
        pieDataSet.setSliceSpace(2);
        pieDataSet.setSelectionShift(1f);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("stocks");
        pieChart.animate();

        Button editPortfolio = (Button) findViewById(R.id.editPortfolio);
        editPortfolio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(Portfolio.this, view);
                popup.setOnMenuItemClickListener(Portfolio.this);
                popup.inflate(R.menu.menu_drop_down);
                popup.show();
            }
        });

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recViewAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }


    void storeDataInArrays() {
        Cursor cursor = DB.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No Stocks", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                Symbol.add(cursor.getString(0));
                price.add(cursor.getString(1));
                profit_loss.add(cursor.getString(4));
                average_cost.add(cursor.getString(3));
                Dividend_Yield.add(cursor.getString(6));
                marketValue.add(cursor.getString(10));
                frequency.add(cursor.getString(8));
            }
        }
    }

    public void totalMarketValue() {
        Cursor cursor = DB.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No stocks", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                tmv.add(cursor.getString(10));
            }
        }
        double total = 0.0;
        for (int i = 0; i < tmv.size(); i++) {
            total = total + Double.parseDouble(tmv.get(i));
            totalMV = total;
        }
        BigDecimal a = new BigDecimal(totalMV);
        BigDecimal b = a.setScale(3, RoundingMode.DOWN);
        totalMV = Double.parseDouble(String.valueOf(b));
        total_market_value.setText("");
        total_market_value.setText(String.format("$%s", totalMV));
    }

    public void averageDividendYield() {
        Cursor cursor = DB.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No Stocks", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                average_Dividend_Yield.add(Double.valueOf(cursor.getString(6)));
            }
        }
        double size = average_Dividend_Yield.size();
        double total = 0.0;
        for (int i = 0; i < average_Dividend_Yield.size(); i++) {
            total = total + Double.parseDouble(String.valueOf(average_Dividend_Yield.get(i)));
            avgDY = total / size;
        }
        BigDecimal a = new BigDecimal(avgDY);
        BigDecimal b = a.setScale(3, RoundingMode.DOWN);
        avgDY = Double.parseDouble(String.valueOf(b));
        avgDyview.setText("");
        avgDyview.setText(avgDY + "%");

    }

    public void loadDataForPie() {
        Cursor cursor = DB.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No stocks", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                mylist.add(new PieModel("" + cursor.getString(0), "" + cursor.getString(10)));
            }
        }

    }

    public void totalProfitLoss() {
        Cursor cursor = DB.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No stocks", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                total_Profit_Loss.add(Double.valueOf(cursor.getString(4).substring(0, 3)));
            }
        }
        double sum = 0;
        for (int i = 0; i < total_Profit_Loss.size(); i++) {
            sum = sum + Double.parseDouble(String.valueOf(total_Profit_Loss.get(i)));
        }
        totalProfitLoss = sum;
        BigDecimal a = new BigDecimal(totalProfitLoss);
        BigDecimal b = a.setScale(3, RoundingMode.DOWN);
        double tpl = Double.parseDouble(String.valueOf(b));
        totalProfitLossView.setText(String.format("$%s", tpl));
    }

    public void annualDividend() {
        Cursor cursor = DB.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No Stocks", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                float div = Float.parseFloat(cursor.getString(5));
                float shares = Float.parseFloat(cursor.getString(2));
                String f = cursor.getString(8);
                float totaldiv = 0;
                if (f.startsWith("M")) {
                    totaldiv = (div * shares) * 12;
                } else if (f.startsWith("Q")) {
                    totaldiv = (div * shares) * 4;
                } else if (f.startsWith("S")) {
                    totaldiv = (div * shares) * 2;
                } else {
                    totaldiv = div * shares;
                }
                annualDividend = annualDividend + totaldiv;
            }
            BigDecimal a = new BigDecimal(annualDividend);
            BigDecimal b = a.setScale(2, RoundingMode.DOWN);
            totalAnnualDiv.setText(String.format("$%s", String.valueOf(b)));
        }
    }



        // implement methods to create an overlay so that the user can enter new data
        public boolean onMenuItemClick (MenuItem item){
            switch (item.getItemId()) {
                case R.id.addStock2:
                    Dialog dialog = new Dialog(this);
                    dialog.setContentView(R.layout.popup_input_dialog_addstock);
                    dialog.show();
                    Button add = (Button) dialog.findViewById(R.id.add_Stock_popup);
                    Button cancel = (Button) dialog.findViewById(R.id.button_cancel_user_data);
                    DBHelper DB = new DBHelper(this);
                    add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            EditText sy = (EditText) dialog.findViewById(R.id.symbol);
                            EditText shar = (EditText) dialog.findViewById(R.id.Shares_add_stock_popup);
                            EditText cps = (EditText) dialog.findViewById(R.id.average_cost_popup);
                            EditText fr = (EditText) dialog.findViewById(R.id.frequency);
                            String symbol = sy.getText().toString();
                            String sha = shar.getText().toString();
                            double shares = Double.parseDouble(String.valueOf(sha));
                            String ag = cps.getText().toString();
                            double avgCost = Double.parseDouble(String.valueOf(ag));
                            String freqq = fr.getText().toString();
                            DB.addStocksDialog(symbol, shares, avgCost, freqq);
                            dialog.dismiss();
                            relaodActivity();
                        }
                    });
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();

                        }
                    });
                    return true;
                case R.id.delete2:
                    Dialog delete = new Dialog(this);
                    delete.setContentView(R.layout.popup_input_dialog_deletestock);
                    delete.show();
                    Button delete2 = (Button) delete.findViewById(R.id.delete_Stock_popup);
                    Button cancel2 = (Button) delete.findViewById(R.id.button_cancel_user_data);
                    DBHelper DB2 = new DBHelper(this);
                    delete2.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            EditText sy = (EditText) delete.findViewById(R.id.symbol);
                            String symbol = sy.getText().toString();
                            DB2.deleteStock(symbol);
                            delete.dismiss();
                            relaodActivity();
                        }
                    });

                    cancel2.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            delete.dismiss();
                        }
                    });

                    return true;
                case R.id.updateStock2:
                    Dialog update = new Dialog(this);
                    update.setContentView(R.layout.popup_input_dialog_updatestock);
                    update.show();
                    Button update2 = (Button) update.findViewById(R.id.update_Stock_popup);
                    Button cancel3 = (Button) update.findViewById(R.id.button_cancel_user_data);
                    DBHelper DB3 = new DBHelper(this);
                    update2.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            EditText sym = (EditText) update.findViewById(R.id.symbol);
                            EditText share = (EditText) update.findViewById(R.id.Shares_add_stock_popup);
                            EditText Avg = (EditText) update.findViewById(R.id.average_cost_popup);
                            String symbol2 = sym.getText().toString();
                            double shares = Double.parseDouble(String.valueOf(share));
                            double aveg = Double.parseDouble(String.valueOf(Avg));
                            DB3.updateStock(symbol2, shares, aveg);
                            update.dismiss();
                            relaodActivity();
                        }
                    });
                    cancel3.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            update.dismiss();
                        }
                    });

                    return true;
                default:
                    return false;
            }
        }
        public void relaodActivity () {
            finish();
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);
        }

    @Override
    public void onItemClick(int position) {

    }
}







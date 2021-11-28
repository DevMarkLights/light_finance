package com.example.myapplication;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class TotalDividends extends AppCompatActivity {
    ArrayList<BarEntry> dividend;
    ArrayList<String> stocks,totalDividendAmount;
    private BarChart chart;
    DBHelper DB;
    double total_div_amount;
    TextView totalDividendView;

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_dividends);
        setTitle("Total Dividends Per Stock");


        DB = new DBHelper(this);
        chart = (BarChart) findViewById(R.id.fragment_verticalbarchart_chart);
        dividend = new ArrayList<>();
        stocks = new ArrayList<>();
        totalDividendAmount = new ArrayList<>();
        totalDividendView = findViewById(R.id.totalDividendView);
        getTotalDividendPerMonth();

        BarDataSet barDataSet = new BarDataSet(dividend, "Stocks");
        barDataSet.setColors(Color.rgb(0, 150, 255));
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(11f);
        BarData barData = new BarData(barDataSet);
        chart.setFitBars(true);
        chart.setData(barData);
        chart.setScaleEnabled(true);
        chart.getDescription().setEnabled(false);
        chart.setDragEnabled(true);
        chart.setVisibleXRange(0,dividend.size());
        chart.animateY(2000);
        chart.setHorizontalScrollBarEnabled(true);
        barData.setBarWidth(.7f);
        XAxis xAxis = chart.getXAxis();
        xAxis.setLabelCount(dividend.size());
        xAxis.setValueFormatter(new IndexAxisValueFormatter(stocks));
        xAxis.setCenterAxisLabels(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(0);
        xAxis.setGranularityEnabled(true);
        chart.getXAxis().setSpaceMax(1);
        xAxis.setDrawGridLines(false);
        xAxis.setSpaceMin(0);
        chart.invalidate();
        getTotalDividend();
    }

   

    public void getTotalDividendPerMonth() {
        Cursor cursor = DB.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No Stocks", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                float div = Float.parseFloat(cursor.getString(5));
                float shares = Float.parseFloat(cursor.getString(2));
                String f = cursor.getString(8);
                float totaldiv = 0;
                if(f.startsWith("M")){
                     totaldiv = (div * shares)*12;
                }else if(f.startsWith("Q")){
                    totaldiv = (div * shares)*4;
                }else if(f.startsWith("S")){
                    totaldiv = (div * shares)*2;
                } else{
                    totaldiv = div * shares;
                }
                dividend.add(new BarEntry(cursor.getPosition(), totaldiv));
                stocks.add(cursor.getString(0));
            }
        }
    }
    
    public void getTotalDividend() {
        Cursor cursor = DB.readAllData();
        if(cursor.getCount() == 0) {
            Toast.makeText(this,"No Stocks", Toast.LENGTH_SHORT).show();
        }else{
            while(cursor.moveToNext()){
                float div = Float.parseFloat(cursor.getString(5));
                float shares = Float.parseFloat(cursor.getString(2));
                String f = cursor.getString(8);
                float totaldiv = 0;
                if(f.startsWith("M")){
                    totaldiv = (div * shares)*12;
                }else if(f.startsWith("Q")){
                    totaldiv = (div * shares)*4;
                }else if(f.startsWith("S")){
                    totaldiv = (div * shares)*2;
                } else{
                    totaldiv = div * shares;
                }
                totalDividendAmount.add(String.valueOf(totaldiv));
            }
        }
        double total = 0;
        for(int i = 0; i<totalDividendAmount.size();i++) {
            total = total + Double.parseDouble(String.valueOf(totalDividendAmount.get(i)));
        }
        total_div_amount = total;
        BigDecimal a = new BigDecimal(total_div_amount);
        BigDecimal b = a.setScale(2, RoundingMode.DOWN);
        totalDividendView.setText("$"+String.valueOf(b)+" annually");
    }


}

package com.example.myapplication;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;

public class TotalMarketValuePerStock extends AppCompatActivity {
    ArrayList<BarEntry> marketValue;
    ArrayList<String> stocks;
    private BarChart chart;
    DBHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_market_value_per_stock);
        setTitle("Market Value per Stock");

        DB = new DBHelper(this);
        marketValue = new ArrayList<>();
        stocks = new ArrayList<>();
        chart = findViewById(R.id.fragment_verticalbarchart_chart);

        marketValuePerStock();

        BarDataSet barDataSet = new BarDataSet(marketValue, "Stocks");
        barDataSet.setColors(Color.rgb(0, 150, 255));
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(11f);
        BarData barData = new BarData(barDataSet);
        chart.setFitBars(true);
        chart.setData(barData);
        chart.setScaleEnabled(true);
        chart.getDescription().setEnabled(false);
        chart.setDragEnabled(true);
        chart.setVisibleXRange(0,marketValue.size());
        chart.animateY(2000);
        chart.setHorizontalScrollBarEnabled(true);
        barData.setBarWidth(.7f);
        XAxis xAxis = chart.getXAxis();
        xAxis.setLabelCount(marketValue.size());
        xAxis.setValueFormatter(new IndexAxisValueFormatter(stocks));
        xAxis.setCenterAxisLabels(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(0);
        xAxis.setGranularityEnabled(true);
        chart.getXAxis().setSpaceMax(1);
        xAxis.setDrawGridLines(false);
        xAxis.setSpaceMin(0);
        chart.invalidate();

    }

    public void marketValuePerStock() {
        Cursor cursor = DB.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No Stocks", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                float mkt = Float.parseFloat(cursor.getString(10));
                marketValue.add(new BarEntry(cursor.getPosition(), mkt));
                stocks.add(cursor.getString(0));
            }
        }
    }
}
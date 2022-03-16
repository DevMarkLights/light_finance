package com.example.myapplication;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class TotalDividends extends AppCompatActivity {
    ArrayList<BarEntry> dividend;
    ArrayList<String> stocks,totalDividendAmount;
    private BarChart chart;
    DBHelper DB;
    static DecimalFormat formatter = new DecimalFormat("#,###.00");


    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_dividends);
        setTitle("Total Dividends Per Stock");
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        int color = Color.parseColor("#808080");
        window.setStatusBarColor(color);

        ActionBar bar;
        bar = getSupportActionBar();
        ColorDrawable cd = new ColorDrawable(Color.parseColor("#808080"));
        bar.setBackgroundDrawable(cd);


        DB = new DBHelper(this);
        chart = (BarChart) findViewById(R.id.fragment_verticalbarchart_chart);
        dividend = new ArrayList<>();
        stocks = new ArrayList<>();
        totalDividendAmount = new ArrayList<>();

        getTotalDividendPerMonth();

        BarDataSet barDataSet = new BarDataSet(dividend, "Stocks");
        barDataSet.setColors(Color.rgb(0, 150, 255));
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(11f);
        BarData barData = new BarData(barDataSet);
        barData.setDrawValues(true);
        chart.setDrawValueAboveBar(false);
        chart.setFitBars(true);
        chart.setData(barData);
        chart.setScaleEnabled(true);
        chart.getDescription().setEnabled(false);
        chart.setDragEnabled(true);
        chart.setVisibleXRange(0,dividend.size());
        chart.animateY(0);
        chart.setHorizontalScrollBarEnabled(true);
        barData.setBarWidth(.7f);
        barData.setValueFormatter(new IntValueFormatter());
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
                String k = formatter.format(totaldiv);
                dividend.add(new BarEntry(cursor.getPosition(), Float.parseFloat(k)));
                stocks.add(cursor.getString(0));
            }
        }
    }

    public static class IntValueFormatter extends ValueFormatter implements IValueFormatter {

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            String t = String.valueOf(value);
            return formatter.format(t);
        }
    }


}

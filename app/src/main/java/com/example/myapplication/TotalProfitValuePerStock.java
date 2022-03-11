package com.example.myapplication;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

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

import java.util.ArrayList;

public class TotalProfitValuePerStock extends AppCompatActivity {

    ArrayList<BarEntry> profitValue;
    ArrayList<String> stocks;
    private BarChart chart;
    DBHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_profit_value_per_stock);
        setTitle("Total Profit Value Per Stock");
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        int color = Color.parseColor("#808080");
        window.setStatusBarColor(color);

        ActionBar bar;
        bar = getSupportActionBar();
        ColorDrawable cd = new ColorDrawable(Color.parseColor("#808080"));
        bar.setBackgroundDrawable(cd);


        DB = new DBHelper(this);
        profitValue = new ArrayList<>();
        stocks = new ArrayList<>();
        chart = findViewById(R.id.fragment_verticalbarchart_chart);

        profitValuePerStock();

        BarDataSet barDataSet = new BarDataSet(profitValue, "Stocks");
        barDataSet.setColors(Color.rgb(0, 150, 255));
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(11f);
        BarData barData = new BarData(barDataSet);
        chart.setFitBars(true);
        chart.setData(barData);
        chart.setScaleEnabled(true);
        chart.setDrawValueAboveBar(true);
        chart.getDescription().setEnabled(false);
        chart.setDragEnabled(true);
        chart.setVisibleXRange(0,profitValue.size());
        chart.animateY(0);
        chart.setHorizontalScrollBarEnabled(true);
        barData.setBarWidth(.7f);
        barData.setDrawValues(true);
        barData.setValueFormatter(new IntValueFormatter());
        XAxis xAxis = chart.getXAxis();
        xAxis.setLabelCount(profitValue.size());
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

    public void profitValuePerStock() {
        Cursor cursor = DB.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No Stocks", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                float pv = Float.parseFloat(cursor.getString(4));
                profitValue.add(new BarEntry(cursor.getPosition(), pv));
                stocks.add(cursor.getString(0));
            }
        }
    }

    public static class IntValueFormatter extends ValueFormatter implements IValueFormatter {

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return String.valueOf((int) value);
        }
    }
}
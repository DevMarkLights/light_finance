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

public class dividendYieldPerStock extends AppCompatActivity {

    ArrayList<BarEntry> dividendYield;
    ArrayList<String> stocks;
    private BarChart chart;
    DBHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dividend_yield_per_stock);
        setTitle("Dividend Yield Per Stock");

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
        dividendYield = new ArrayList<>();
        stocks = new ArrayList<>();

        getDivYieldPerStock();

        BarDataSet barDataSet = new BarDataSet(dividendYield, "Stocks");
        barDataSet.setColors(Color.rgb(0, 150, 255));
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(11f);
        BarData barData = new BarData(barDataSet);
        chart.setFitBars(true);
        chart.setData(barData);
        chart.setScaleEnabled(true);
        chart.getDescription().setEnabled(false);
        chart.setDragEnabled(true);
        chart.setVisibleXRange(0,dividendYield.size());
        chart.animateY(0);
        chart.setHorizontalScrollBarEnabled(true);
        barData.setBarWidth(.7f);
        barData.setDrawValues(true);
        chart.setDrawValueAboveBar(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setLabelCount(dividendYield.size());
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

    public void getDivYieldPerStock() {
        Cursor cursor = DB.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No Stocks", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                float divYield = Float.parseFloat(cursor.getString(6));
                dividendYield.add(new BarEntry(cursor.getPosition(),divYield));
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
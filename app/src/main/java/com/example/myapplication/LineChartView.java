package com.example.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;

import org.json.JSONException;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LineChartView extends AppCompatActivity {
    ApiCalls api = new ApiCalls();
    LineChart lineChart;
    TextView highestvalue,lowestValue;
    double highVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart);
        setTitle("stock price history");

        highestvalue = findViewById(R.id.highestValue);
        lowestValue = findViewById(R.id.lowestValue);

        try {
            api.historicalPrices("Slvo",30);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        highestValueForLineChart();
        lowestValueForLineChart();

        lineChart = findViewById(R.id.lineChart);
        List<Entry> lineEntries = getDataSet();
        LineDataSet lineDataSet = new LineDataSet(lineEntries, "");
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setLineWidth(2);
        lineDataSet.setColors(Color.rgb(0, 150, 255));
        lineDataSet.setCircleRadius(2);
        lineDataSet.setCircleHoleRadius(1);
        lineDataSet.setDrawValues(false);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new DefaultAxisValueFormatter(0));
        xAxis.setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false);



        LineData lineData = new LineData(lineDataSet);
        lineChart.getDescription().setText("Past 30 day stock price");
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        lineChart.setData(lineData);
    }

    private List<Entry> getDataSet() {
        List<Entry> lineEntries = new ArrayList<Entry>();
        for(int i = 0; i <api.historicalPrice30.size(); i++) {
            float data = Float.parseFloat(api.historicalPrice30.get(i));
            lineEntries.add(new Entry(i, data));

        }
        return lineEntries;
    }

    public void highestValueForLineChart() {
        highVal = Double.parseDouble(Collections.max(api.historicalPrice30));
        BigDecimal a = new BigDecimal(highVal);
        BigDecimal b = a.setScale(2, RoundingMode.DOWN);
        highestvalue.setText(String.format("$%s", String.valueOf(b)));

    }

    public void lowestValueForLineChart() {
        double min = 0.0;
        for (int i = 0; i < api.historicalPrice30.size(); i++) {
            if (i == 0){
                min = Double.parseDouble(api.historicalPrice30.get(i));
            }
            if (Double.parseDouble(api.historicalPrice30.get(i)) < min) {
                min = Double.parseDouble(api.historicalPrice30.get(i));
            }
        }
        BigDecimal a = new BigDecimal(min);
        BigDecimal b = a.setScale(2, RoundingMode.DOWN);
        lowestValue.setText(String.format("$%s", String.valueOf(b)));

    }

 }

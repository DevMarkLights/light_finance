package com.example.myapplication;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import soup.neumorphism.NeumorphButton;


public class FutureValue extends AppCompatActivity  {
    portfolio_V2 pt = new portfolio_V2();
    float totalMarketValue;
    private BarChart chart;
    ArrayList<BarEntry> futureMarketvalue;
    ArrayList<String> futureYear;
    float totalDispersedDividends,endingValue,expectedDividends;
    NeumorphButton oneYearftv,fiveYearftv,tenYearftv,twentyFiveYearftv;
    TextView totalDividendsReceived,endingAmount,expectedDiv,presentValue;
    SwitchCompat reinvestDivSwitch;
    EditText annualContributions;
    DecimalFormat formatter = new DecimalFormat("#,###.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future_value);
        setTitle("Future Value");

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        int color = Color.parseColor("#808080");
        window.setStatusBarColor(color);

        ActionBar bar;
        bar = getSupportActionBar();
        ColorDrawable cd = new ColorDrawable(Color.parseColor("#808080"));
        bar.setBackgroundDrawable(cd);

        chart = (BarChart) findViewById(R.id.futureValueChart);
        oneYearftv = findViewById(R.id.oneYearftv);
        fiveYearftv = findViewById(R.id.fiveYearftv);
        tenYearftv = findViewById(R.id.tenYearftv);
        twentyFiveYearftv = findViewById(R.id.twentyFiveYearftv);
        totalDividendsReceived = findViewById(R.id.totalDividendsReceived);
        endingAmount = findViewById(R.id.endingAmount);
        reinvestDivSwitch = findViewById(R.id.reinvestDivSwitch);
        annualContributions = findViewById(R.id.annualContributions);
        expectedDiv = findViewById(R.id.expectedDiv);
        presentValue = findViewById(R.id.presentValue);

        reinvestDivSwitch.toggle();
        setOnClickListeners();

        futureMarketvalue = new ArrayList<BarEntry>();
        futureYear = new ArrayList<String>();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                calculateTheFutureValue(5);
            }
        }); t.start();

    }

    public void setOnClickListeners() {

        oneYearftv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        calculateTheFutureValue(1);
                    }
                });t.start();
            }
        });

        fiveYearftv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        calculateTheFutureValue(5);
                    }
                });t.start();
            }
        });

        tenYearftv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        calculateTheFutureValue(10);
                    }
                });t.start();
            }
        });

        twentyFiveYearftv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        calculateTheFutureValue(25);
                    }
                });t.start();
            }
        });

    }

    public void setBarChart(){
        BarDataSet barDataSet = new BarDataSet(futureMarketvalue, "Future Value");
        barDataSet.setColors(Color.rgb(0, 150, 255));
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(5f);
        BarData barData = new BarData(barDataSet);
        chart.setFitBars(true);
        chart.setData(barData);
        barData.setDrawValues(true);
        chart.setScaleEnabled(true);
        chart.getDescription().setEnabled(false);
        chart.setDragEnabled(true);
        chart.setVisibleXRange(0,futureMarketvalue.size());
        chart.animateY(1000);
        chart.setHorizontalScrollBarEnabled(true);
        chart.setTouchEnabled(true);
        barData.setBarWidth(.7f);
        XAxis xAxis = chart.getXAxis();
        xAxis.setLabelCount(futureMarketvalue.size());
        xAxis.setValueFormatter(new IndexAxisValueFormatter(futureYear));
        xAxis.setCenterAxisLabels(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(0);
        xAxis.setGranularityEnabled(true);
        chart.getXAxis().setSpaceMax(1);
        xAxis.setDrawGridLines(false);
        xAxis.setSpaceMin(0);
        //CustomMarkerClass mv = new CustomMarkerClass(this, R.layout.custom_marker_layout);
        //chart.setMarker(mv);
        chart.invalidate();
    }

    // calculates the given future value based on the input
    public void calculateTheFutureValue(int length){
        float AC = Float.parseFloat(annualContributions.getText().toString());
        if(reinvestDivSwitch.isChecked()){ // dividends reinvested
            futureYear.clear();
            futureMarketvalue.clear();
            totalDispersedDividends = 0;
            double ARI = .06; // average stock market return on investment accounted for inflation
            totalMarketValue = (float) portfolio_V2.totalMV;
            int year = Calendar.getInstance().get(Calendar.YEAR);
            float divY = (float) portfolio_V2.avgDY;
            float value = 0;

            for(int i = 0; i <=length; i++){
                // base case
                if(i==0){
                    futureYear.add(String.valueOf(year));
                    futureMarketvalue.add(new BarEntry(i,totalMarketValue));
                    year++;
                    value = totalMarketValue;
                } else{
                    // getting dividends for each year and calculating them with the new price of the next year
                    float divAmount = 0;
                    float tempDy = divY/100;// gets the average percent dividend yield in decimal form
                    double tempV = value * ARI;// gets the average return on the money in your portfolio
                    value = (float) (value + tempV); // inflation minus the money you have for that year
                    divAmount = value * tempDy; // gets the dollar amount of dividends for this year
                    value = (float) (value + divAmount + AC);
                    futureMarketvalue.add(new BarEntry(i,value));
                    futureYear.add(String.valueOf(year));
                    year++;
                    totalDispersedDividends+=divAmount;
                    if(i == length){
                        endingValue = value;
                        expectedDividends = value * tempDy;
                    }
                }

            }

            Handler mainHandler = new Handler(Looper.getMainLooper());
            // Send a task to the MessageQueue of the main thread
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    // Code will be executed on the main thread
                    setBarChart();
                    totalDividendsReceived.setText("$"+formatter.format(totalDispersedDividends));
                    endingAmount.setText("$"+formatter.format(endingValue));
                    expectedDiv.setText("$"+formatter.format(expectedDividends));
                    presentValue.setText("$"+formatter.format(totalMarketValue));
                }
            });
        } else { // dividends not reinvested
            futureYear.clear();
            futureMarketvalue.clear();
            totalDispersedDividends = 0;
            double ARI = .06; // average stock market return on investment accounted for inflation
            totalMarketValue = (float) portfolio_V2.totalMV;
            int year = Calendar.getInstance().get(Calendar.YEAR);
            float divY = (float) portfolio_V2.avgDY;
            float value = 0;

            for(int i = 0; i <=length; i++){
                // base case
                if(i==0){
                    futureYear.add(String.valueOf(year));
                    futureMarketvalue.add(new BarEntry(i,totalMarketValue));
                    year++;
                    value = totalMarketValue;
                } else{
                    // getting dividends for each year and calculating them with the new price of the next year
                    float divAmount = 0;
                    float tempDy = divY/100;// gets the average percent dividend yield in decimal form
                    double tempV = value * ARI;// gets the average return on the money in your portfolio
                    value = (float) (value + tempV); // average return plus the money you have for that year
                    divAmount = value * tempDy; // gets the dollar amount of dividends for this year
                    value = (float) (value + AC);
                    futureMarketvalue.add(new BarEntry(i,value));
                    futureYear.add(String.valueOf(year));
                    year++;
                    totalDispersedDividends+=divAmount;
                    if(i == length){
                        endingValue = value;
                        expectedDividends = value * tempDy;
                    }
                }


            }

            Handler mainHandler = new Handler(Looper.getMainLooper());
            // Send a task to the MessageQueue of the main thread
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    // Code will be executed on the main thread
                    setBarChart();
                    totalDividendsReceived.setText("$"+formatter.format(totalDispersedDividends));
                    endingAmount.setText("$"+formatter.format(endingValue));
                    expectedDiv.setText("$"+formatter.format(expectedDividends));
                    presentValue.setText("$"+formatter.format(totalMarketValue));

                }
            });
        }

    }


}
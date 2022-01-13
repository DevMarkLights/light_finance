package com.example.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
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

import soup.neumorphism.NeumorphButton;
import soup.neumorphism.NeumorphTextView;

public class LineChartView extends AppCompatActivity {
    ApiCalls api = new ApiCalls();
    LineChart lineChart;
    TextView highestvalue,lowestValue;
    // For 1 day card view
    TextView openValue,lowValue,highValue,closeValue,fifty_two_week_high_TV,fifty_two_week_low_TV,
            divAmountView,exDivDate;
    double highVal;
    NeumorphTextView symbolView,priceView;
    NeumorphButton thirtydaytv,NinetydayTV,oneYear,sevenDay;
    int days = 7;
    double lowest,highest;
    String fifty_two_week_loww,fifty_two_week_highh,high,low,open,close, exDate;
    double divAmount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart);
        setTitle("stock price history");
        String sym = String.valueOf(getIntent().getSerializableExtra("Symbol"));

        highestvalue = findViewById(R.id.highestValue);
        lowestValue = findViewById(R.id.lowestValue);
        thirtydaytv = findViewById(R.id.thirtydayTV);
        NinetydayTV = findViewById(R.id.NinetydayTV);
        oneYear = findViewById(R.id.oneYear);
        sevenDay = findViewById(R.id.sevenDay);
        openValue = findViewById(R.id.openValue);
        lowValue = findViewById(R.id.lowValue);
        highValue = findViewById(R.id.highValue);
        closeValue = findViewById(R.id.closeValue);
        fifty_two_week_high_TV = findViewById(R.id.fifty_two_week_high_TV);
        fifty_two_week_low_TV = findViewById(R.id.fifty_two_week_low_TV);
        divAmountView = findViewById(R.id.divAmountView);
        exDivDate = findViewById(R.id.exDivDate);
        symbolView = findViewById(R.id.symbolView);
        priceView = findViewById(R.id.priceView);
        api.price(sym);
        priceView.setText("$"+String.valueOf(ApiCalls.stock_price));
        symbolView.setText(sym);
        fiftyTwoWeek(sym);
        getDivAmount_ExDiv(sym);


        try {
            api.historicalPrices(sym,days);
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
        if(lowest > highest){
            lineDataSet.setColors(Color.rgb(255, 0, 0));
        }
        lineDataSet.setColors(Color.rgb(0, 150, 255));
        lineDataSet.setCircleRadius(2);
        lineDataSet.setCircleHoleRadius(1);
        lineDataSet.setDrawValues(false);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new DefaultAxisValueFormatter(api.historicalPrice30.size()));
        xAxis.setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false);

        LineData lineData = new LineData(lineDataSet);
        lineChart.getDescription().setText("Past 7 day stock price");
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        lineChart.setData(lineData);

        thirtydaytv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                days = 30;
                dataForLineChart(sym,days);
                negOrPos();
                lineChart();
                lineChart.getDescription().setText("Past 30 day stock price");
                highestValueForLineChart();
                lowestValueForLineChart();
            }
        });
        NinetydayTV.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                days = 90;
                dataForLineChart(sym,days);
                lineChart.getDescription().setText("Past 90 day stock price");
                negOrPos();
                lineChart();
                highestValueForLineChart();
                lowestValueForLineChart();
            }
        });
        oneYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                days = 365;
                dataForLineChart(sym,days);
                lineChart.getDescription().setText("Past year day stock price");
                negOrPos();
                lineChart();
                highestValueForLineChart();
                lowestValueForLineChart();
                negOrPos();
            }
        });
        sevenDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                days = 7;
                dataForLineChart(sym,days);
                lineChart.getDescription().setText("Past 7 day stock price");
                negOrPos();
                lineChart();
                highestValueForLineChart();
                lowestValueForLineChart();
                negOrPos();
            }
        });
    }

    void dataForLineChart(String sym, int days) {
        try {
            api.historicalPrices(sym,days);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        getDataSet();
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

    public void negOrPos(){

        double l = Double.parseDouble(api.historicalPrice30.get(0));
        double h = Double.parseDouble(api.historicalPrice30.get(api.historicalPrice30.size()-1));
        lowest = l;
        highest = h;
    }

    public void reloadActivity() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    public void lineChart() {
        lineChart = findViewById(R.id.lineChart);
        List<Entry> lineEntries = getDataSet();
        LineDataSet lineDataSet = new LineDataSet(lineEntries, "");
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setLineWidth(2);
        if(lowest > highest){
            lineDataSet.setColors(Color.rgb(230, 0, 0));
        }else {
            lineDataSet.setColors(Color.rgb(0, 150, 255));
        }
        lineDataSet.setCircleRadius(2);
        lineDataSet.setCircleHoleRadius(1);
        lineDataSet.setDrawValues(false);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new DefaultAxisValueFormatter(api.historicalPrice30.size()));
        xAxis.setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false);

        LineData lineData = new LineData(lineDataSet);
        //lineChart.getDescription().setText("Past 7 day stock price");
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        lineChart.setData(lineData);

        lineChart.notifyDataSetChanged();
        lineChart.invalidate();

    }

    public void fiftyTwoWeek(String symb) {
        api.stockQuote(symb);
        high = api.high;
        low = api.low;
        open = api.open;
        close = api.close;

        openValue.setText("$"+open);
        lowValue.setText("$"+low);
        highValue.setText("$"+high);
        closeValue.setText("$"+close);

        fifty_two_week_loww = api.fifty_two_week_low;
        fifty_two_week_highh = api.fifty_two_week_high;

        fifty_two_week_low_TV.setText("$"+fifty_two_week_loww);
        fifty_two_week_high_TV.setText("$"+fifty_two_week_highh);
    }

    public void getDivAmount_ExDiv(String sym){
        api.Dividend(sym);
        divAmount = ApiCalls.amount_of_dividend;
        exDate = ApiCalls.date_of_dividend;
        String amount = String.valueOf(divAmount);
        divAmountView.setText("$"+amount);
        exDivDate.setText(exDate);
    }
 }

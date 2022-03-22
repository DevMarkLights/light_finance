package com.example.myapplication;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CalendarActivity extends AppCompatActivity implements RecyclerViewInterface{
    CalendarView calendarView;
    RecyclerView calendarViewRecyclerView;
    RecViewCalendarView recViewCalendarView;
    TextView dateView,frequencyView,amountOfDividendView,annualDividendAmountView;
    ArrayList<String> symbols = new ArrayList<String>();
    ArrayList<String> date = new ArrayList<String>();
    ArrayList<String> frequency = new ArrayList<String>();
    ArrayList<String> amountOfDividend = new ArrayList<String>();
    ArrayList<String> totalDividendAmount = new ArrayList<String>();
    DBHelper DB;
    DecimalFormat formatter = new DecimalFormat("#,###.00");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_view);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        int color = Color.parseColor("#808080");
        window.setStatusBarColor(color);

        ActionBar bar;
        bar = getSupportActionBar();
        ColorDrawable cd = new ColorDrawable(Color.parseColor("#808080"));
        if (bar != null) {
            bar.setBackgroundDrawable(cd);
        }
        DB = new DBHelper(this);

        getUiElements();

        setMinAndMaxDates();

        dataFromDateOnClick();

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i1, int  i2, int i3) {
                String year = String.valueOf(i1);
                String month;
                int k = i2+1;
                if(k < 10){
                     month = String.format("0%s", String.valueOf(k));
                } else{
                     month = String.valueOf(k);
                }
                String day = String.valueOf(i3);
                String dates = year +"-"+ month +"-"+ day;
                Thread ta = new Thread(() -> {
                    Cursor cursor = DB.readAllData();
                    if (cursor.getCount() == 0) {
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(()->{
                            Toast.makeText(CalendarActivity.this, "No stocks", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        //clearing arraylist
                        symbols.clear();amountOfDividend.clear();frequency.clear();date.clear();totalDividendAmount.clear();
                        while (cursor.moveToNext()) {
                            if(cursor.getString(9).startsWith(dates)) {
                                symbols.add(cursor.getString(0));
                                float shares = Float.parseFloat(cursor.getString(2));
                                amountOfDividend.add(String.valueOf(cursor.getString(5)));
                                frequency.add(cursor.getString(8));
                                date.add(cursor.getString(9));
                                totalDividendAmount.add(formatter.format(shares * Float.parseFloat(cursor.getString(5))));
                            }
                        }
                        // if no symbols match that date then there are no dividends for that date
                        if(symbols.isEmpty()){
                            Handler mainHandler = new Handler(Looper.getMainLooper());
                            mainHandler.post(()->{
                                Toast.makeText(CalendarActivity.this, "No dividends today", Toast.LENGTH_LONG).show();
                            });
                        }
                    }
                    cursor.close();
                    setRecyclerView();
                }); ta.start();
            }
        });

    }

    private void getUiElements() {
        calendarView = findViewById(R.id.calendarView2);
        calendarViewRecyclerView = findViewById(R.id.calendarViewRecyclerView);
        dateView = findViewById(R.id.dateView);
        frequencyView = findViewById(R.id.frequencyView);
        amountOfDividendView = findViewById(R.id.amountOfDividendView);
        annualDividendAmountView = findViewById(R.id.annualDividendAmountView);
        symbols = new ArrayList<String>();
        date = new ArrayList<String>();
        frequency = new ArrayList<String>();
        amountOfDividend = new ArrayList<String>();
        totalDividendAmount = new ArrayList<String>();

    }


    void setMinAndMaxDates() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
        calendar.set(Calendar.HOUR_OF_DAY, 23);//not sure this is needed
        long endOfMonth = calendar.getTimeInMillis();
        //may need to reinitialize calendar, not sure
        calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        long startOfMonth = calendar.getTimeInMillis();
        calendarView.setMaxDate(endOfMonth);
        calendarView.setMinDate(startOfMonth);


    }

    void dataFromDateOnClick(){
        Thread t = new Thread(() -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            String time = sdf.format(Calendar.getInstance().getTime());
            Cursor cursor = DB.readAllData();
            if (cursor.getCount() == 0) {
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(()->{
                    Toast.makeText(CalendarActivity.this, "No stocks", Toast.LENGTH_SHORT).show();
                });
            } else {
                while (cursor.moveToNext()) {
                    if(cursor.getString(9).startsWith(time)) {
                        symbols.add(cursor.getString(0));
                        float shares = Float.parseFloat(cursor.getString(2));
                        amountOfDividend.add(String.valueOf(cursor.getString(5)));
                        frequency.add(cursor.getString(8));
                        date.add(cursor.getString(9));
                        totalDividendAmount.add(formatter.format(shares * Float.parseFloat(cursor.getString(5))));
                    }
                }
            }
            cursor.close();
            setRecyclerView();

        });t.start();
    }

    public void setRecyclerView() {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        // Send a task to the MessageQueue of the main thread
        mainHandler.post(() -> {
            // Code will be executed on the main thread
            recViewCalendarView = new RecViewCalendarView(CalendarActivity.this, symbols, date,
                    frequency, amountOfDividend,totalDividendAmount, CalendarActivity.this);
            calendarViewRecyclerView.setAdapter(recViewCalendarView);
            calendarViewRecyclerView.setLayoutManager(new LinearLayoutManager(CalendarActivity.this));

        });
    }

    @Override
    public void onItemClick(int position) {

    }


}
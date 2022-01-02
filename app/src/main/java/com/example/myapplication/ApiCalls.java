package com.example.myapplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiCalls {
    static String date_of_dividend;
    static double amount_of_dividend,stock_price, annualDividend, Dividend_Yield,profit_loss,
            percentage_profit_loss;
    String todayy,today30,today90,todayYear,today7;
    ArrayList<String> historicalPrice30 = new ArrayList<>();
    ArrayList<String> historicalDates30 = new ArrayList<>();

    // api call to get the dividend amount and dividend yield
    public void Dividend(String s) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://yahoofinance-stocks1.p.rapidapi.com/dividends?Symbol="+s+"&OrderBy=Descending")
                .get()
                .addHeader("x-rapidapi-host", "yahoofinance-stocks1.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "1825a76a53mshde9945082d517f9p1c5c08jsn6dcc58da9fbd")
                .build();
        try {
            Response response = client.newCall(request).execute();
            String all = response.body().string();
            String allres = new JSONObject(all).getString("results");
            JSONArray t = new JSONArray(allres);
            for(int i = 0; i < 1;i++){
                JSONObject T = t.getJSONObject(i);
                amount_of_dividend = T.getDouble("amount");
                date_of_dividend = T.getString("date");
            }

                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

            }


    public void price(String s) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://twelve-data1.p.rapidapi.com/price?symbol="+s+"&format=json&outputsize=30").get()
                .addHeader("x-rapidapi-host","twelve-data1.p.rapidapi.com")
                .addHeader("x-rapidapi-key","1825a76a53mshde9945082d517f9p1c5c08jsn6dcc58da9fbd")
                .build();

        try{
            Response response = client.newCall(request).execute();
            String allres = response.body().string();
            String ally = new JSONObject(allres).getString("price");
            stock_price = Double.parseDouble(ally);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void getAnnualDividend(String freq) {
        if(freq.startsWith("M")) {
            annualDividend = amount_of_dividend * 12;
        } else if (freq.startsWith("Q")) {
            annualDividend = amount_of_dividend * 4;
        }
        else if (freq.startsWith("S")) {
            annualDividend = amount_of_dividend * 2;
        }
        else {
            annualDividend = amount_of_dividend;
        }

    }
    public static void getDividendYield() {
        double DY = annualDividend / stock_price;
        Dividend_Yield = DY * 100;
        // Rounding
        BigDecimal a = new BigDecimal(Dividend_Yield);
        BigDecimal b = a.setScale(3, RoundingMode.DOWN);
        Dividend_Yield = Double.parseDouble(String.valueOf(b));
    }

    public static void profit_loss(double shares, double costbasis) {
        double market_value = stock_price * shares;
        double cost_value = shares * costbasis;
        double pr = market_value - cost_value;
        BigDecimal l = new BigDecimal(pr);
        BigDecimal m = l.setScale(2,RoundingMode.DOWN);
        profit_loss = Double.parseDouble(String.valueOf(m));
        double p = (profit_loss/cost_value) * 100;
        // Rounding
        BigDecimal c = new BigDecimal(p);
        BigDecimal d = c.setScale(3,RoundingMode.DOWN);
        percentage_profit_loss = Double.parseDouble(String.valueOf(d));

    }

    public void historicalPrices(String s, int days) throws IOException, JSONException {
        if (historicalPrice30 != null){
            historicalPrice30.clear();
        }
        getDates();
        String date1 = "";

        if(days== 7) {
            date1 = today7;
        }
        else if(days == 30){
            date1 = today30;
        } else if (days == 90){
            date1 = today90;
        } else{
            date1 = todayYear;
        }

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://yahoofinance-stocks1.p.rapidapi.com/stock-prices?EndDateInclusive="+todayy+"&StartDateInclusive="+date1+"&Symbol="+s+"&OrderBy=Ascending")
                .get()
                .addHeader("x-rapidapi-host", "yahoofinance-stocks1.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "1825a76a53mshde9945082d517f9p1c5c08jsn6dcc58da9fbd")
                .build();

        Response response = client.newCall(request).execute();
        String all = response.body().string();
        String allres = new JSONObject(all).getString("results");
        JSONArray t = new JSONArray(allres);
        for(int i = 0; i < t.length();i++){
            JSONObject T = t.getJSONObject(i);
            String date = T.getString("date");
            String adjClose = T.getString("adjClose");
            historicalPrice30.add(adjClose);
            historicalDates30.add(date);
        }
    }

    public void getDates() {
        if (historicalDates30 != null){
            historicalDates30.clear();
        }
        Date today = new Date();
        Calendar cal = new GregorianCalendar();

        cal.setTime(today);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        String mont = "";
        if (month <10){
            mont = "0"+month;
            todayy = year+"-"+mont+"-"+day;
        }else {
            todayy = year + "-" + month + "-" + day;
        }
        //
        cal.add(Calendar.DAY_OF_MONTH, -7);
        int month7 = cal.get(Calendar.MONTH);
        int year7 = cal.get(Calendar.YEAR);
        int day7 = cal.get(Calendar.DAY_OF_MONTH);
        String mont7 = "";

        if (month7 <10){
            mont7 = "0"+month7;
            today7 = year7 + "-" + mont7 + "-" + day7;
        }else {
            today7 = year7 + "-" + month7 + "-" + day7;
        }
        //
        // get the date for precious 30 days
        cal.add(Calendar.DAY_OF_MONTH, -30);
        int month30 = cal.get(Calendar.MONTH);
        int year30 = cal.get(Calendar.YEAR);
        int day30 = cal.get(Calendar.DAY_OF_MONTH);
        String mont30 = "";

        if (month30 <10){
             mont30 = "0"+month30;
             today30 = year30 + "-" + mont30 + "-" + day30;
        }else {
            today30 = year30 + "-" + month30 + "-" + day30;
        }
        // get the date for precious 90 days
        cal.add(Calendar.DAY_OF_MONTH, -90);
        int month90 = cal.get(Calendar.MONTH);
        int year90 = cal.get(Calendar.YEAR);
        int day90 = cal.get(Calendar.DAY_OF_MONTH);
        String mont90 = "";

        if (month90 <10){
            mont90 = "0"+month90;
            today90 = year90 + "-" + mont90 + "-" + day90;
        }else {
            today90 = year90 + "-" + month90 + "-" + day90;
        }

        cal.add(Calendar.DAY_OF_MONTH, -365);
        int month365 = cal.get(Calendar.MONTH);
        int year365 = cal.get(Calendar.YEAR);
        int day365 = cal.get(Calendar.DAY_OF_MONTH);
        String mont365 = "";

        if (month90 <10){
            mont365 = "0"+month90;
            todayYear = year365 + "-" + mont365 + "-" + day365;
        }else {
            todayYear = year365 + "-" + month365 + "-" + day365;
        }

    }

}





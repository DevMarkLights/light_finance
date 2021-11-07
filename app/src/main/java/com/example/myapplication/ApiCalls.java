package com.example.myapplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiCalls {
    static String date_of_dividend;
    static double amount_of_dividend,stock_price, annualDividend, Dividend_Yield,profit_loss,
            percentage_profit_loss;

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
                // org.json.JSONException: Value 0.52 at amount of type java.lang.Double cannot be converted to JSONObject
                // String div = String.valueOf(T.getJSONObject("amount").toString());
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

    public void getAnnualDividend(String freq) {
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
    public void getDividendYield() {
        double DY = annualDividend / stock_price;
        Dividend_Yield = DY * 100;
        // Rounding
        BigDecimal a = new BigDecimal(Dividend_Yield);
        BigDecimal b = a.setScale(3, RoundingMode.DOWN);
        Dividend_Yield = Double.parseDouble(String.valueOf(b));
    }

    public void profit_loss(double shares,double costbasis) {
        double market_value = stock_price * shares;
        double cost_value = shares * costbasis;
        double pr = market_value - cost_value;
        BigDecimal l = new BigDecimal(pr);
        BigDecimal m = l.setScale(3,RoundingMode.DOWN);
        profit_loss = Double.parseDouble(String.valueOf(m));
        double p = (profit_loss/cost_value) * 100;
        // Rounding
        BigDecimal c = new BigDecimal(p);
        BigDecimal d = c.setScale(3,RoundingMode.DOWN);
        percentage_profit_loss = Double.parseDouble(String.valueOf(d));

    }


}





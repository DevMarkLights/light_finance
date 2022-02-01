package com.example.myapplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiCalls {
    static String date_of_dividend;
    static double amount_of_dividend,stock_price, annualDividend, Dividend_Yield,profit_loss,
            percentage_profit_loss, percent_Change,ytdReturn;
    String todayy,today30,today90,todayYear,today7;
    String fifty_two_week_high, fifty_two_week_low,open,close,high,low;
    ArrayList<String> recommendedSymbols = new ArrayList<>();

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
                .url("https://stock-data-yahoo-finance-alternative.p.rapidapi.com/v6/finance/quote?symbols="+s)
                .get()
                .addHeader("x-rapidapi-host", "stock-data-yahoo-finance-alternative.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "1825a76a53mshde9945082d517f9p1c5c08jsn6dcc58da9fbd")
                .build();
        try{
        Response response = client.newCall(request).execute();
        String allres = response.body().string();
        String ab = new JSONObject(allres).getString("quoteResponse");
        String al = new JSONObject(ab).getString("result");
        JSONArray t = new JSONArray(al);
        JSONObject l = new JSONObject(String.valueOf(t.get(0)));
        String k = String.valueOf(l.get("regularMarketPrice"));
        stock_price = Double.parseDouble(k);

            open = String.valueOf(l.get("regularMarketOpen"));
            BigDecimal a = new BigDecimal(open);
            BigDecimal b = a.setScale(3, RoundingMode.DOWN);
            open = String.valueOf(b);

            close = String.valueOf(l.get("regularMarketPreviousClose"));
            BigDecimal c = new BigDecimal(close);
            BigDecimal d = c.setScale(3, RoundingMode.DOWN);
            close = String.valueOf(d);

            high = String.valueOf(l.get("regularMarketDayHigh"));
            BigDecimal e = new BigDecimal(high);
            BigDecimal f = e.setScale(3, RoundingMode.DOWN);
            high = String.valueOf(f);

            low = String.valueOf(l.get("regularMarketDayLow"));
            BigDecimal g = new BigDecimal(low);
            BigDecimal h = g.setScale(3, RoundingMode.DOWN);
            low = String.valueOf(h);

            fifty_two_week_low = String.valueOf(l.get("fiftyTwoWeekLow"));
            BigDecimal i = new BigDecimal(fifty_two_week_low);
            BigDecimal j = i.setScale(3, RoundingMode.DOWN);
            fifty_two_week_low = String.valueOf(j);

            fifty_two_week_high = String.valueOf(l.get("fiftyTwoWeekHigh"));
            BigDecimal km = new BigDecimal(fifty_two_week_high);
            BigDecimal m = km.setScale(3, RoundingMode.DOWN);
            fifty_two_week_high = String.valueOf(m);


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

    public void stockQuote(String s) {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://stock-data-yahoo-finance-alternative.p.rapidapi.com/v6/finance/quote?symbols="+s)
                .get()
                .addHeader("x-rapidapi-host", "stock-data-yahoo-finance-alternative.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "1825a76a53mshde9945082d517f9p1c5c08jsn6dcc58da9fbd")
                .build();
        try{
            Response response = client.newCall(request).execute();
            String allres = response.body().string();
            String ab = new JSONObject(allres).getString("quoteResponse");
            String al = new JSONObject(ab).getString("result");
            //String all = new JSONObject(al).getString("regularMarketPrice");
            JSONArray t = new JSONArray(al);
            JSONObject l = new JSONObject(String.valueOf(t.get(0)));

            open = String.valueOf(l.get("regularMarketOpen"));
            BigDecimal a = new BigDecimal(open);
            BigDecimal b = a.setScale(3, RoundingMode.DOWN);
            open = String.valueOf(b);

            close = String.valueOf(l.get("regularMarketPreviousClose"));
            BigDecimal c = new BigDecimal(close);
            BigDecimal d = c.setScale(3, RoundingMode.DOWN);
            close = String.valueOf(d);

            high = String.valueOf(l.get("regularMarketDayHigh"));
            BigDecimal e = new BigDecimal(high);
            BigDecimal f = e.setScale(3, RoundingMode.DOWN);
            high = String.valueOf(f);

            low = String.valueOf(l.get("regularMarketDayLow"));
            BigDecimal g = new BigDecimal(low);
            BigDecimal h = g.setScale(3, RoundingMode.DOWN);
            low = String.valueOf(h);

            //fifty two week values
           // String fifty = new JSONObject(all).getString("fifty_two_week");
            //String ally = new JSONObject(fifty).getString("low");
            //String blly = new JSONObject(fifty).getString("high");

            fifty_two_week_low = String.valueOf(l.get("fiftyTwoWeekLow"));
            BigDecimal i = new BigDecimal(fifty_two_week_low);
            BigDecimal j = i.setScale(3, RoundingMode.DOWN);
            fifty_two_week_low = String.valueOf(j);

            fifty_two_week_high = String.valueOf(l.get("fiftyTwoWeekHigh"));
            BigDecimal k = new BigDecimal(fifty_two_week_high);
            BigDecimal m = k.setScale(3, RoundingMode.DOWN);
            fifty_two_week_high = String.valueOf(m);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public void similarStocks(String s) throws IOException, JSONException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://stock-data-yahoo-finance-alternative.p.rapidapi.com/v6/finance/recommendationsbysymbol/"+s)
                .get()
                .addHeader("x-rapidapi-host", "stock-data-yahoo-finance-alternative.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "1825a76a53mshde9945082d517f9p1c5c08jsn6dcc58da9fbd")
                .build();

        Response response = client.newCall(request).execute();
        String allres = response.body().string();
        String ab = new JSONObject(allres).getString("finance");
        String b = new JSONObject(ab).getString("result");
        JSONArray t = new JSONArray(b);
        JSONObject l = new JSONObject(String.valueOf(t.get(0)));
        String k = String.valueOf(l.get("recommendedSymbols"));
        JSONArray m = new JSONArray(k);
        for (int i = 0; i<m.length();i++) {
            JSONObject n = new JSONObject(String.valueOf(m.get(i)));
            String o = String.valueOf(n.get("symbol"));
            recommendedSymbols.add(o);
        }


    }

    public void regularMarketPrice(String s) throws IOException, JSONException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://stock-data-yahoo-finance-alternative.p.rapidapi.com/v6/finance/quote?symbols=" + s)
                .get()
                .addHeader("x-rapidapi-host", "stock-data-yahoo-finance-alternative.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "1825a76a53mshde9945082d517f9p1c5c08jsn6dcc58da9fbd")
                .build();
        try {
            Response response = client.newCall(request).execute();
            String allres = response.body().string();
            String ab = new JSONObject(allres).getString("quoteResponse");
            String al = new JSONObject(ab).getString("result");
            JSONArray t = new JSONArray(al);
            JSONObject l = new JSONObject(String.valueOf(t.get(0)));
            String k = String.valueOf(l.get("regularMarketPrice"));
            stock_price = Double.parseDouble(k);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void getPercentChange(String s) throws IOException, JSONException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://yahoofinance-stocks1.p.rapidapi.com/stock-metadata?Symbol="+s)
                .get()
                .addHeader("x-rapidapi-host", "yahoofinance-stocks1.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "1825a76a53mshde9945082d517f9p1c5c08jsn6dcc58da9fbd")
                .build();

        Response response = client.newCall(request).execute();
        String all = response.body().string();
        String allres = new JSONObject(all).getString("result");
        String allre = new JSONObject(allres).getString("fiftyDayAverageChangePercent");
        double t = Float.parseFloat(allre) * 100;
        BigDecimal a = new BigDecimal(t);
        BigDecimal b = a.setScale(3, RoundingMode.DOWN);
        percent_Change = Double.parseDouble(String.valueOf(b));

    }

    public void getDivYield(String s) throws IOException, JSONException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://seeking-alpha.p.rapidapi.com/symbols/get-key-data?symbol="+s)
                .get()
                .addHeader("x-rapidapi-host", "seeking-alpha.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "1825a76a53mshde9945082d517f9p1c5c08jsn6dcc58da9fbd")
                .build();

        Response response = client.newCall(request).execute();
        String all = response.body().string();
        String allres = new JSONObject(all).getString("data");
        JSONArray t = new JSONArray(allres);
        JSONObject l = new JSONObject(String.valueOf(t.get(0)));
        String k = String.valueOf(l.get("attributes"));
        String m = new JSONObject(k).getString("divYield");
        if (m == null) {
            Dividend_Yield = 0.0;
        } else {
            Dividend_Yield = Double.parseDouble(m);
        }



    }
}





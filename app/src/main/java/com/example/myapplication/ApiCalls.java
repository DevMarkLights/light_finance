package com.example.myapplication;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/*
if the api call does not have a response check and see how many request are being made per second
 */
public class ApiCalls {
    //----------------------=
    // variables for adding stock to database
    static String date_of_dividend,frequency;
    static double amount_of_dividend, stock_price, annualDividend, Dividend_Yield, profit_loss,oneDayPercentChange;
    //-----------------------

    static double percentage_profit_loss;
    static double stock_price_for_updating_single_stock;
    String fifty_two_week_high, fifty_two_week_low,open,close,high,low;
    //-----------------
    // variables for recommended symbols
    static ArrayList<String> recommendedSymbols = new ArrayList<>();
    static ArrayList<Float> priceSim = new ArrayList<>();
    static ArrayList<Float> percentChange = new ArrayList<>();
    static double DivYieldSim,DivAmountSim,annualDividendSimStockStocks,percent_Change,stockPriceSimStocks;
    static String FreqSim;
    //------------------

    // for checking if the stock exist in the stock market
    boolean stockExists = true;
    //----------------------

    //------------------------------
    // values for portfolio update
    static double amountOfDividendU,stockPriceU,dividendYieldU,profitLossU,annualDividendUpdate,marketValueU;
    static String dateOfDividendU,frequencyU;
    //------------------------------
    static DecimalFormat formatter = new DecimalFormat("#,###.00");
    static DecimalFormat formatterNoComma = new DecimalFormat("####.00");

    // checks to see if the symbol is exist in the stock market / alternative yahoo api call
    public void checkSymbol (String s){
        stockExists = true;
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://stock-data-yahoo-finance-alternative.p.rapidapi.com/v6/finance/quote?symbols=" + s)
                .get()
                .addHeader("x-rapidapi-host", "stock-data-yahoo-finance-alternative.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "1825a76a53mshde9945082d517f9p1c5c08jsn6dcc58da9fbd")
                .build();
        try {
            Response response = client.newCall(request).execute();
            String allres = Objects.requireNonNull(response.body()).string();
            String ab = new JSONObject(allres).getString("quoteResponse");
            String al = new JSONObject(ab).getString("result");
            int T = al.length();
            if(T < 3){
                stockExists = false;
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    // get the dividend and the ex-div date / yahoo finance api
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
            String all = Objects.requireNonNull(response.body()).string();
            String allres = new JSONObject(all).getString("results");
            String check = new JSONObject(all).getString("total");
            if(check.equals("0")) {
                amount_of_dividend = 0.0;
                date_of_dividend = null;
                return;
            }
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

    // gets just the stock price for a stock / yahoo finance api
    public static void getOnlyStockPrice(String s) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://yahoofinance-stocks1.p.rapidapi.com/stock-metadata?Symbol="+s)
                .get()
                .addHeader("x-rapidapi-host", "yahoofinance-stocks1.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "1825a76a53mshde9945082d517f9p1c5c08jsn6dcc58da9fbd")
                .build();
        try{
            Response response = client.newCall(request).execute();
            String allres = Objects.requireNonNull(response.body()).string();
            String ab = new JSONObject(allres).getString("result");
            String k = new JSONObject(ab).getString("regularMarketPrice");
            stock_price = Double.parseDouble(k);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

    }

    // gets the stock price for updating the stocks in the user's database /  mboum finance
    public static void getOnlyStockPriceForSingleStockUpdating(String s) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://mboum-finance.p.rapidapi.com/qu/quote?symbol="+s)
                .get()
                .addHeader("x-rapidapi-host", "mboum-finance.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "1825a76a53mshde9945082d517f9p1c5c08jsn6dcc58da9fbd")
                .build();
        try {
            Response response = client.newCall(request).execute();
            String allres = Objects.requireNonNull(response.body()).string();
            JSONArray t = new JSONArray(allres);
            JSONObject l = new JSONObject(String.valueOf(t.get(0)));
            String k = String.valueOf(l.get("regularMarketPrice"));
            stock_price_for_updating_single_stock = Double.parseDouble(k);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

    }

    // multiplies the dividend amount by the frequency
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

    // divides the annual dividend by the stock price to get the dividend yield
    public static void getDividendYield() {
        double DY = annualDividend / stock_price;
        Dividend_Yield = DY * 100;
        // Rounding
        BigDecimal a = new BigDecimal(Dividend_Yield);
        BigDecimal b = a.setScale(3, RoundingMode.DOWN);
        Dividend_Yield = Double.parseDouble(String.valueOf(b));
    }

    // gets the market value of the current stock price with user's amount of shares. then compares it
    // with the cost value. which is shares * the average price paid per stock
    public static void profit_loss(double shares, double costbasis) {
        double market_value = stock_price_for_updating_single_stock * shares;
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

    // gets the open, close, high,low, 52 week high, and 52 week low prices of a stock for the day / yahoo alternative
    public void stockQuote(String s) {
        open = "";
        close = "";
        high = "";
        low = "";
        fifty_two_week_low = "";
        fifty_two_week_high = "";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://stock-data-yahoo-finance-alternative.p.rapidapi.com/v6/finance/quote?symbols="+s)
                .get()
                .addHeader("x-rapidapi-host", "stock-data-yahoo-finance-alternative.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "1825a76a53mshde9945082d517f9p1c5c08jsn6dcc58da9fbd")
                .build();
        try{
            Response response = client.newCall(request).execute();
            String allres = Objects.requireNonNull(response.body()).string();
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

    // gets recommended stocks based off the stock on the line chart activity / yahoo alternative
    public void similarStocks(String s) throws IOException, JSONException {
        recommendedSymbols.clear();
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://stock-data-yahoo-finance-alternative.p.rapidapi.com/v6/finance/recommendationsbysymbol/"+s)
                .get()
                .addHeader("x-rapidapi-host", "stock-data-yahoo-finance-alternative.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "1825a76a53mshde9945082d517f9p1c5c08jsn6dcc58da9fbd")
                .build();

        Response response = client.newCall(request).execute();
        String allres = Objects.requireNonNull(response.body()).string();
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

    // another stock price api call to reduce how many calls are being done on one api a second and also trying to spread
    // the usage around so it can be less costly / seeking alpha
    public static void regularMarketPrice(String s) throws IOException, JSONException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://twelve-data1.p.rapidapi.com/quote?symbol="+s+"&interval=1day&outputsize=30&format=json")
                .get()
                .addHeader("x-rapidapi-host", "twelve-data1.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "1825a76a53mshde9945082d517f9p1c5c08jsn6dcc58da9fbd")
                .build();
        try{
            Response response = client.newCall(request).execute();
            String allres = Objects.requireNonNull(response.body()).string();
            String ab = new JSONObject(allres).getString("close");
            String m = formatter.format(Double.parseDouble(ab));
            stock_price = Double.parseDouble(m);
            String k = new JSONObject(allres).getString("percent_change");
            String t = formatter.format(Double.parseDouble(k));
            oneDayPercentChange = Double.parseDouble(t);
        } catch (IOException | JSONException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    // gets the percent change of a stock in one day compared to its open amount. yahoo alternative api
    public void getPercentChange(String s) throws IOException, JSONException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://stock-data-yahoo-finance-alternative.p.rapidapi.com/v6/finance/quote?symbols="+s)
                .get()
                .addHeader("x-rapidapi-host", "stock-data-yahoo-finance-alternative.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "1825a76a53mshde9945082d517f9p1c5c08jsn6dcc58da9fbd")
                .build();

        Response response = client.newCall(request).execute();
        String all = Objects.requireNonNull(response.body()).string();
        String allres = new JSONObject(all).getString("quoteResponse");
        String al = new JSONObject(allres).getString("result");
        JSONArray t = new JSONArray(al);
        JSONObject l = new JSONObject(String.valueOf(t.get(0)));
        String allre = String.valueOf(l.get("regularMarketChangePercent"));
        BigDecimal a = new BigDecimal(allre);
        BigDecimal b = a.setScale(3, RoundingMode.DOWN);
        percent_Change = Double.parseDouble(String.valueOf(b));

    }

    // gets the dividend yield and annual dividend amount for 1 share / seeking alpha
    public void getDivYield(String s) throws IOException, JSONException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://seeking-alpha.p.rapidapi.com/symbols/get-key-data?symbol="+s)
                .get()
                .addHeader("x-rapidapi-host", "seeking-alpha.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "1825a76a53mshde9945082d517f9p1c5c08jsn6dcc58da9fbd")
                .build();

        Response response = client.newCall(request).execute();
        String all = Objects.requireNonNull(response.body()).string();
        String allres = new JSONObject(all).getString("data");
        JSONArray t = new JSONArray(allres);
        JSONObject l = new JSONObject(String.valueOf(t.get(0)));
        String k = String.valueOf(l.get("attributes"));
        String m = new JSONObject(k).getString("divYield");
        String n = new JSONObject(k).getString("divRate");
        if (m.isEmpty() || m.equals("null")) {
            Dividend_Yield = 0.0;
        } else {
            Dividend_Yield = Double.parseDouble(m);
            annualDividendSimStockStocks = Double.parseDouble(n);
        }


    }

    // created this method to simplify adding a stock to the database with less api calls which in tails speeds
    // the time it takes / seeking alpha
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void simplifiedDividendForAddingStocks(String symbol) throws IOException, JSONException {
        // get the date to check the dividend dates
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        String thisMonth = dtf.format(now);
        // if the dividend stock is a quarterly stock, I need to get the dates before the current one for
        // accurate dividend dates
        LocalDate now2 = LocalDate.now();
        String oneMonthBefore = String.valueOf(now2.minusMonths(1));
        String twoMonthBefore = String.valueOf(now2.minusMonths(2));

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://seeking-alpha.p.rapidapi.com/symbols/get-dividend-history?symbol="+symbol+"&years=1&group_by=month")
                .get()
                .addHeader("x-rapidapi-host", "seeking-alpha.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "1825a76a53mshde9945082d517f9p1c5c08jsn6dcc58da9fbd")
                .build();

        Response response = client.newCall(request).execute();
        String all = Objects.requireNonNull(response.body()).string();
        String allres = new JSONObject(all).getString("data");
        JSONArray t = new JSONArray(allres);
        // gets the dividend yeild for this month
        for (int i = t.length()-1; i >= 0; i--){
            JSONObject l = new JSONObject(String.valueOf(t.get(i)));
            String k = String.valueOf(l.get("attributes"));
            String m = new JSONObject(k).getString("pay_date");
            String n = m.substring(0, 7);
            if(thisMonth.startsWith(n) || oneMonthBefore.startsWith(n) || twoMonthBefore.startsWith(n)){
                amount_of_dividend = Double.parseDouble(new JSONObject(k).getString("amount"));
                frequency = new JSONObject(k).getString("freq");
                date_of_dividend = new JSONObject(k).getString("pay_date");
                getAnnualDividend(frequency);
                getOnlyStockPrice(symbol);
                getDividendYield();
                break;
            }
        }

    }

    // method for simStockLineChartView
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void getDivYieldSimStocks(String s) throws IOException, JSONException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        String thisMonth = dtf.format(now);
        // if the dividend stock is a quarterly stock, I need to get the dates before the current one for
        // accurate dividend dates
        LocalDate now2 = LocalDate.now();
        String oneMonthBefore = String.valueOf(now2.minusMonths(1));
        String twoMonthBefore = String.valueOf(now2.minusMonths(2));

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://seeking-alpha.p.rapidapi.com/symbols/get-dividend-history?symbol="+s+"&years=1&group_by=month")
                .get()
                .addHeader("x-rapidapi-host", "seeking-alpha.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "1825a76a53mshde9945082d517f9p1c5c08jsn6dcc58da9fbd")
                .build();

        Response response = client.newCall(request).execute();
        String all = Objects.requireNonNull(response.body()).string();
        String allres = new JSONObject(all).getString("data");
        if(allres.length() < 3){
            DivYieldSim = 0.0;
            annualDividendSimStockStocks = 0.0;
            return;
        }
        JSONArray t = new JSONArray(allres);
        for (int i = t.length()-1; i >= 0; i--){
            JSONObject l = new JSONObject(String.valueOf(t.get(i)));
            String k = String.valueOf(l.get("attributes"));
            String m = new JSONObject(k).getString("pay_date");
            String n = m.substring(0, 7);
            if(thisMonth.startsWith(n) || oneMonthBefore.startsWith(n) || twoMonthBefore.startsWith(n)){
                DivAmountSim = Double.parseDouble(new JSONObject(k).getString("amount"));
                FreqSim = new JSONObject(k).getString("freq");
                getAnnualDividendSim(FreqSim);
                getDividendYieldSim();
                break;
            }
        }

    } // seeking alpha
    public static void getDividendYieldSim() {
        String DY = formatterNoComma.format(Double.parseDouble(String.valueOf(annualDividendSimStockStocks)) / Double.parseDouble(String.valueOf(stockPriceSimStocks)));
        DivYieldSim = Double.parseDouble(DY) * 100;
        // Rounding
        BigDecimal a = new BigDecimal(DivYieldSim);
        BigDecimal b = a.setScale(3, RoundingMode.DOWN);
        DivYieldSim = Double.parseDouble(String.valueOf(b));
    }
    public static void getOnlyStockPriceSim() throws IOException, JSONException {
        priceSim.clear();
        percentChange.clear();
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://stock-data-yahoo-finance-alternative.p.rapidapi.com/v6/finance/quote?symbols="+recommendedSymbols.get(0)+"%2C"+recommendedSymbols.get(1)+"%2C"+
                        recommendedSymbols.get(2)+"%2C"+recommendedSymbols.get(3)+"%2C"+recommendedSymbols.get(4))
                .get()
                .addHeader("x-rapidapi-host", "stock-data-yahoo-finance-alternative.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "1825a76a53mshde9945082d517f9p1c5c08jsn6dcc58da9fbd")
                .build();


        Response response = client.newCall(request).execute();
        String allres = Objects.requireNonNull(response.body()).string();
        String ab = new JSONObject(allres).getString("quoteResponse");
        String al = new JSONObject(ab).getString("result");
        JSONArray t = new JSONArray(al);
        for (int i = 0; i < t.length(); i++){
            JSONObject l = new JSONObject(String.valueOf(t.get(i)));
            priceSim.add(Float.parseFloat(String.valueOf(l.get("regularMarketPrice"))));
            percentChange.add(Float.parseFloat(formatter.format(Double.parseDouble(String.valueOf(l.get("regularMarketChangePercent"))))));
        }


    } // yahoo alternative
    public static void getAnnualDividendSim(String freq) {
        if(freq.startsWith("M")) {
            annualDividendSimStockStocks = DivAmountSim * 12;
        } else if (freq.startsWith("Q")) {
            annualDividendSimStockStocks = DivAmountSim * 4;
        }
        else if (freq.startsWith("S")) {
            annualDividendSimStockStocks = DivAmountSim * 2;
        }
        else {
            annualDividendSimStockStocks = DivAmountSim;
        }

        BigDecimal a = new BigDecimal(annualDividendSimStockStocks);
        BigDecimal b = a.setScale(3, RoundingMode.DOWN);
        annualDividendSimStockStocks = Double.parseDouble(String.valueOf(b));


    }
    //-----------------------



    // methods for updating stocks in the portfolio in the background
    public static void getOnlyStockPriceUpdate(String s) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://mboum-finance.p.rapidapi.com/qu/quote?symbol="+s)
                .get()
                .addHeader("x-rapidapi-host", "mboum-finance.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "1825a76a53mshde9945082d517f9p1c5c08jsn6dcc58da9fbd")
                .build();
        try {
            Response response = client.newCall(request).execute();
            String allres = Objects.requireNonNull(response.body()).string();
            JSONArray t = new JSONArray(allres);
            JSONObject l = new JSONObject(String.valueOf(t.get(0)));
            String k = String.valueOf(l.get("regularMarketPrice"));
            stockPriceU = Double.parseDouble(k);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

    } // mboum finance

    public static void profitLossUpdate(double shares, double costbasis) {
        marketValueU = stockPriceU * shares;
        BigDecimal a = new BigDecimal(marketValueU);
        BigDecimal b = a.setScale(2,RoundingMode.DOWN);
        marketValueU = Double.parseDouble(String.valueOf(b));
        double cost_value = shares * costbasis;
        double pr = marketValueU - cost_value;
        BigDecimal l = new BigDecimal(pr);
        BigDecimal m = l.setScale(2,RoundingMode.DOWN);
        profitLossU = Double.parseDouble(String.valueOf(m));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void dividendYieldUpdate(String s, double shares) throws IOException, JSONException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        String thisMonth = dtf.format(now);
        // if the dividend stock is a quarterly stock, I need to get the dates before the current one for
        // accurate dividend dates
        LocalDate now2 = LocalDate.now();
        String oneMonthBefore = String.valueOf(now2.minusMonths(1));
        String twoMonthBefore = String.valueOf(now2.minusMonths(2));

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://seeking-alpha.p.rapidapi.com/symbols/get-dividend-history?symbol="+s+"&years=1&group_by=month")
                .get()
                .addHeader("x-rapidapi-host", "seeking-alpha.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "1825a76a53mshde9945082d517f9p1c5c08jsn6dcc58da9fbd")
                .build();

        Response response = client.newCall(request).execute();
        String all = Objects.requireNonNull(response.body()).string();
        String allres = new JSONObject(all).getString("data");
        JSONArray t = new JSONArray(allres);
        for (int i = t.length()-1; i >= 0; i--){
            JSONObject l = new JSONObject(String.valueOf(t.get(i)));
            String k = String.valueOf(l.get("attributes"));
            String m = new JSONObject(k).getString("pay_date");
            String n = m.substring(0, 7);
            if(thisMonth.startsWith(n) || oneMonthBefore.startsWith(n) || twoMonthBefore.startsWith(n)){
                amountOfDividendU = Double.parseDouble(new JSONObject(k).getString("amount"));
                frequencyU = new JSONObject(k).getString("freq");
                dateOfDividendU = new JSONObject(k).getString("pay_date");
                getAnnualDividendUpdate(frequencyU);
                getOnlyStockPriceUpdate(s);
                getDividendYieldUpdate();
                break;
            }
        }

    } // seeking alpha

    public static void getDividendYieldUpdate() {
        double DY = annualDividendUpdate / stockPriceU;
        dividendYieldU = DY * 100;
        // Rounding
        BigDecimal a = new BigDecimal(dividendYieldU);
        BigDecimal b = a.setScale(2, RoundingMode.DOWN);
        dividendYieldU = Double.parseDouble(String.valueOf(b));
    }

    public static void getAnnualDividendUpdate(String freq) {
        double upd;
        if(freq.startsWith("M")) {
            upd = amountOfDividendU * 12;
        } else if (freq.startsWith("Q")) {
            upd = amountOfDividendU * 4;
        }
        else if (freq.startsWith("S")) {
            upd = amountOfDividendU * 2;
        }
        else {
            upd = amountOfDividendU;
        }

        annualDividendUpdate = Double.parseDouble(formatter.format(upd));

    }

}





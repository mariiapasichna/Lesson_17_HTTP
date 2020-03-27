package com.mariiapasichna;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/*
1) Написать консольную программу которая спрашивает у пользователя дату в формате 25.03.2020; И выводит курс доллара
за эту дату или описание ошибки, используя HttpURLConnection.
2*) То же самое но для запросов использовать библиотеку Okhttp3
*/

public class Task1_2 {

    public static final String URL = "https://api.privatbank.ua/p24api/exchange_rates?json&date=";
    public static final String INPUT_DATE_FORMAT = "dd.MM.yyyy";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please, enter a date in the pattern \"dd.mm.yyyy\"");
        String date = scanner.nextLine();
        String inputDate = checkDate(date);
        if (inputDate != null){
            requestHttpURLConnection(inputDate);
            requestOkHttp(inputDate);
        }
    }

    private static void requestHttpURLConnection(String date) {
        Response response = HttpUtil.sendRequest(URL + date);
        Gson gson = new Gson();
        Rate rate = gson.fromJson(response.body, Rate.class);
        if (rate != null && response.getException() == null) {
            printCurrency(rate);
        } else {
            System.out.println("Request failed: " + response.getException() + " Response code: " + response.getResponseCode());
        }
    }

    private static void requestOkHttp(String date) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(URL + date)
                    .build();
            okhttp3.Response response = client.newCall(request).execute();
            Gson gson = new Gson();
            Rate rate = gson.fromJson(response.body().string(), Rate.class);
            if (rate != null && response.isSuccessful()) {
                printCurrency(rate);
            } else {
                System.out.println("Request failed: " + response.message() + " Response code: " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printCurrency(Rate rate) {
        System.out.println("Date: " + rate.date);
        if (rate.exchangeRate.size() == 0) {
            System.out.println("The exchange rate for this date is not set");
        }
        for (Currency currency : rate.exchangeRate) {
            if (currency.currency != null && currency.currency.equals("USD")) {
                System.out.println(currency);
            }
        }
    }

    private static String checkDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(INPUT_DATE_FORMAT);
        try {
            Date dateInput = dateFormat.parse(date);
            return dateFormat.format(dateInput);
        } catch (ParseException e) {
            System.out.println("Unparseable date: " + date + ". Please, enter a date in the pattern \"dd.mm.yyyy\"");
        }
        return null;
    }

    public static class Currency {
        String currency;
        double saleRate;
        double purchaseRate;

        @Override
        public String toString() {
            return "Currency:      " + currency +
                    "\nSale rate:     " + saleRate +
                    "\nPurchase rate: " + purchaseRate;
        }
    }

    public static class Rate {
        String date;
        List<Currency> exchangeRate;
    }
}
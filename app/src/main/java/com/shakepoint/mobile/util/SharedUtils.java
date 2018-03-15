package com.shakepoint.mobile.util;

import android.content.Context;

import com.google.gson.Gson;
import com.shakepoint.mobile.SearchMachineActivity;
import com.shakepoint.mobile.data.internal.CardInfo;
import com.shakepoint.mobile.data.res.MachineSearchResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created by jose.rubalcaba on 02/13/2018.
 */

public class SharedUtils {

    public static final DateFormat LOCAL_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");
    public static final DateFormat LOCAL_SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    public static final DateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");


    static {
        LOCAL_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT-7"));
    }

    private static final String AUTH_TOKEN = "shakepoint-auth-token";
    private static final String CARD_INFO = "shakepoint-internal-card-info";
    private static final String PREFERED_MACHINE = "shakepoint-prefered-machine";
    private static final String PREFS = "shakepoint-prefs";

    public static final String getAuthenticationToken(Context cxt) {
        final String token = cxt.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(AUTH_TOKEN, "");
        return token;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static final void setAuthenticationToken(Context cxt, String token) {
        cxt.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putString(AUTH_TOKEN, token)
                .commit();
    }

    public static String getAuthenticationHeader(Context cxt) {
        return "Basic " + getAuthenticationToken(cxt);
    }

    public static void setPreferredMachine(Context cxt, MachineSearchResponse resp) {
        cxt.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putString(PREFERED_MACHINE, new Gson().toJson(resp))
                .commit();
    }

    public static MachineSearchResponse getPreferredMachine(Context cxt) {
        return new Gson().fromJson(cxt.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString(PREFERED_MACHINE, ""), MachineSearchResponse.class);
    }

    public static void clear(Context cxt) {
        cxt.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .commit();
    }

    public static CardInfo getCardInfo(Context cxt) {
        return new Gson().fromJson(cxt.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(CARD_INFO, ""), CardInfo.class);
    }

    public static void saveCardInfo(Context cxt, String cardNumber, String cardExpirationDate) {
        cxt.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putString(CARD_INFO, new Gson().toJson(new CardInfo(cardNumber, cardExpirationDate.replaceAll("/", ""))))
                .commit();
    }
}

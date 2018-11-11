package com.shakepoint.mobile.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import com.google.gson.Gson;
import com.shakepoint.mobile.SearchMachineActivity;
import com.shakepoint.mobile.data.internal.CardInfo;
import com.shakepoint.mobile.data.internal.SecurityRole;
import com.shakepoint.mobile.data.res.MachineSearchResponse;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Created by jose.rubalcaba on 02/13/2018.
 */

public class SharedUtils {

    public static final DateFormat LOCAL_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");
    public static final DateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    public static final DateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    public static final String ADMIN_ROLE = "ADMIN";

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$", Pattern.CASE_INSENSITIVE);


    static {
        LOCAL_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT-7"));
    }

    private static final String AUTH_TOKEN = "shakepoint-auth-token";
    private static final String ACCEPTED_TERMS = "shakepoint-terms-accepted";
    private static final String AUTH_ROLE = "shakepoint-auth-role";
    private static final String CARD_INFO = "shakepoint-internal-card-info";
    private static final String PREFERED_MACHINE = "shakepoint-prefered-machine";
    private static final String PREFS = "shakepoint-prefs";
    private static final String PREFS_TERMS = "shakepoint-terms";

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

    public static final void setAuthenticationToken(Context cxt, String token, String role) {
        cxt.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putString(AUTH_TOKEN, token)
                .putString(AUTH_ROLE, role)
                .commit();
    }

    public static final void setAcceptedTerms(Context cxt, Boolean accepted) {
        cxt.getSharedPreferences(PREFS_TERMS, Context.MODE_PRIVATE)
                .edit()
                .putString(ACCEPTED_TERMS, String.valueOf(accepted))
                .commit();
    }

    public static final Boolean areTermsAccepted(Context cxt) {
        return Boolean.parseBoolean(cxt.getSharedPreferences(PREFS_TERMS, Context.MODE_PRIVATE).getString(ACCEPTED_TERMS, "false"));
    }

    public static SecurityRole getAuthenticationRole(Context cxt) {
        return SecurityRole.fromString(cxt.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString(AUTH_ROLE, ""));
    }

    public static String getAuthenticationHeader(Context cxt) {
        return getAuthenticationToken(cxt);
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

    public static boolean isEmailValid(String email){
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static Integer getAge(String current) {
        try {
            Date birthdate = SharedUtils.SIMPLE_DATE_FORMAT.parse(current);
            Calendar dob = Calendar.getInstance();
            dob.setTime(birthdate);
            Calendar today = Calendar.getInstance();
            int curYear = today.get(Calendar.YEAR);
            int dobYear = dob.get(Calendar.YEAR);
            int age = curYear - dobYear;
            // if dob is month or day is behind today's month or day
            // reduce age by 1
            int curMonth = today.get(Calendar.MONTH);
            int dobMonth = dob.get(Calendar.MONTH);

            if (dobMonth > curMonth) { // this year can't be counted!
                age--;
            } else if (dobMonth == curMonth) { // same month? check for day
                int curDay = today.get(Calendar.DAY_OF_MONTH);
                int dobDay = dob.get(Calendar.DAY_OF_MONTH);
                if (dobDay > curDay) { // this year can't be counted!
                    age--;
                }
            }
            return age;

        } catch (ParseException ex) {
            return 0;
        }
    }

    public static File saveBitmap(Bitmap bitmap, Context cxt) {
        FileOutputStream out = null;
        File file = getOutputMediaFile(cxt);
        try {

            out = new FileOutputStream(file.getAbsolutePath());
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(Context cxt){
        File mediaFile = new File(cxt.getFilesDir(), UUID.randomUUID().toString() + ".jpg");
        FileOutputStream fos;

        try{
            fos = cxt.openFileOutput(mediaFile.getName(), Context.MODE_PRIVATE);
            fos.close();
            return mediaFile;
        } catch(IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static final String getHexColorFromInteger(int color) {
        return String.format("#%06X", (0xFFFFFF & color));
    }
}

package com.example.oakkub.jobintern.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by OaKKuB on 8/25/2015.
 */
public class UtilMethod {

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static String capitalize(String stringToBeCapitalized) {
        return stringToBeCapitalized.substring(0, 1).toUpperCase() + stringToBeCapitalized.substring(1).toLowerCase();
    }

    public static String getPreferredDateFormat(Date dateToBeFormatted) {

        SimpleDateFormat preferredDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getAvailableLocales()[Util.TH_LOCALE]);
        Date date = dateToBeFormatted;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, 543);

        date = calendar.getTime();

        return preferredDateFormat.format(date);
    }

    public static String getPreferredDateFormat(String dateToBeFormatted) {

        if (dateToBeFormatted.equalsIgnoreCase("")) return "";

        Date date = null;
        SimpleDateFormat defaultDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getAvailableLocales()[Util.TH_LOCALE]);

        /*Log.i("Default Locale", String.valueOf(Locale.getDefault()));
        for(String ISOLanguage: Locale.getISOLanguages())
            Log.i("ISO Languages", ISOLanguage);
        for(String ISOCountry: Locale.getISOCountries())
            Log.i("ISO Countries", ISOCountry);
        for(int i = 0, size = Locale.getAvailableLocales().length; i < size; i++)
            Log.i("Available Local", "Index: " + i + ": " +
                    String.valueOf(Locale.getAvailableLocales()[i]));*/

        try {
            date = defaultDateFormat.parse(dateToBeFormatted);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return getPreferredDateFormat(date);
    }

    public static void hideSoftKeyboard(Context context) {

        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        View view = ((Activity) context).getCurrentFocus();
        if (view == null) return;

        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}

package com.aryupay.helpingapp.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.util.TypedValue;


import com.aryupay.helpingapp.R;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Created by Karan Brahmaxatriya on 20-Sept-18.
 */
public class GlobalMethods {
    public static SimpleDateFormat dd_MM_yyyy = new SimpleDateFormat("dd-MM-yyyy");


    public static void showAlertDialog(Context context, String title, String message) {
        showAlertDialog(context, title, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    public static void showAlertDialog(Context context, String title, String message, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
        builder.setTitle(title);
        builder.setMessage(message);
        String positiveText = "OK";
        builder.setPositiveButton(positiveText, onClickListener);
//        String positiveText = "OK";
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void showConfirmDialog(Context context, final String message, DialogInterface.OnClickListener positiveClickListener) {
        showConfirmDialog(context, message, positiveClickListener, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    public static void showConfirmDialog(Context context, final String message, DialogInterface.OnClickListener positiveClickListener, DialogInterface.OnClickListener negativeClickListener) {
        final AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
//        builderSingle.setTitle("Confirm");
        builderSingle.setMessage(message);

        builderSingle.setPositiveButton("Yes", positiveClickListener);
        builderSingle.setNegativeButton("No", negativeClickListener);
        builderSingle.show();
    }

    public static String getCurrentDate() {
        return getCurrentDate(dd_MM_yyyy);
    }

    public static String getCurrentDate(SimpleDateFormat dateFormat) {
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    public static Date getParseDate(String date) {
        return getParseDate(date, dd_MM_yyyy);
    }

    public static Date getParseDate(String date, SimpleDateFormat dateFormat) {
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return Calendar.getInstance().getTime();
        }
    }

    public static String formatDate(String date, SimpleDateFormat readFormat, SimpleDateFormat outFormat) {
        try {
            return outFormat.format(readFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return date;
        }
    }

    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();

    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "G");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    public static String format(String value) {
        try {
            return format(Long.parseLong(value));
        } catch (Exception ex) {
            ex.printStackTrace();
            return format(10);
        }
    }

    public static String format(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) {
            return format(Long.MIN_VALUE + 1);
        }
        if (value < 0) {
            return "-" + format(-value);
        }
        if (value < 1000) {
            return Long.toString(value); //deal with easy case
        }

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    public static String formatDouble(double value) {
        NumberFormat mNumberFormat = NumberFormat.getInstance();
        mNumberFormat.setMinimumFractionDigits(2);
        mNumberFormat.setMaximumFractionDigits(2);

        return mNumberFormat.format(value);
    }

    public static int dpToSp(float dp, Context context) {
        return (int) (dpToPx(dp, context) / context.getResources().getDisplayMetrics().scaledDensity);
    }

    public static int dpToPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static int spToPx(float sp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }
}
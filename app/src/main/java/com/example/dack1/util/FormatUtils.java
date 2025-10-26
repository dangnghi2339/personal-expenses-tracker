package com.example.dack1.util;

import android.content.Context;
import android.text.TextUtils;

import java.text.NumberFormat;
import java.util.Locale;

public class FormatUtils {

    public static String formatCurrency(double amount) {
        try {
            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            return format.format(amount);
        } catch (Exception e) {
            return String.format(Locale.getDefault(), "%,.0f VND", amount);
        }
    }

    public static int getDrawableIdByName(Context context, String name) {
        if (context == null || TextUtils.isEmpty(name)) return 0;
        return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
    }

    public static int parseColorSafe(String colorHex) {
        try {
            return android.graphics.Color.parseColor(colorHex);
        } catch (Exception e) {
            return 0;
        }
    }
}



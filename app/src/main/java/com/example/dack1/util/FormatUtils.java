package com.example.dack1.util;

import android.content.Context;
import android.text.TextUtils;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    /**
     * Định dạng timestamp (long) thành chuỗi "Tháng Năm" (ví dụ: "October 2025").
     * @param timestamp Unix timestamp in milliseconds.
     * @return Formatted string or empty string if timestamp is invalid.
     */
    public static String formatMonthYear(long timestamp) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(timestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault()); // Locale.US hoặc Locale.getDefault()
            return sdf.format(cal.getTime());
        } catch (Exception e) {
            return ""; // Trả về chuỗi rỗng nếu có lỗi
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



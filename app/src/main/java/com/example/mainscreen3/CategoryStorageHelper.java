package com.example.mainscreen3;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CategoryStorageHelper {
    private static final String PREFS_NAME = "CategoryPrefs";
    private static final String KEY_CATEGORIES = "CustomCategories";
    private static final Gson gson = new Gson();

    // Hàm LƯU danh sách
    public static void saveCategories(Context context, List<CategoryModel> categories) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Chuyển List<CategoryModel> thành String JSON
        String json = gson.toJson(categories);

        editor.putString(KEY_CATEGORIES, json);
        editor.apply();
    }

    // Hàm TẢI danh sách
    public static List<CategoryModel> loadCategories(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_CATEGORIES, null);

        if (json == null) {
            // Nếu chưa có gì, trả về danh sách rỗng
            return new ArrayList<>();
        }

        // Chuyển String JSON ngược lại thành List<CategoryModel>
        Type type = new TypeToken<ArrayList<CategoryModel>>() {}.getType();
        return gson.fromJson(json, type);
    }
}

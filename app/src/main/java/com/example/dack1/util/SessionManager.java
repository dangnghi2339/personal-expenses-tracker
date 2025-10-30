package com.example.dack1.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.dack1.ui.view.LoginActivity;

public class SessionManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "UserSession";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_EMAIL = "email";


    private static final String KEY_LANGUAGE = "KEY_LANGUAGE";
    private static final String KEY_NAME = "KEY_NAME";
    public static final String KEY_CURRENCY_CODE = "KEY_CURRENCY_CODE";
    public static final String KEY_CURRENCY_SYMBOL = "KEY_CURRENCY_SYMBOL";

    private static final String KEY_NOTIFY_ENABLED = "KEY_NOTIFY_ENABLED";


    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(int userId, String email){
        editor.putBoolean(IS_LOGIN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_EMAIL, email);
        editor.apply(); // Dùng apply()
    }

    public boolean checkLogin(){
        if (!this.isLoggedIn()){
            Intent i = new Intent(_context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
            return false; // User không đăng nhập
        }
        return true; // User đã đăng nhập
    }

    public void logoutUser(){
        editor.clear();
        editor.apply(); // Dùng apply()

        Intent i = new Intent(_context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    public String getUserEmail() {
        return pref.getString(KEY_EMAIL, null);
    }

    public int getUserId() {
        return pref.getInt(KEY_USER_ID, -1); // Trả về -1 nếu không tìm thấy
    }

    // ======== CÁC HÀM MỚI ĐƯỢC THÊM VÀO ========

    /**
     * Lưu ngôn ngữ người dùng chọn
     */
    public void setLanguage(String languageCode) {
        editor.putString(KEY_LANGUAGE, languageCode);
        editor.apply();
    }

    /**
     * Lấy ngôn ngữ đã lưu, mặc định là "vi" (Tiếng Việt)
     */
    public String getLanguage() {
        return pref.getString(KEY_LANGUAGE, "vi");
    }

    /**
     * Lưu tên người dùng khi đăng nhập
     */
    public void setUserName(String name) {
        editor.putString(KEY_NAME, name);
        editor.apply();
    }

    /**
     * Lấy tên người dùng (dùng cho ProfileFragment)
     */
    public String getUserName() {
        return pref.getString(KEY_NAME, null); // Trả về null nếu không có tên
    }
    // ======== THÊM CÁC HÀM MỚI CHO TIỀN TỆ ========

    /**
     * Lưu loại tiền tệ (Vd: "VND", "đ")
     */
    public void setCurrency(String code, String symbol) {
        editor.putString(KEY_CURRENCY_CODE, code);
        editor.putString(KEY_CURRENCY_SYMBOL, symbol);
        editor.apply();
    }

    /**
     * Lấy mã tiền tệ (Vd: "VND"), mặc định là "VND"
     */
    public String getCurrencyCode() {
        return pref.getString(KEY_CURRENCY_CODE, "VND");
    }

    /**
     * Lấy ký hiệu tiền tệ (Vd: "đ"), mặc định là "đ"
     */
    public String getCurrencySymbol() {
        return pref.getString(KEY_CURRENCY_SYMBOL, "đ");
    }
    /**
     * Lưu trạng thái bật/tắt thông báo
     */
    public void setNotifyEnabled(boolean enabled) {
        editor.putBoolean(KEY_NOTIFY_ENABLED, enabled);
        editor.apply();
    }

    /**
     * Lấy trạng thái thông báo, mặc định là false (tắt)
     */
    public boolean isNotifyEnabled() {
        return pref.getBoolean(KEY_NOTIFY_ENABLED, false);
    }
}
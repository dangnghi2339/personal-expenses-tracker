package com.example.login;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
    private static final String SP = "prefs_moneyme";

    // =================== Session (token) ===================
    private static final String K_TOKEN        = "auth_token";
    private static final String K_REFRESH      = "refresh_token";
    private static final String K_TOKEN_EXPIRE = "token_expire"; // millis epoch
    // =================== Notification setting ===================
    private static final String K_NOTIFY = "notify_enabled";

    /** Mặc định bật thông báo (true) */
    public static boolean isNotify(android.content.Context c) {
        return get(c).getBoolean(K_NOTIFY, true);
    }

    public static void setNotify(android.content.Context c, boolean enabled) {
        edit(c).putBoolean(K_NOTIFY, enabled).apply();
    }

    public static void setSession(Context c, String token, String refresh, long expireAtMillis) {
        edit(c).putString(K_TOKEN, token)
                .putString(K_REFRESH, refresh)
                .putLong(K_TOKEN_EXPIRE, expireAtMillis)
                .apply();
    }
    public static String getToken(Context c)   { return get(c).getString(K_TOKEN, ""); }
    public static String getRefresh(Context c) { return get(c).getString(K_REFRESH, ""); }
    public static long   getExpire(Context c)  { return get(c).getLong(K_TOKEN_EXPIRE, 0L); }
    public static void   clearSession(Context c) {
        edit(c).remove(K_TOKEN).remove(K_REFRESH).remove(K_TOKEN_EXPIRE).apply();
    }
    /** Phiên hợp lệ nếu có token và (nếu có hạn) chưa hết hạn */
    public static boolean hasValidSession(Context c) {
        String t = getToken(c);
        if (t == null || t.isEmpty()) return false;
        long exp = getExpire(c);
        return exp <= 0 || System.currentTimeMillis() < exp;
    }

    // =================== User info ===================
    private static final String K_NAME     = "user_name";
    private static final String K_EMAIL    = "user_email";
    private static final String K_USER_PASS= "user_pass"; // demo local login

    public static void setName(Context c, String v)  { edit(c).putString(K_NAME, v).apply(); }
    public static String getName(Context c)          { return get(c).getString(K_NAME, ""); }
    public static void setEmail(Context c, String v) { edit(c).putString(K_EMAIL, v).apply(); }
    public static String getEmail(Context c)         { return get(c).getString(K_EMAIL, ""); }
    public static void setUserPass(Context c, String v) { edit(c).putString(K_USER_PASS, v).apply(); }
    public static String getUserPass(Context c)         { return get(c).getString(K_USER_PASS, ""); }

    // =================== Language (en/vi) ===================
    private static final String K_LANG = "lang"; // "en" / "vi"
    public static void setLang(Context c, String v) { edit(c).putString(K_LANG, v).apply(); }
    public static String getLang(Context c)         { return get(c).getString(K_LANG, "vi"); }

    // =================== Currency (code & symbol) ===================
    private static final String K_CUR_CODE   = "currency_code";
    private static final String K_CUR_SYMBOL = "currency_symbol";

    /** Lưu mã tiền tệ (VD: USD/VND) và ký hiệu (VD: $/₫) */
    public static void setCurrency(Context c, String code, String symbol) {
        edit(c).putString(K_CUR_CODE, code)
                .putString(K_CUR_SYMBOL, symbol)
                .apply();
    }
    /** Lấy mã tiền tệ đã chọn, mặc định "USD" */
    public static String getCurrencyCode(Context c) {
        return get(c).getString(K_CUR_CODE, "USD");
    }
    /** Lấy ký hiệu tiền tệ đã chọn, mặc định "$" */
    public static String getCurrency(Context c) {
        return get(c).getString(K_CUR_SYMBOL, "$");
    }

    // =================== Internal helpers ===================
    private static SharedPreferences get(Context c) {
        return c.getSharedPreferences(SP, Context.MODE_PRIVATE);
    }
    private static SharedPreferences.Editor edit(Context c) {
        return get(c).edit();
    }
}

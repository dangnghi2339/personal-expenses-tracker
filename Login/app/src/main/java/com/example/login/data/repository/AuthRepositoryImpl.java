package com.example.login.data.repository;

import android.content.Context;
import com.example.login.data.datasource.Prefs;
// Import interface từ tầng domain
import com.example.login.domain.repository.AuthRepository;
// --- ĐÃ SỬA LỖI IMPORT TẠI ĐÂY ---
// Import enum AuthResult từ BÊN TRONG AuthRepository interface
import com.example.login.domain.repository.AuthRepository.AuthResult;
// --- KẾT THÚC SỬA LỖI ---


/**
 * Lớp triển khai AuthRepository interface.
 */
public class AuthRepositoryImpl implements AuthRepository {

    private Context appContext;

    public AuthRepositoryImpl(Context context) {
        this.appContext = context.getApplicationContext();
    }

    @Override
    public AuthResult login(String email, String password) {
        String regEmail = Prefs.getEmail(appContext);
        String regPass = Prefs.getUserPass(appContext);

        if (regEmail.isEmpty() || regPass.isEmpty()) {
            return AuthResult.FAILURE_NOT_REGISTERED;
        }
        if (!email.equalsIgnoreCase(regEmail)) {
            return AuthResult.FAILURE_WRONG_CREDENTIALS;
        }
        if (!password.equals(regPass)) {
            return AuthResult.FAILURE_WRONG_CREDENTIALS;
        }

        String access = "demo_access_" + System.currentTimeMillis();
        String refresh = "demo_refresh_" + System.currentTimeMillis();
        long expire = System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000;
        Prefs.setSession(appContext, access, refresh, expire);

        return AuthResult.SUCCESS;
    }

    @Override
    public void register(String username, String email, String password) {
        Prefs.setName(appContext, username);
        Prefs.setEmail(appContext, email);
        Prefs.setUserPass(appContext, password);

        String access = "demo_access_" + System.currentTimeMillis();
        String refresh = "demo_refresh_" + System.currentTimeMillis();
        long expireAt = System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000;
        Prefs.setSession(appContext, access, refresh, expireAt);
    }
}
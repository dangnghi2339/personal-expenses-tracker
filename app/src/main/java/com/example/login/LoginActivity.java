package com.example.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends BaseActivity {

    private TextInputEditText etEmail, etPassword;
    private View btnSignIn, btnFacebook, btnGoogle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // giữ layout của bạn

        // Tìm theo NHIỀU TÊN ID khả dĩ (không dùng R.id “dự phòng”)
        etEmail    = findByNames("etEmail", "email", "etEmailL", "inputEmail");
        etPassword = findByNames("etPassword", "password", "etPassL", "inputPassword");

        btnSignIn  = findByNames("btnSignIn", "signin", "buttonLogin", "btnLogin");
        btnFacebook = findByNames("btnFacebook", "facebook", "buttonFacebook");
        btnGoogle   = findByNames("btnGoogle", "google", "buttonGoogle");

        if (btnSignIn != null)   btnSignIn.setOnClickListener(v -> doLogin());
        if (btnFacebook != null) btnFacebook.setOnClickListener(v -> Toast.makeText(this, "Facebook SSO (stub)", Toast.LENGTH_SHORT).show());
        if (btnGoogle != null)   btnGoogle.setOnClickListener(v -> Toast.makeText(this, "Google SSO (stub)", Toast.LENGTH_SHORT).show());
    }

    private void doLogin() {
        if (etEmail == null || etPassword == null) {
            Toast.makeText(this, "Không tìm thấy trường Email/Password trong layout.", Toast.LENGTH_LONG).show();
            return;
        }

        String email = etEmail.getText() == null ? "" : etEmail.getText().toString().trim();
        String pass  = etPassword.getText() == null ? "" : etPassword.getText().toString();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email không hợp lệ");
            etEmail.requestFocus();
            return;
        }
        if (pass.isEmpty()) {
            etPassword.setError("Vui lòng nhập mật khẩu");
            etPassword.requestFocus();
            return;
        }

        String regEmail = Prefs.getEmail(this);
        String regPass  = Prefs.getUserPass(this);
        if (regEmail.isEmpty() || regPass.isEmpty()) {
            Toast.makeText(this, "Bạn chưa đăng ký tài khoản.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, RegisterActivity.class));
            return;
        }
        if (!email.equalsIgnoreCase(regEmail)) {
            etEmail.setError("Email không đúng"); etEmail.requestFocus(); return;
        }
        if (!pass.equals(regPass)) {
            etPassword.setError("Mật khẩu không đúng"); etPassword.requestFocus(); return;
        }

        // Set token demo
        String access = "demo_access_" + System.currentTimeMillis();
        String refresh = "demo_refresh_" + System.currentTimeMillis();
        long   expireAt = System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000;
        Prefs.setSession(this, access, refresh, expireAt);

        Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, IndividualActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}

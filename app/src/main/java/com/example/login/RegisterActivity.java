package com.example.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends BaseActivity {

    private TextInputEditText etUsername, etEmail, etPass, etConfirm;
    private View btnSignUp, tvAlready;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // GIỮ layout hiện tại của bạn

        // Tìm view theo NHIỀU TÊN ID có thể có trong layout (không dùng R.id “dự phòng”)
        etUsername = findByNames("etUsernameR", "etUsername", "username", "inputUsername");
        etEmail    = findByNames("etEmailR", "etEmail", "email", "inputEmail");
        etPass     = findByNames("etPassR", "etPassword", "password", "inputPassword");
        etConfirm  = findByNames("etConfirmR", "etConfirm", "confirmPassword", "inputConfirm");

        btnSignUp  = findByNames("btnSignUp", "btnRegister", "buttonRegister", "signUp");
        tvAlready  = findByNames("tvAlready", "alreadyHaveAccount", "linkLogin");

        if (btnSignUp != null) btnSignUp.setOnClickListener(v -> doRegister());
        if (tvAlready != null) tvAlready.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class)));
    }

    private void doRegister() {
        if (etEmail == null || etPass == null || etConfirm == null) {
            Toast.makeText(this, "Không tìm thấy trường dữ liệu trong layout.", Toast.LENGTH_LONG).show();
            return;
        }

        String username = etUsername != null && etUsername.getText()!=null ? etUsername.getText().toString().trim() : "";
        String email    = etEmail.getText() == null ? "" : etEmail.getText().toString().trim();
        String pass     = etPass.getText() == null ? "" : etPass.getText().toString();
        String conf     = etConfirm.getText() == null ? "" : etConfirm.getText().toString();

        if (username.isEmpty()) { if (etUsername!=null){ etUsername.setError("Username không được để trống"); etUsername.requestFocus(); } return; }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { etEmail.setError("Email không hợp lệ"); etEmail.requestFocus(); return; }
        if (pass.length() < 6) { etPass.setError("Mật khẩu phải ≥ 6 ký tự"); etPass.requestFocus(); return; }
        if (!pass.equals(conf)) { etConfirm.setError("Mật khẩu xác nhận không khớp"); etConfirm.requestFocus(); return; }

        // Lưu user cho màn cá nhân & local login
        Prefs.setName(this, username);
        Prefs.setEmail(this, email);
        Prefs.setUserPass(this, pass);

        // Auto-login: set token demo rồi vào Trang cá nhân
        String access = "demo_access_" + System.currentTimeMillis();
        String refresh = "demo_refresh_" + System.currentTimeMillis();
        long   expireAt = System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000;
        Prefs.setSession(this, access, refresh, expireAt);

        Toast.makeText(this, "Đăng ký & đăng nhập thành công!", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, IndividualActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}

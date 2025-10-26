package com.example.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Onboarding02Activity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_02); // GIỮ layout của bạn

        // Tìm nút Login theo NHIỀU tên id có thể có trong layout
        View loginBtn = findByNames("btnLogin", "login", "buttonLogin", "btn_sign_in", "action_login");
        if (loginBtn != null) {
            loginBtn.setOnClickListener(v ->
                    startActivity(new Intent(this, LoginActivity.class)));
        }

        // Tìm nút Register theo NHIỀU tên id có thể có trong layout
        View registerBtn = findByNames("btnRegister", "register", "buttonRegister", "btn_sign_up", "action_register");
        if (registerBtn != null) {
            registerBtn.setOnClickListener(v ->
                    startActivity(new Intent(this, RegisterActivity.class)));
        }
    }
}

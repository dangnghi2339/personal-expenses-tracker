package com.example.dack1.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout; // <-- THÊM IMPORT NÀY
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.dack1.ui.view.CostalActivity;
import com.example.dack1.ui.view.LoginActivity;
import com.example.dack1.R;
import com.example.dack1.util.SessionManager;

// Giả sử bạn sẽ sao chép các Activity này vào /ui/view/
// import com.example.dack1.ui.view.LanguageActivity;
// import com.example.dack1.ui.view.MoneyActivity;
// import com.example.dack1.ui.view.NotificationActivity;


public class ProfileFragment extends Fragment {

    // Đổi Button thành LinearLayout
    private LinearLayout btnLogout;
    private LinearLayout btnManageCategories;

    // Khai báo các View mới
    private TextView tvName;
    private TextView tvEmail;
    private LinearLayout rowMoney;
    private LinearLayout rowNotification;
    private LinearLayout rowLanguage;

    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Gắn layout "fragment_profile.xml" (bản mới)
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(getContext());

        // 1. Ánh xạ View (dùng ID cũ cho các nút cũ)
        btnLogout = view.findViewById(R.id.btn_logout);
        btnManageCategories = view.findViewById(R.id.btn_manage_categories);

        // Ánh xạ các View mới
        tvName = view.findViewById(R.id.et_name); // ID cho tên trong layout mới
        tvEmail = view.findViewById(R.id.et_email);     // ID cho email trong layout mới
        rowMoney = view.findViewById(R.id.rowMoney);
        rowNotification = view.findViewById(R.id.rowNotification);
        rowLanguage = view.findViewById(R.id.rowLanguage);

        // 2. Hiển thị thông tin User từ SessionManager
        String userEmail = sessionManager.getUserEmail();
        // Bạn cần thêm hàm getUserName() vào SessionManager nhé
        // String userName = sessionManager.getUserName();

        if (userEmail != null) {
            tvEmail.setText(userEmail); // Gán vào tvEmail
        }

        // if (userName != null && !userName.isEmpty()) {
        //     tvName.setText(userName); // Gán vào tvName
        // } else {
        tvName.setText("Người dùng"); // Tạm thời
        // }

        // 3. Gán sự kiện click (Code cũ vẫn chạy)
        btnLogout.setOnClickListener(v -> {
            sessionManager.logoutUser();
            Toast.makeText(getContext(), getString(R.string.logout_success), Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        btnManageCategories.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), CostalActivity.class));
        });

        // 4. Gán sự kiện cho các nút mới
        rowLanguage.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Sắp mở LanguageActivity...", Toast.LENGTH_SHORT).show();
            // Mở dòng dưới khi đã sao chép LanguageActivity.java
            // startActivity(new Intent(getActivity(), LanguageActivity.class));
        });

        rowNotification.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Sắp mở NotificationActivity...", Toast.LENGTH_SHORT).show();
            // Mở dòng dưới khi đã sao chép NotificationActivity.java
            // startActivity(new Intent(getActivity(), NotificationActivity.class));
        });

        rowMoney.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Sắp mở MoneyActivity...", Toast.LENGTH_SHORT).show();
            // Mở dòng dưới khi đã sao chép MoneyActivity.java
            // startActivity(new Intent(getActivity(), MoneyActivity.class));
        });
    }
}
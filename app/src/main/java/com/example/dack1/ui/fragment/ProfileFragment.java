package com.example.dack1.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.dack1.ui.view.CostalActivity;
import com.example.dack1.ui.view.LanguageActivity;
import com.example.dack1.ui.view.LoginActivity;
import com.example.dack1.R;
import com.example.dack1.ui.view.MoneyActivity;
import com.example.dack1.ui.view.NotificationActivity;
import com.example.dack1.util.SessionManager;

public class ProfileFragment extends Fragment {

    private LinearLayout btnLogout;
    private LinearLayout btnManageCategories;

    private TextView tvName;
    private TextView tvEmail;
    private LinearLayout rowMoney;
    private LinearLayout rowNotification;
    private LinearLayout rowLanguage;

    private TextView tvMoneyValue;
    private TextView tvNotifyValue;
    private TextView tvLangValue;

    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(getContext());

        btnLogout = view.findViewById(R.id.btn_logout);
        btnManageCategories = view.findViewById(R.id.btn_manage_categories);

        tvName = view.findViewById(R.id.et_name);
        tvEmail = view.findViewById(R.id.et_email);
        rowMoney = view.findViewById(R.id.rowMoney);
        rowNotification = view.findViewById(R.id.rowNotification);
        rowLanguage = view.findViewById(R.id.rowLanguage);

        tvMoneyValue = view.findViewById(R.id.tvMoneyValue);
        tvNotifyValue = view.findViewById(R.id.tvNotifyValue);
        tvLangValue = view.findViewById(R.id.tvLangValue);

        loadProfileData();

        String userEmail = sessionManager.getUserEmail();
        if (userEmail != null) {
            tvEmail.setText(userEmail);
        }

        tvName.setText("Người dùng");

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

        rowLanguage.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), LanguageActivity.class));
        });

        rowNotification.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), NotificationActivity.class));
        });

        rowMoney.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), MoneyActivity.class));
        });
    }

    private void loadProfileData() {
        if (sessionManager == null || !isAdded()) {
            return;
        }

        String userEmail = sessionManager.getUserEmail();
        String userName = sessionManager.getUserName();

        if (userEmail != null) {
            tvEmail.setText(userEmail);
        }

        if (userName != null && !userName.isEmpty()) {
            tvName.setText(userName);
        } else {
            tvName.setText("Người dùng");
        }

        String currencySymbol = sessionManager.getCurrencySymbol();
        tvMoneyValue.setText(currencySymbol); // Hiển thị ký hiệu ($, ₫, €...)

        boolean isNotifyEnabled = sessionManager.isNotifyEnabled();
        tvNotifyValue.setText(isNotifyEnabled ? getString(R.string.turn_on) : getString(R.string.turn_off));

        String langCode = sessionManager.getLanguage();
        if ("en".equals(langCode)) {
            tvLangValue.setText(getString(R.string.english)); // Sẽ hiển thị "English"
        } else {
            tvLangValue.setText(getString(R.string.vietnamese)); // Sẽ hiển thị "Tiếng Việt"
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProfileData();
    }
}
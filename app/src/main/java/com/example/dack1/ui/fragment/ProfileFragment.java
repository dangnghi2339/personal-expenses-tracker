package com.example.dack1.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.dack1.ui.view.CostalActivity;
import com.example.dack1.ui.view.LoginActivity;
import com.example.dack1.R;
import com.example.dack1.util.SessionManager;

public class ProfileFragment extends Fragment {

    private Button btnLogout;
    private Button btnManageCategories;
    private TextView tvUserEmail;
    private SessionManager sessionManager;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Gắn layout "fragment_profile.xml"
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Ánh xạ View
        btnLogout = view.findViewById(R.id.btn_logout);
        btnManageCategories = view.findViewById(R.id.btn_manage_categories); // Ánh xạ nút mới
        tvUserEmail = view.findViewById(R.id.tv_user_email);
        sessionManager = new SessionManager(getContext());
        
        // Display user email
        String userEmail = sessionManager.getUserEmail();
        if (userEmail != null) {
            tvUserEmail.setText(userEmail);
        }
        
        // 2. Gán sự kiện click
        btnLogout.setOnClickListener(v -> {
            // Clear session and logout
            sessionManager.logoutUser();
            Toast.makeText(getContext(), getString(R.string.logout_success), Toast.LENGTH_SHORT).show();
            
            // Navigate to login and finish MainActivity
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        });
        btnManageCategories.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), CostalActivity.class));
        });
    }
}
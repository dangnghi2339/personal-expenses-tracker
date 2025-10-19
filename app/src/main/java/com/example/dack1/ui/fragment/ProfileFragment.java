package com.example.dack1.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.dack1.ui.view.CostalActivity;
import android.content.Intent;
import com.example.dack1.R;

public class ProfileFragment extends Fragment {

    private Button btnLogout;
    private Button btnManageCategories;
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
        // 2. Gán sự kiện click
        btnLogout.setOnClickListener(v -> {
            // Tạm thời hiển thị Toast
            // Logic Đăng xuất thật sẽ ở Giai đoạn 3
            Toast.makeText(getContext(), "Chuẩn bị Đăng xuất...", Toast.LENGTH_SHORT).show();
        });
        btnManageCategories.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), CostalActivity.class));
        });
    }
}
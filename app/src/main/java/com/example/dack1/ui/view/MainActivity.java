package com.example.dack1.ui.view;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.example.dack1.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.dack1.ui.view.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1. Gắn layout KHUNG SƯỜN vào Activity
        setContentView(R.layout.activity_main);

        // 2. Tìm thanh điều hướng 4 tab
        BottomNavigationView navView = findViewById(R.id.bottom_nav);

        // 3. Tìm "khung chứa" Fragment (NavHostFragment)
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        // 4. Lấy "bộ điều khiển" (NavController) từ khung chứa đó
        NavController navController = navHostFragment.getNavController();

        // 5. Dùng thư viện NavigationUI để TỰ ĐỘNG kết nối 4 tab với bộ điều khiển
        // Dòng này sẽ tự xử lý việc bấm tab nào thì hiện Fragment đó.
        NavigationUI.setupWithNavController(navView, navController);
    }
}
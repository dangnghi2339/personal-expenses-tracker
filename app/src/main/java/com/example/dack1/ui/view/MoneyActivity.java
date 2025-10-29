package com.example.dack1.ui.view; // Đã sửa package

import android.os.Bundle;
import android.widget.ListView;

import com.example.dack1.ui.adapter.CurrencyAdapter;
import com.example.dack1.R; // Đã sửa R file
import com.example.dack1.util.SessionManager; // Thêm import SessionManager

import java.util.ArrayList;
import java.util.List;

public class MoneyActivity extends BaseActivity {

    private SessionManager sessionManager; // <-- THÊM DÒNG NÀY

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Lưu ý: File này chưa dùng ViewBinding, nhưng vẫn ổn
        setContentView(R.layout.activity_money);

        sessionManager = new SessionManager(this); // <-- THÊM DÒNG NÀY

        ListView list = findViewById(R.id.listCurrency);

        // Danh sách tiền tệ phổ biến
        List<CurrencyAdapter.Item> items = new ArrayList<>();
        items.add(new CurrencyAdapter.Item("USD", "$",  "USD — US Dollar"));
        items.add(new CurrencyAdapter.Item("EUR", "€",  "EUR — Euro"));
        items.add(new CurrencyAdapter.Item("VND", "₫",  "VND — Viet Nam Dong")); // Có thể để "đ" thay vì "₫"
        items.add(new CurrencyAdapter.Item("JPY", "¥",  "JPY — Japanese Yen"));
        items.add(new CurrencyAdapter.Item("GBP", "£",  "GBP — British Pound"));
        items.add(new CurrencyAdapter.Item("KRW", "₩",  "KRW — South Korean Won"));
        items.add(new CurrencyAdapter.Item("CNY", "¥",  "CNY — Chinese Yuan"));
        items.add(new CurrencyAdapter.Item("AUD", "A$", "AUD — Australian Dollar"));
        items.add(new CurrencyAdapter.Item("CAD", "C$", "CAD — Canadian Dollar"));
        items.add(new CurrencyAdapter.Item("SGD", "S$", "SGD — Singapore Dollar"));
        // thêm nếu bạn muốn…

        // ĐÂY LÀ DÒNG ĐÃ SỬA:
        String currentCode = sessionManager.getCurrencyCode();

        CurrencyAdapter adapter = new CurrencyAdapter(this, items, currentCode);
        list.setAdapter(adapter);

        // Khi người dùng chọn xong, đóng màn hình
        // Lưu ý: Việc lưu tiền tệ đã được thực hiện bên trong CurrencyAdapter
        list.setOnItemClickListener((parent, view, position, id) -> {
            finish(); // Đóng Activity sau khi chọn
        });
    }
}
package com.example.login;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MoneyActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money);

        ListView list = findViewById(R.id.listCurrency);

        // Danh sách tiền tệ phổ biến
        List<CurrencyAdapter.Item> items = new ArrayList<>();
        items.add(new CurrencyAdapter.Item("USD", "$",  "USD — US Dollar"));
        items.add(new CurrencyAdapter.Item("EUR", "€",  "EUR — Euro"));
        items.add(new CurrencyAdapter.Item("VND", "₫",  "VND — Viet Nam Dong"));
        items.add(new CurrencyAdapter.Item("JPY", "¥",  "JPY — Japanese Yen"));
        items.add(new CurrencyAdapter.Item("GBP", "£",  "GBP — British Pound"));
        items.add(new CurrencyAdapter.Item("KRW", "₩",  "KRW — South Korean Won"));
        items.add(new CurrencyAdapter.Item("CNY", "¥",  "CNY — Chinese Yuan"));
        items.add(new CurrencyAdapter.Item("AUD", "A$", "AUD — Australian Dollar"));
        items.add(new CurrencyAdapter.Item("CAD", "C$", "CAD — Canadian Dollar"));
        items.add(new CurrencyAdapter.Item("SGD", "S$", "SGD — Singapore Dollar"));
        // thêm nếu bạn muốn…

        String current = Prefs.getCurrencyCode(this); // code đang lưu (mặc định USD)
        CurrencyAdapter adapter = new CurrencyAdapter(this, items, current);
        list.setAdapter(adapter);

        // Khi người dùng chọn xong, bạn có thể finish() để quay lại trang cá nhân.
        list.setOnItemClickListener((parent, view, position, id) -> {
            // Adapter đã lưu Prefs.setCurrency ở onClick; ta chỉ cần đóng màn.
            finish();
        });
    }
}

package com.example.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class IndividualActivity extends BaseActivity {

    private TextView tvName, tvEmail, tvMoneyValue, tvNotifyValue, tvLangValue;
    private View rowMoney, rowNotification, rowLanguage, rowLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual);

        // ===== Đẩy nội dung xuống dưới camera/status bar nếu có rootIndividual =====
        final View root = findViewByNames("rootIndividual"); // id của root trong XML (nếu bạn đã thêm)
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(
                        v.getPaddingLeft(),
                        sys.top,        // tránh đè camera/status bar
                        v.getPaddingRight(),
                        sys.bottom      // chừa chỗ cho nav bar (nếu có)
                );
                return insets;
            });
            ViewCompat.requestApplyInsets(root);
        }

        // ===== Lấy view theo nhiều tên ID (không cần sửa XML) =====
        tvName        = findByNames("tvName", "userName", "txtName");
        tvEmail       = findByNames("tvEmail", "userEmail", "txtEmail");
        tvMoneyValue  = findByNames("tvMoneyValue", "tvCurrency", "moneyValue");
        tvNotifyValue = findByNames("tvNotifyValue", "tvNotifyState", "notifyValue");
        tvLangValue   = findByNames("tvLangValue", "tvLanguageValue", "langValue");

        rowMoney        = findByNames("rowMoney", "menuMoney", "itemMoney");
        rowNotification = findByNames("rowNotification", "menuNotification", "itemNotification");
        rowLanguage     = findByNames("rowLanguage", "menuLanguage", "itemLanguage");
        rowLogout       = findByNames("rowLogout", "menuLogout", "itemLogout");

        // ===== Click các hàng =====
        if (rowMoney != null)        rowMoney.setOnClickListener(v -> startActivity(new Intent(this, MoneyActivity.class)));
        if (rowNotification != null) rowNotification.setOnClickListener(v -> startActivity(new Intent(this, NotificationActivity.class)));
        if (rowLanguage != null)     rowLanguage.setOnClickListener(v -> startActivity(new Intent(this, LanguageActivity.class)));
        if (rowLogout != null)       rowLogout.setOnClickListener(v -> logout());

        // Gán dữ liệu lần đầu
        bindUser();
        bindSettingsPreview();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật lại khi từ màn khác quay về (money/language/notification)
        bindUser();
        bindSettingsPreview();
    }

    /** Gán tên & email từ Prefs (hoặc giá trị mặc định nếu trống) */
    private void bindUser() {
        String name  = Prefs.getName(this);
        String email = Prefs.getEmail(this);
        if (tvName  != null)  tvName.setText((name == null || name.trim().isEmpty()) ? "Guest" : name);
        if (tvEmail != null)  tvEmail.setText(email == null ? "" : email);
    }

    /** Hiển thị giá trị xem trước cho Money/Notification/Language */
    private void bindSettingsPreview() {
        if (tvMoneyValue != null) {
            // Chọn 1 trong 2 cách hiển thị:
            tvMoneyValue.setText(Prefs.getCurrencyCode(this)); // USD / VND / EUR ...
            // tvMoneyValue.setText(Prefs.getCurrency(this));   // $ / ₫ / € ...
        }
        if (tvNotifyValue != null) {
            boolean on = Prefs.isNotify(this);
            // dùng resource để tự dịch theo ngôn ngữ
            tvNotifyValue.setText(getString(on ? R.string.turn_on : R.string.turn_off));
        }
        if (tvLangValue != null) {
            boolean vi = "vi".equals(Prefs.getLang(this));
            tvLangValue.setText(getString(vi ? R.string.vietnamese : R.string.english));
        }
    }

    private void logout() {
        Prefs.clearSession(this);
        Intent i = new Intent(this, Onboarding02Activity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    // ===== Helper: tìm view theo TÊN id (dựa trên BaseActivity.findByNames) =====
    @SuppressWarnings("unchecked")
    private <T extends View> T findViewByNames(String... names) {
        return findByNames(names);
    }
}

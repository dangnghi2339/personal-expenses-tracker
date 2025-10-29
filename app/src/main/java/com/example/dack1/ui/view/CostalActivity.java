package com.example.dack1.ui.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dack1.R;
import com.example.dack1.data.model.Category;
import com.example.dack1.ui.adapter.CategoryAdapter;
import com.example.dack1.ui.viewmodel.CategoryViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.dack1.ui.view.BaseActivity;

public class CostalActivity extends BaseActivity implements CategoryAdapter.OnCategoryActionListener {

    private CategoryViewModel categoryViewModel;
    private CategoryAdapter categoryAdapter;
    private RecyclerView rvCategories;
    private FloatingActionButton fabAddCategory;
    private TextView tvEmptyCategories;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_costal_editing); // Layout chứa RecyclerView và FAB

        rvCategories = findViewById(R.id.rv_categories);
        fabAddCategory = findViewById(R.id.fab_add_category);
        tvEmptyCategories = findViewById(R.id.tv_empty_categories);

        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        setupRecyclerView();
        observeCategories();

        fabAddCategory.setOnClickListener(v -> showAddEditCategoryDialog(null));
    }

    private void setupRecyclerView() {
        categoryAdapter = new CategoryAdapter(this);
        rvCategories.setLayoutManager(new LinearLayoutManager(this));
        rvCategories.setAdapter(categoryAdapter);
    }

    private void observeCategories() {
        categoryViewModel.getAllCategories().observe(this, categories -> {
            categoryAdapter.submitList(categories);
            
            // Show/hide empty state
            if (categories == null || categories.isEmpty()) {
                rvCategories.setVisibility(View.GONE);
                tvEmptyCategories.setVisibility(View.VISIBLE);
            } else {
                rvCategories.setVisibility(View.VISIBLE);
                tvEmptyCategories.setVisibility(View.GONE);
            }
        });
    }

    // --- Xử lý sự kiện từ Adapter ---

    @Override
    public void onEditClick(Category category) {
        showAddEditCategoryDialog(category);
    }

    @Override
    public void onDeleteClick(Category category) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirm_delete))
                .setMessage(getString(R.string.confirm_delete_category, category.getName()))
                .setPositiveButton(getString(R.string.delete), (dialog, which) -> {
                    // Check if any transactions use this category
                    categoryViewModel.getTransactionCountByCategoryId(category.getId())
                            .observe(this, count -> {
                                if (count != null && count > 0) {
                                    Toast.makeText(this, getString(R.string.cannot_delete_category_in_use), Toast.LENGTH_LONG).show();
                                } else {
                                    categoryViewModel.delete(category);
                                    Toast.makeText(this, getString(R.string.category_deleted), Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

    // --- Dialog Thêm/Sửa Category ---

    private String selectedIconName = null;
    private String selectedColorHex = null;
    private ImageButton previouslySelectedIcon = null;
    private Button previouslySelectedColorButton = null;

    private void showAddEditCategoryDialog(@Nullable Category categoryToEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_category, null); // Layout dialog "xịn"
        builder.setView(dialogView);

        // --- Ánh xạ View ---
        // ID này phải tồn tại trong add_category.xml
        final EditText etCategoryName = dialogView.findViewById(R.id.et_dialog_category_name);
        final Button btnSave = dialogView.findViewById(R.id.btnsave);
        final ImageButton btnBack = dialogView.findViewById(R.id.imageButton12);
        final RadioGroup rgCategoryType = dialogView.findViewById(R.id.rg_category_type);

        // --- Reset state ---
        selectedIconName = null;
        selectedColorHex = null;
        previouslySelectedIcon = null;
        previouslySelectedColorButton = null;

        // --- Thiết lập chọn Icon và Màu ---
        setupIconSelection(dialogView);
        setupColorSelection(dialogView);

        // --- Xử lý chế độ Sửa ---
        if (categoryToEdit != null) {
            builder.setTitle("Sửa danh mục");
            etCategoryName.setText(categoryToEdit.getName());
            selectedIconName = categoryToEdit.getIconName(); // SỬA: Dùng getIconName()
            selectedColorHex = categoryToEdit.getColor();    // Dùng getColor()
            btnSave.setText("Cập nhật");
            
            // Set category type in RadioGroup
            if ("income".equalsIgnoreCase(categoryToEdit.getType())) {
                rgCategoryType.check(R.id.rb_income);
            } else {
                rgCategoryType.check(R.id.rb_expense);
            }
            
            highlightSavedSelections(dialogView, selectedIconName, selectedColorHex); // Highlight lựa chọn cũ
        } else {
            builder.setTitle("Thêm danh mục mới");
            btnSave.setText("Save catalog");
            // Default to expense
            rgCategoryType.check(R.id.rb_expense);
        }

        AlertDialog dialog = builder.create();

        // --- Xử lý nút Lưu/Cập nhật ---
        btnSave.setOnClickListener(v -> {
            String categoryName = etCategoryName.getText().toString().trim();

                        if (TextUtils.isEmpty(categoryName)) {
                            Toast.makeText(this, getString(R.string.category_name_required), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (selectedIconName == null) {
                            Toast.makeText(this, getString(R.string.select_icon), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (selectedColorHex == null) {
                            Toast.makeText(this, getString(R.string.select_color), Toast.LENGTH_SHORT).show();
                            return;
                        }

            // Get selected category type from RadioGroup
            String categoryType = getSelectedCategoryType(rgCategoryType);

            if (categoryToEdit != null) { // Chế độ Sửa
                // Pre-check duplicate name (excluding current id)
                categoryViewModel.findByName(categoryName).observe(this, existing -> {
                    if (existing != null && existing.getId() != categoryToEdit.getId()) {
                        Toast.makeText(this, getString(R.string.category_name_exists), Toast.LENGTH_SHORT).show();
                    } else {
                        categoryToEdit.setName(categoryName);
                        categoryToEdit.setType(categoryType);
                        categoryToEdit.setIconName(selectedIconName);
                        categoryToEdit.setColor(selectedColorHex);
                        categoryViewModel.update(categoryToEdit);
                        Toast.makeText(this, getString(R.string.category_updated), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            } else { // Chế độ Thêm mới
                categoryViewModel.findByName(categoryName).observe(this, existing -> {
                    if (existing != null) {
                        Toast.makeText(this, getString(R.string.category_name_exists), Toast.LENGTH_SHORT).show();
                    } else {
                        Category newCategory = new Category(categoryName, categoryType, selectedIconName, selectedColorHex);
                        categoryViewModel.insert(newCategory);
                        Toast.makeText(this, getString(R.string.category_added), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            }
        });

        // --- Xử lý nút Back ---
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> dialog.dismiss());
        }

        dialog.show();
    }

    private String getSelectedCategoryType(RadioGroup rgCategoryType) {
        int checkedId = rgCategoryType.getCheckedRadioButtonId();
        if (checkedId == R.id.rb_income) {
            return "income";
        } else {
            return "expense";
        }
    }


    // --- Thiết lập chọn Icon ---
    private void setupIconSelection(View dialogView) {
        // ID này phải tồn tại trong add_category.xml
        LinearLayout iconContainer = dialogView.findViewById(R.id.icon_rows_container);

        if (iconContainer == null) {
            Log.e("CostalActivity", "Layout add_category.xml thiếu LinearLayout ID 'icon_rows_container'.");
            return;
        }

        for (int i = 0; i < iconContainer.getChildCount(); i++) {
            View child = iconContainer.getChildAt(i);
            if (child instanceof LinearLayout) {
                LinearLayout rowLayout = (LinearLayout) child;
                for (int j = 0; j < rowLayout.getChildCount(); j++) {
                    View item = rowLayout.getChildAt(j);
                    if (item instanceof ImageButton) {
                        ImageButton iconButton = (ImageButton) item;
                        // Lấy tên icon từ Tag (cần đặt android:tag="ic_xxx" trong XML)
                        final String iconName = (iconButton.getTag() != null) ? iconButton.getTag().toString() : null;

                        if (iconName != null) {
                            iconButton.setOnClickListener(v -> {
                                selectedIconName = iconName;
                                highlightIcon((ImageButton) v);
                                Log.d("CostalActivity", "Selected Icon Tag: " + selectedIconName);
                            });
                        } else {
                            Log.w("CostalActivity", "ImageButton này thiếu android:tag để lấy tên icon.");
                        }
                    }
                }
            }
        }
    }

    // --- Thiết lập chọn Màu ---
    private void setupColorSelection(View dialogView) {
        int[] buttonIds = {
                R.id.button, R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5,
                R.id.button6, R.id.button7, R.id.button8, R.id.button9, R.id.button10, R.id.button11,
                R.id.button12, R.id.button13, R.id.button14, R.id.button15, R.id.button16, R.id.button17
        };

        for (int id : buttonIds) {
            Button colorButton = dialogView.findViewById(id);
            if (colorButton != null) {
                colorButton.setOnClickListener(v -> {
                    ColorStateList colorStateList = v.getBackgroundTintList();
                    if (colorStateList != null) {
                        int colorInt = colorStateList.getDefaultColor();
                        selectedColorHex = String.format("#%06X", (0xFFFFFF & colorInt));
                        highlightColorButton((Button) v);
                        Log.d("CostalActivity", "Selected Color Hex: " + selectedColorHex);
                    }
                });
            }
        }
    }

    // --- Hàm Highlight Icon ---
    private void highlightIcon(ImageButton selectedButton) {
        if (previouslySelectedIcon != null) {
            // Reset background drawable của icon cũ
            previouslySelectedIcon.setBackgroundResource(R.drawable.rounded_imagebutton);
        }
        // Highlight icon mới bằng background color (hoặc drawable khác)
        selectedButton.setBackgroundColor(Color.LTGRAY); // Màu xám nhạt
        // Hoặc: selectedButton.setBackgroundResource(R.drawable.rounded_imagebutton_selected);
        previouslySelectedIcon = selectedButton;
    }

    // --- Hàm Highlight Màu ---
    private void highlightColorButton(Button selectedButton) {
        if (previouslySelectedColorButton != null) {
            // Reset hiệu ứng highlight cho nút màu cũ (đặt lại alpha)
            previouslySelectedColorButton.setAlpha(1.0f);
            // setButtonBorder(previouslySelectedColorButton, Color.TRANSPARENT, 0); // Nếu dùng viền
        }
        // Highlight nút màu mới (giảm alpha)
        selectedButton.setAlpha(0.7f);
        // setButtonBorder(selectedButton, Color.BLACK, 4); // Nếu dùng viền
        previouslySelectedColorButton = selectedButton;
    }

    /* // Ví dụ hàm thêm/xóa viền (cần tạo drawable shape_button_border.xml)
    private void setButtonBorder(Button button, int color, int width) {
        GradientDrawable drawable = (GradientDrawable) ContextCompat.getDrawable(this, R.drawable.shape_button_border);
        if (drawable != null) {
             GradientDrawable mutableDrawable = (GradientDrawable) drawable.mutate(); // Tạo bản sao để sửa đổi
             mutableDrawable.setStroke(width, color);
             button.setBackground(mutableDrawable);
             // Quan trọng: Set lại backgroundTint sau khi setBackground
             ColorStateList tint = button.getBackgroundTintList();
             if (tint != null) {
                 DrawableCompat.setTintList(button.getBackground(), tint);
             }
        }
    }*/


    // --- Hàm Highlight khi Sửa ---
    private void highlightSavedSelections(View dialogView, String savedIconName, String savedColorHex) {
        // Highlight Icon
        if (savedIconName != null) {
            LinearLayout iconContainer = dialogView.findViewById(R.id.icon_rows_container);
            if (iconContainer != null) {
                boolean iconFound = false;
                for (int i = 0; i < iconContainer.getChildCount() && !iconFound; i++) {
                    View child = iconContainer.getChildAt(i);
                    if (child instanceof LinearLayout) {
                        LinearLayout rowLayout = (LinearLayout) child;
                        for (int j = 0; j < rowLayout.getChildCount() && !iconFound; j++) {
                            View item = rowLayout.getChildAt(j);
                            if (item instanceof ImageButton) {
                                ImageButton iconButton = (ImageButton) item;
                                String iconName = (iconButton.getTag() != null) ? iconButton.getTag().toString() : null;
                                if (iconName != null && iconName.equals(savedIconName)) {
                                    highlightIcon(iconButton);
                                    iconFound = true;
                                }
                            }
                        }
                    }
                }
                if (!iconFound) Log.w("CostalActivity", "Không tìm thấy ImageButton với tag để highlight: " + savedIconName);
            }
        }

        // Highlight Color
        if (savedColorHex != null) {
            try {
                int targetColor = Color.parseColor(savedColorHex);
                int[] buttonIds = {
                        R.id.button, R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5,
                        R.id.button6, R.id.button7, R.id.button8, R.id.button9, R.id.button10, R.id.button11,
                        R.id.button12, R.id.button13, R.id.button14, R.id.button15, R.id.button16, R.id.button17
                };
                boolean colorFound = false;
                for (int id : buttonIds) {
                    Button colorButton = dialogView.findViewById(id);
                    if (colorButton != null) {
                        ColorStateList colorStateList = colorButton.getBackgroundTintList();
                        // So sánh màu (chú ý bỏ qua thành phần alpha nếu mã hex chỉ có 6 chữ số)
                        if (colorStateList != null && (0xFFFFFF & colorStateList.getDefaultColor()) == (0xFFFFFF & targetColor)) {
                            highlightColorButton(colorButton);
                            colorFound = true;
                            break;
                        }
                    }
                }
                if (!colorFound) Log.w("CostalActivity", "Không tìm thấy Button với màu để highlight: " + savedColorHex);
            } catch (IllegalArgumentException e) {
                Log.e("CostalActivity", "Mã màu lưu trong DB không hợp lệ: " + savedColorHex, e);
            }
        }
    }
}
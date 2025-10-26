package com.example.mainscreen3;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;

public class AddCategoryFragment extends DialogFragment {

    public static final String TAG = "AddCategoryFragment";

    private EditText etCategoryName;
    private GridLayout iconGrid, colorGrid;
    private Button btnSave;
    private ImageView ivIconPreview;

    // Biến để theo dõi lựa chọn
    private int selectedIconResId = 0;
    private int selectedColorResId = 0;
    private View currentlySelectedIconView = null;
    private View currentlySelectedColorView = null;
    private TextView tvTitleAdd, tvTitleFix, btnDeleteCategory;
    private CategoryModel categoryToEdit = null;
    private OnCategoryAddedListener listener;

    private final int[] MOCK_ICON_IDS = new int[]{
            R.drawable.ic_credit_card, R.drawable.ic_eat_drink, R.drawable.ic_shopping,
            R.drawable.ic_gasoline, R.drawable.ic_electricity, R.drawable.ic_house,
            R.drawable.ic_load_phone, R.drawable.ic_school, R.drawable.ic_market,
            R.drawable.ic_pet,
            R.drawable.ic_travel,
            R.drawable.ic_health,
            R.drawable.ic_sport,
            R.drawable.ic_beauty,
            R.drawable.ic_movie,
            R.drawable.ic_transport
    };

    private final int[] MOCK_COLOR_IDS = new int[]{
            R.color.color_1, R.color.color_2, R.color.color_3, R.color.color_4,
            R.color.color_5, R.color.color_6, R.color.color_7, R.color.colorPrimary,
            R.color.color_error, R.color.color_secondary,

            R.color.color_red, R.color.color_pink, R.color.color_purple,
            R.color.color_deep_purple, R.color.color_indigo, R.color.color_light_blue,
            R.color.color_cyan, R.color.color_teal, R.color.color_green,
            R.color.color_light_green, R.color.color_lime, R.color.color_yellow,
            R.color.color_amber, R.color.color_orange, R.color.color_deep_orange,
            R.color.color_brown, R.color.color_grey, R.color.color_blue_grey
    };

    public AddCategoryFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTheme);
        if (getArguments() != null && getArguments().containsKey("EDIT_CATEGORY_MODEL")) {
            categoryToEdit = (CategoryModel) getArguments().getSerializable("EDIT_CATEGORY_MODEL");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton btnBack = view.findViewById(R.id.btn_add_category_back);

        etCategoryName = view.findViewById(R.id.et_category_name);
        iconGrid = view.findViewById(R.id.icon_grid);
        colorGrid = view.findViewById(R.id.color_grid);
        btnSave = view.findViewById(R.id.btn_save_catalog);
        ivIconPreview = view.findViewById(R.id.iv_icon_preview);

        tvTitleAdd = view.findViewById(R.id.tv_title_add);
        tvTitleFix = view.findViewById(R.id.tv_title_fix);
        btnDeleteCategory = view.findViewById(R.id.btn_delete_category);

        if (categoryToEdit != null) {
            // --- CHẾ ĐỘ SỬA ---
            tvTitleAdd.setVisibility(View.GONE);
            tvTitleFix.setVisibility(View.VISIBLE);
            btnDeleteCategory.setVisibility(View.VISIBLE);
            btnSave.setText("Save"); // Hoặc "Update"

            // Điền thông tin cũ
            etCategoryName.setText(categoryToEdit.getName());
            selectedIconResId = categoryToEdit.getIconResource();
            selectedColorResId = categoryToEdit.getColorResource();

        } else {
            // --- CHẾ ĐỘ THÊM MỚI ---
            tvTitleAdd.setVisibility(View.VISIBLE);
            tvTitleFix.setVisibility(View.GONE);
            btnDeleteCategory.setVisibility(View.GONE);
            btnSave.setText("Save the catalog");

            if (MOCK_ICON_IDS.length > 0) {
                selectedIconResId = MOCK_ICON_IDS[0];
                ivIconPreview.setImageResource(selectedIconResId);
            }
            if (MOCK_COLOR_IDS.length > 0) {
                selectedColorResId = MOCK_COLOR_IDS[0];
                updateIconPreviewColor();
            }
        }

        updateIconPreviewImage();
        updateIconPreviewColor();

        loadIconGrid();
        loadColorGrid();

        btnBack.setOnClickListener(v -> dismiss());
        btnSave.setOnClickListener(v -> saveCategory());
        btnDeleteCategory.setOnClickListener(v -> deleteCategory());

    }

    private void loadIconGrid() {
        if (getContext() == null) return;

        for (int iconResId : MOCK_ICON_IDS) {
            ImageButton iconButton = new ImageButton(getContext());

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = (int) (getResources().getDisplayMetrics().density * 48);
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(10, 10, 10, 10);
            iconButton.setLayoutParams(params);

            iconButton.setImageResource(iconResId);
            iconButton.setBackgroundResource(R.drawable.icon_button_bg);
            iconButton.setTag(iconResId);

            iconButton.setOnClickListener(v -> handleIconSelection(v, iconResId));

            iconGrid.addView(iconButton);
        }
    }

    private void loadColorGrid() {
        if (getContext() == null) return;

        for (int colorResId : MOCK_COLOR_IDS) {
            View colorCircle = new View(getContext());

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            int size = (int) (getResources().getDisplayMetrics().density * 36);
            params.width = size;
            params.height = size;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setGravity(Gravity.CENTER);
            params.setMargins(5, 5, 5, 5);
            colorCircle.setLayoutParams(params);

            colorCircle.setBackgroundResource(R.drawable.color_circle_bg);
            int actualColor = ContextCompat.getColor(getContext(), colorResId);
            colorCircle.getBackground().setColorFilter(actualColor, android.graphics.PorterDuff.Mode.SRC_ATOP);
            colorCircle.setTag(colorResId);

            colorCircle.setOnClickListener(v -> handleColorSelection(v, colorResId));

            colorGrid.addView(colorCircle);
        }
    }

    private void handleIconSelection(View newSelectionView, int iconResId) {
        if (currentlySelectedIconView != null) {
            currentlySelectedIconView.setSelected(false);
        }

        newSelectionView.setSelected(true);
        currentlySelectedIconView = newSelectionView;
        selectedIconResId = iconResId;

        updateIconPreviewImage();
    }

    private void handleColorSelection(View newSelectionView, int colorResId) {
        if (currentlySelectedColorView != null) {
            currentlySelectedColorView.setSelected(false);
        }

        newSelectionView.setSelected(true);
        currentlySelectedColorView = newSelectionView;
        selectedColorResId = colorResId;

        updateIconPreviewColor();
    }

    private void deleteCategory() {
        if (listener != null && categoryToEdit != null) {
            listener.onCategoryDeleted(categoryToEdit);
        }
        dismiss();
    }

    private void saveCategory() {
        String name = etCategoryName.getText().toString().trim();

        if (name.isEmpty()) {
            etCategoryName.setError("Category name required");
            return;
        }

        if (selectedIconResId == 0) {
            Toast.makeText(getContext(), "Please select an icon", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedColorResId == 0) {
            Toast.makeText(getContext(), "Please select a color", Toast.LENGTH_SHORT).show();
            return;
        }

        String newCategoryType = categoryToEdit != null ?
                categoryToEdit.getType() :
                (getArguments() != null ? getArguments().getString("CATEGORY_TYPE", ExpenseItem.TYPE_EXPENSE) : ExpenseItem.TYPE_EXPENSE);

        CategoryModel savedCategory = new CategoryModel(name, selectedIconResId, newCategoryType, selectedColorResId);

        if (listener != null) {
            listener.onCategorySaved(savedCategory, categoryToEdit);
        }

        dismiss();
    }

    public void setOnCategoryAddedListener(OnCategoryAddedListener listener) {
        this.listener = listener;
    }

    private void updateIconPreviewColor() {
        if (selectedColorResId != 0 && ivIconPreview != null && getContext() != null) {
            int color = ContextCompat.getColor(getContext(), selectedColorResId);
            ivIconPreview.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        }
    }

    private void updateIconPreviewImage() {
        if (selectedIconResId != 0 && ivIconPreview != null) {
            ivIconPreview.setImageResource(selectedIconResId);
        }
    }
}
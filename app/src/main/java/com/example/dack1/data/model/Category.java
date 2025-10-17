package com.example.dack1.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Entity đại diện cho bảng 'categories'.
 * Mỗi danh mục sẽ có một tên không được trùng lặp.
 * Chúng ta thêm một 'index' để tối ưu việc truy vấn và đảm bảo tính duy nhất (unique).
 */
@Entity(tableName = "categories",
        indices = {@Index(value = {"name"}, unique = true)})
public class Category {

    @PrimaryKey(autoGenerate = true)
    public long id;

    /**
     * Tên của danh mục, ví dụ: "Ăn uống", "Lương", "Đi lại".
     * Tên này là bắt buộc và không được trùng lặp.
     * @NonNull: Báo cho Room biết rằng cột này không được phép có giá trị null.
     */
    @NonNull
    @ColumnInfo(name = "name")
    public String name;

    /**
     * Loại danh mục: "INCOME" hoặc "EXPENSE".
     * Giúp chúng ta phân loại, ví dụ "Lương" là INCOME, "Ăn uống" là EXPENSE.
     */
    @NonNull
    @ColumnInfo(name = "type")
    public String type;

    /**
     * Tên của file icon trong thư mục 'drawable' (ví dụ: "ic_food", "ic_transport").
     * Lưu dưới dạng String giúp chúng ta linh hoạt trong việc hiển thị icon tương ứng.
     */
    @ColumnInfo(name = "icon_name")
    public String iconName;

    public Category(long id, @NonNull String name, @NonNull String type, String iconName) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.iconName = iconName;
    }

    public Category() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getType() {
        return type;
    }

    public void setType(@NonNull String type) {
        this.type = type;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }
    // Constructor, Getters và Setters
    // ...
}
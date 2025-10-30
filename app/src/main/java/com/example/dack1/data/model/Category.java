package com.example.dack1.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Entity đại diện cho bảng 'categories'.
 */
@Entity(tableName = "categories",
        indices = {@Index(value = {"name"}, unique = true)})
public class Category {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    @ColumnInfo(name = "name")
    public String name;

    @NonNull
    @ColumnInfo(name = "type", defaultValue = "EXPENSE") // Mặc định là chi tiêu
    public String type;

    // Sửa tên trường thành 'iconName' để khớp logic CostalActivity
    @ColumnInfo(name = "icon_name")
    public String iconName;

    @ColumnInfo(name = "color")
    public String color; // Thêm trường color

    /**
     * Constructor rỗng cho Room.
     */
    public Category() {
    }

    /**
     * Constructor @Ignore dùng khi tạo mới Category từ UI.
     * Nhận đủ các trường cần thiết.
     */
    @Ignore
    public Category(@NonNull String name, @NonNull String type, String iconName, String color) {
        this.name = name;
        this.type = type;
        this.iconName = iconName; // Gán vào iconName
        this.color = color;       // Gán vào color
    }

    // --- Getters and Setters ---

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

    // Getter/Setter cho iconName (khớp tên trường)
    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    // Getter/Setter cho color (khớp tên trường)
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
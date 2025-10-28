package com.example.mainscreen3.data.local.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class CategoryModel implements Serializable {
    private String id;
    private String name;
    private int iconResource;
    private String type;
    private int colorResource;
    private boolean isCustom;

    public CategoryModel() {
        this.id = UUID.randomUUID().toString();
        this.isCustom = true;
    }

    public CategoryModel(String name, int iconResource, String type, int colorResource) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.iconResource = iconResource;
        this.type = type;
        this.colorResource = colorResource;
        this.isCustom = true;
    }

    public CategoryModel(String name, int iconResource, String type, int colorResource, boolean isCustom) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.iconResource = iconResource;
        this.type = type;
        this.colorResource = colorResource;
        this.isCustom = isCustom;
    }

    public String getId() { return id; }
    public boolean isCustom() { return isCustom; }
    public String getName() { return name; }
    public int getIconResource() { return iconResource; }
    public String getType() { return type; }
    public int getColorResource() { return colorResource; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryModel that = (CategoryModel) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        // Hash bằng ID
        return Objects.hash(id);
    }
}

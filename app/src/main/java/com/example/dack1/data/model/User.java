package com.example.dack1.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * User entity for authentication.
 */
@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    public long id;
    
    @ColumnInfo(name = "email")
    public String email;
    
    @ColumnInfo(name = "password")
    public String password;

    @ColumnInfo(name = "name") // Thêm dòng này
    public String name;        // Thêm dòng này

    public User() {}

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

package com.example.mudu.warnabruv.datalayer.model;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "test_table")
public class TestDataBaseEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    @NonNull
    private int id;

    @ColumnInfo
    private String name;

    @ColumnInfo
    private String address;

    @ColumnInfo
    private String mobile;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }


}

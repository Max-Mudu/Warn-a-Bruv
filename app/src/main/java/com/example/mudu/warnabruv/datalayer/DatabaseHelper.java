package com.example.mudu.warnabruv.datalayer;

import android.content.Context;

import androidx.room.Room;

import com.example.mudu.warnabruv.datalayer.model.TestDataBaseEntity;

import java.util.List;


public class DatabaseHelper {

    private static final String DB_NAME = "database.db";
    private static DatabaseHelper databaseHelper;
    private MyDatabase myDatabase;

    private DatabaseHelper(Context context) {
        myDatabase = Room.databaseBuilder(context, MyDatabase.class, DB_NAME).fallbackToDestructiveMigration().build();
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper(context);
        }

        return databaseHelper;
    }

    public void addTestModel(TestDataBaseEntity testModel) {
        myDatabase.testModelDao().savaData(testModel);
    }

    public List<TestDataBaseEntity> getAllData() {
        return myDatabase.testModelDao().getAllData();
    }
}

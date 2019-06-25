package com.example.mudu.warnabruv.datalayer;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.mudu.warnabruv.datalayer.model.TestDataBaseEntity;


@Database(entities = {TestDataBaseEntity.class}, version = 1, exportSchema = false)
public abstract class MyDatabase extends RoomDatabase {

    abstract MyTestDAO testModelDao();
}

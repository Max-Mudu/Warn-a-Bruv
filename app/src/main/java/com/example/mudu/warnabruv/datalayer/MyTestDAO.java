package com.example.mudu.warnabruv.datalayer;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mudu.warnabruv.datalayer.model.TestDataBaseEntity;

import java.util.List;


@Dao
public interface MyTestDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long savaData(TestDataBaseEntity myTestModel);

    @Query("select * from test_table")
    List<TestDataBaseEntity> getAllData();
}

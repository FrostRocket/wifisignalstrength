package com.frostrocket.signalstrength.persistance.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.frostrocket.signalstrength.persistance.entity.DataPoint;

import java.util.List;

/**
 * Dao for the DataPoint entity.
 */
@Dao
public interface DataPointDao {

    @Query("SELECT * FROM datapoint")
    List<DataPoint> getAll();

    @Query("SELECT * FROM datapoint WHERE id = :id")
    DataPoint get(long id);

    @Query("SELECT COUNT(*) from datapoint")
    int count();

    @Insert
    void insertAll(DataPoint... dataPoints);

    @Insert
    void insert(DataPoint dataPoint);

    @Update
    void update(DataPoint dataPoint);

    @Delete
    void delete(DataPoint dataPoint);
}

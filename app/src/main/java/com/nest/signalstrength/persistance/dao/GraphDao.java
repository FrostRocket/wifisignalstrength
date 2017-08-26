package com.nest.signalstrength.persistance.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.nest.signalstrength.persistance.entity.DataPoint;
import com.nest.signalstrength.persistance.entity.Graph;

import java.util.List;

/**
 * Dao for the Graph entity.
 */
@Dao
public interface GraphDao {

    @Query("SELECT * FROM graph")
    List<Graph> getAll();

    @Query("SELECT * FROM graph WHERE id = :id")
    Graph get(String id);

    @Query("SELECT COUNT(*) from graph")
    int count();

    @Insert
    void insertAll(Graph... graphs);

    @Insert
    void insert(Graph graph);

    @Update
    void update(Graph graph);

    @Delete
    void delete(Graph graph);

    @Query("SELECT * FROM datapoint WHERE graphId = :graphId")
    List<DataPoint> getDataPoints(String graphId);
}

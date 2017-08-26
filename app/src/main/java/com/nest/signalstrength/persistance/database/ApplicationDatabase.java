package com.nest.signalstrength.persistance.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.nest.signalstrength.persistance.dao.DataPointDao;
import com.nest.signalstrength.persistance.dao.GraphDao;
import com.nest.signalstrength.persistance.entity.DataPoint;
import com.nest.signalstrength.persistance.entity.Graph;

/**
 * Singleton that handles creation and access to application database.
 */
@Database(entities = {Graph.class, DataPoint.class}, version = 1)
public abstract class ApplicationDatabase extends RoomDatabase {

    private static volatile ApplicationDatabase instance;

    public abstract GraphDao graphDao();

    public abstract DataPointDao dataPointDao();

    public static ApplicationDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (ApplicationDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(), ApplicationDatabase.class, "database").build();
                }
            }
        }

        return instance;
    }

    public static void destroyInstance() {
        instance = null;
    }
}
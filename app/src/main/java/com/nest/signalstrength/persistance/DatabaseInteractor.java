package com.nest.signalstrength.persistance;

import android.content.Context;
import android.support.annotation.WorkerThread;

import com.nest.signalstrength.persistance.database.ApplicationDatabase;
import com.nest.signalstrength.persistance.entity.DataPoint;
import com.nest.signalstrength.persistance.entity.Graph;

import java.util.List;
import java.util.Queue;

/**
 * Singleton that handles all interaction with the database, rather than accessing it directly.
 * Also provides useful helper methods for obfuscating those interactions.
 */
public class DatabaseInteractor {

    private static volatile DatabaseInteractor instance;
    private static ApplicationDatabase database;

    public static DatabaseInteractor getInstance(Context context) {
        if (instance == null) {
            synchronized (DatabaseInteractor.class) {
                if (instance == null) {
                    instance = new DatabaseInteractor(context);
                }
            }
        }

        return instance;
    }

    private DatabaseInteractor(Context context) {
        database = ApplicationDatabase.getInstance(context);
    }

    @WorkerThread
    public List<Graph> getGraphs() {
        return database.graphDao().getAll();
    }

    @WorkerThread
    public Graph getGraph(String id) {
        return database.graphDao().get(id);
    }

    @WorkerThread
    public void addGraph(Graph graph) {
        database.graphDao().insert(graph);
    }

    @WorkerThread
    public void deleteGraph(String id) {
        Graph graph = database.graphDao().get(id);
        database.graphDao().delete(graph);
    }

    @WorkerThread
    public List<DataPoint> getDataPointsForGraph(String id) {
        return database.graphDao().getDataPoints(id);
    }

    @WorkerThread
    public void addDataPoint(DataPoint dataPoint) {
        database.dataPointDao().insert(dataPoint);
    }

    @WorkerThread
    public void addDataPoints(Queue<DataPoint> dataPoints) {
        database.dataPointDao().insertAll(dataPoints.toArray(new DataPoint[dataPoints.size()]));
    }

    public static void destroyInstance() {
        if (instance != null) {
            instance = null;
            ApplicationDatabase.destroyInstance();
        }
    }
}
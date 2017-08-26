package com.nest.signalstrength;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.nest.signalstrength.persistance.dao.GraphDao;
import com.nest.signalstrength.persistance.database.ApplicationDatabase;
import com.nest.signalstrength.persistance.entity.Graph;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class GraphDaoTest {

    private GraphDao graphDao;
    private ApplicationDatabase database;

    @Before
    public void setUp() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        database = Room.inMemoryDatabaseBuilder(context, ApplicationDatabase.class).allowMainThreadQueries().build();
        graphDao = database.graphDao();
    }

    @Test
    public void databaseCreation() {
        assertNotNull(database);
    }

    @Test
    public void daoCreation() {
        assertNotNull(graphDao);
    }

    @Test
    public void verifyOneCount() {
        graphDao.insert(new Graph());

        assertEquals(1, graphDao.count());
    }

    @Test
    public void verifyManyCount() {
        graphDao.insert(new Graph());
        graphDao.insert(new Graph());

        assertEquals(2, graphDao.count());
    }

    @Test
    public void insertOneGraph() {
        Graph graph = new Graph();

        String id = graph.getId();

        graphDao.insert(graph);

        List<Graph> graphs = graphDao.getAll();

        assertEquals(1, graphs.size());
        assertEquals(id, graphs.get(0).getId());
    }

    @Test
    public void insertManyGraphs() {
        List<Graph> graphs = new ArrayList<>();

        Graph graph1 = new Graph();
        Graph graph2 = new Graph();
        Graph graph3 = new Graph();

        graphs.add(graph1);
        graphs.add(graph2);
        graphs.add(graph3);

        String id1 = graph1.getId();
        String id2 = graph2.getId();
        String id3 = graph3.getId();

        graphDao.insertAll(graphs.toArray(new Graph[graphs.size()]));

        List<Graph> loadedGraphs = graphDao.getAll();

        assertEquals(3, loadedGraphs.size());

        assertEquals(id1, loadedGraphs.get(0).getId());
        assertEquals(id2, loadedGraphs.get(1).getId());
        assertEquals(id3, loadedGraphs.get(2).getId());
    }

    @Test
    public void deleteGraph() {
        graphDao.insert(new Graph());

        List<Graph> graphs = graphDao.getAll();

        assertEquals(1, graphs.size());

        graphDao.delete(graphs.get(0));

        assertEquals(0, graphDao.count());
    }

    @After
    public void tearDown() throws Exception {
        database.close();
    }
}
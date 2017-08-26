package com.frostrocket.signalstrength;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.frostrocket.signalstrength.persistance.dao.DataPointDao;
import com.frostrocket.signalstrength.persistance.database.ApplicationDatabase;
import com.frostrocket.signalstrength.persistance.entity.DataPoint;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class DataPointDaoTest {

    private DataPointDao dataPointDao;
    private ApplicationDatabase database;

    @Before
    public void setUp() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        database = Room.inMemoryDatabaseBuilder(context, ApplicationDatabase.class).allowMainThreadQueries().build();
        dataPointDao = database.dataPointDao();
    }

    @Test
    public void databaseCreation() {
        assertNotNull(database);
    }

    @Test
    public void daoCreation() {
        assertNotNull(dataPointDao);
    }

    @Test
    public void verifyOneCount() {
        dataPointDao.insert(new DataPoint(1337));

        assertEquals(1, dataPointDao.count());
    }

    @Test
    public void verifyManyCount() {
        dataPointDao.insert(new DataPoint(1337));
        dataPointDao.insert(new DataPoint(7331));

        assertEquals(2, dataPointDao.count());
    }

    @Test
    public void insertOneDataPoint() {
        dataPointDao.insert(new DataPoint(1337));

        List<DataPoint> dataPoints = dataPointDao.getAll();

        assertEquals(1, dataPoints.size());

        DataPoint dataPoint = dataPoints.get(0);

        assertEquals(1, dataPoint.getId());
    }

    @Test
    public void insertManyDataPoints() {
        List<DataPoint> dataPoints = new ArrayList<>();

        dataPoints.add(new DataPoint(0));
        dataPoints.add(new DataPoint(100));
        dataPoints.add(new DataPoint(1337));

        dataPointDao.insertAll(dataPoints.toArray(new DataPoint[dataPoints.size()]));

        assertEquals(3, dataPoints.size());

        DataPoint dataPoint1 = dataPoints.get(0);
        DataPoint dataPoint2 = dataPoints.get(1);
        DataPoint dataPoint3 = dataPoints.get(2);

        assertEquals(0, dataPoint1.getValue());
        assertEquals(100, dataPoint2.getValue());
        assertEquals(1337, dataPoint3.getValue());
    }

    @Test
    public void updateDataPoint() {
        dataPointDao.insert(new DataPoint(1337));

        List<DataPoint> dataPoints = dataPointDao.getAll();

        DataPoint dataPoint = dataPoints.get(0);
        dataPoint.setValue(7331);

        long id = dataPoint.getId();

        dataPointDao.update(dataPoint);

        DataPoint updatedDataPoint = dataPointDao.get(id);

        assertEquals(7331, updatedDataPoint.getValue());
    }


    @Test
    public void deleteDataPoint() {
        dataPointDao.insert(new DataPoint(1337));

        List<DataPoint> dataPoints = dataPointDao.getAll();

        assertEquals(1, dataPoints.size());

        dataPointDao.delete(dataPoints.get(0));

        assertEquals(0, dataPointDao.count());
    }

    @After
    public void tearDown() throws Exception {
        database.close();
    }
}
package com.frostrocket.signalstrength.data;

import com.frostrocket.signalstrength.persistance.entity.DataPoint;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Queue;

import io.reactivex.observers.TestObserver;

import static org.junit.Assert.assertNotNull;

public class WifiDataProviderTest {

    private WifiDataProvider provider;

    @Before
    public void setUp() throws Exception {
        provider = WifiDataProvider.getInstance();
    }

    @Test
    public void instanceCreation() {
        assertNotNull(provider);
    }

    @Test
    public void providerQueueCreated() {
        TestObserver<DataPoint> dataPointTestObserver = provider.getDataPointObservable().test();
        TestObserver<Queue<DataPoint>> queueTestObserver = provider.getQueueSubject().test();

        DataPoint dataPoint = new DataPoint(0);
        DataPoint dataPoint1 = new DataPoint(1);
        DataPoint dataPoint2 = new DataPoint(2);

        //when
        dataPointTestObserver.onNext(dataPoint);
        dataPointTestObserver.onNext(dataPoint1);
        dataPointTestObserver.onNext(dataPoint2);

        // then
        queueTestObserver
                .assertNoErrors()
                .assertValueCount(1);
    }

    @After
    public void tearDown() throws Exception {
        WifiDataProvider.destroyInstance();
    }
}
package com.nest.signalstrength.data;

import com.nest.signalstrength.persistance.entity.DataPoint;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

/**
 * Singleton that provides all "Wi-Fi" related data in order to back views.
 */
public class WifiDataProvider {

    private static volatile WifiDataProvider instance;

    public static final int MAX_SIGNAL_STRENGTH = 100;
    private static final int INTERVAL_IN_SECONDS = 1;
    private static final int MAX_INTERVAL = 30;

    private Queue<DataPoint> queue;
    private BehaviorSubject<Queue<DataPoint>> queueSubject;

    public static WifiDataProvider getInstance() {
        if (instance == null) {
            synchronized (WifiDataProvider.class) {
                if (instance == null) {
                    instance = new WifiDataProvider();
                }
            }
        }

        return instance;
    }

    /**
     * Using a CircularFifoQueue to purge values after we reach the queue limit
     */
    private WifiDataProvider() {
        queue = new CircularFifoQueue<>(MAX_INTERVAL);
        queueSubject = BehaviorSubject.createDefault(queue);

        getDataPointObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::enqueue);
    }

    /**
     * This method returns an observable that emits a random number and datapoint every second
     */
    Observable<DataPoint> getDataPointObservable() {
        return Observable.interval(0, INTERVAL_IN_SECONDS, TimeUnit.SECONDS)
                .map(__ -> getWifiSignalStrength());
    }

    /**
     * This method returns a subject that listens observable that emits the values of the queue every second
     */
    public Observable<Queue<DataPoint>> getQueueSubject() {
        return queueSubject;
    }

    /**
     * This method mocks Wi-Fi signal strength data by providing a generated random number
     * between 1 and 100 (inclusive) and creates a new DataPoint object with timestamp set to the current time
     */
    private DataPoint getWifiSignalStrength() {
        return new DataPoint(new Random().nextInt(MAX_SIGNAL_STRENGTH + 1));
    }

    private void enqueue(DataPoint dataPoint) {
        queue.add(dataPoint);
        queueSubject.onNext(queue);
    }

    public Queue<DataPoint> getQueue() {
        return queue;
    }

    public static void destroyInstance() {
        instance = null;
    }
}
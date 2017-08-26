package com.nest.signalstrength.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.nest.signalstrength.R;
import com.nest.signalstrength.data.WifiDataProvider;
import com.nest.signalstrength.persistance.DatabaseInteractor;
import com.nest.signalstrength.persistance.entity.DataPoint;
import com.nest.signalstrength.persistance.entity.Graph;
import com.nest.signalstrength.view.GraphView;

import java.util.Queue;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.nest.signalstrength.R.id.graph;

public class LiveActivity extends AppCompatActivity {

    private GraphView graphView;
    private CoordinatorLayout coordinatorLayout;

    private DatabaseInteractor interactor;
    private WifiDataProvider provider;

    CompositeDisposable disposables = new CompositeDisposable();

    public static Intent newIntent(Context context) {
        return new Intent(context, LiveActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.a_graph);
        setSupportActionBar(findViewById(R.id.toolbar));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        graphView = findViewById(graph);
        coordinatorLayout = findViewById(R.id.coordinator);

        interactor = DatabaseInteractor.getInstance(this);
        provider = WifiDataProvider.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Observe our queue subject to emit items to our graph view whenever an update occurs
        disposables.add(provider.getQueueSubject()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dataPoints -> graphView.setDataPoints(dataPoints)));
    }

    @Override
    protected void onStop() {
        super.onStop();

        disposables.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        DatabaseInteractor.destroyInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.m_live, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveGraphToDatabase();
                Snackbar.make(coordinatorLayout, R.string.save_snackbar_message, Snackbar.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This method saves the graph and datapoint objects to persistant storage by getting current
     * queue values from the data provider.
     * <p>
     * Triggered when the user hit's the save button in the toolbar.
     */
    public void saveGraphToDatabase() {
        Queue<DataPoint> queue = provider.getQueue();

        Graph graph = new Graph();

        disposables.add(Completable.fromAction(() -> interactor.addGraph(graph))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe());

        for (DataPoint dataPoint : queue) {
            dataPoint.setGraphId(graph.getId());
        }

        disposables.add(Completable.fromAction(() -> interactor.addDataPoints(queue))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe());
    }
}

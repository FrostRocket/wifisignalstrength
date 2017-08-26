package com.frostrocket.signalstrength.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.frostrocket.signalstrength.R;
import com.frostrocket.signalstrength.persistance.DatabaseInteractor;
import com.frostrocket.signalstrength.view.GraphView;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class SavedActivity extends AppCompatActivity {

    public static final String EXTRA_GRAPH_ID = "com.frostrocket.signalstrength.graphId";

    private GraphView graphView;
    private AlertDialog deleteDialog;

    private DatabaseInteractor interactor;

    private CompositeDisposable disposables = new CompositeDisposable();

    public static Intent newIntent(Context context, String graphId) {
        Intent intent = new Intent(context, SavedActivity.class);
        intent.putExtra(EXTRA_GRAPH_ID, graphId);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.a_graph);
        setSupportActionBar(findViewById(R.id.toolbar));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        graphView = findViewById(R.id.graph);

        interactor = DatabaseInteractor.getInstance(getApplicationContext());
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Fetch stored data points for a graph (off the main thread) and display them in the GraphView (on main thread)
        disposables.add(Single.fromCallable(() -> interactor.getDataPointsForGraph(getIntent().getStringExtra(EXTRA_GRAPH_ID)))
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

        // Used to avoid leaking the dialog window
        if (deleteDialog != null) {
            deleteDialog.dismiss();
        }

        DatabaseInteractor.destroyInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.m_saved, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                displayConfirmationDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This method creates a confirmation dialog to ask the user if they want to delete the graph.
     * <p>
     * Triggered when the user clicks the delete button in the toolbar.
     */
    private void displayConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.delete_dialog_title);
        builder.setMessage(R.string.delete_dialog_message);
        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
            deleteGraph();
        });
        builder.setNegativeButton(R.string.no, (dialog, which) -> {
        });

        deleteDialog = builder.create();
        deleteDialog.show();
    }

    /**
     * This method deletes a Graph object off the main thread.
     */
    private void deleteGraph() {
        disposables.add(Completable.fromAction(() -> interactor.deleteGraph(getIntent().getStringExtra(EXTRA_GRAPH_ID)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());

        finish();
    }
}

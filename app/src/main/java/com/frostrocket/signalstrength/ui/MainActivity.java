package com.frostrocket.signalstrength.ui;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.frostrocket.signalstrength.R;
import com.frostrocket.signalstrength.persistance.DatabaseInteractor;
import com.frostrocket.signalstrength.persistance.entity.Graph;

import java.util.Collections;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GraphsAdapter.OnGraphSelectedListener {

    private RecyclerView recyclerView;
    private GraphsAdapter adapter;

    private DatabaseInteractor interactor;

    private CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.a_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView liveDataHeader = findViewById(R.id.live_header).findViewById(R.id.header);
        TextView recyclerHeader = findViewById(R.id.saved_header).findViewById(R.id.header);

        // Re-using layouts so the text needs to be set dynamically
        liveDataHeader.setText(R.string.live_header);
        recyclerHeader.setText(R.string.saved_header);

        recyclerView = findViewById(R.id.saved_graphs);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.item_decoration));
        recyclerView.addItemDecoration(dividerItemDecoration);

        adapter = new GraphsAdapter(this);
        recyclerView.setAdapter(adapter);

        interactor = DatabaseInteractor.getInstance(getApplicationContext());
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Fetch the list of saved graphs off the main thread
        disposables.add(Single.fromCallable(() -> interactor.getGraphs())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::initializeAdapter));
    }

    private void initializeAdapter(List<Graph> graphs) {
        // We want to display information in reverse chronological order
        Collections.reverse(graphs);

        adapter.addAll(graphs);
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
    public void onGraphSelected(Graph graph) {
        startActivity(SavedActivity.newIntent(this, graph.getId()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.live_button:
                startActivity(LiveActivity.newIntent(this));
                break;
        }
    }
}

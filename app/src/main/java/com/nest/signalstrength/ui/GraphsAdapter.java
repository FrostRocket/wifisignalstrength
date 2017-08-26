package com.nest.signalstrength.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.nest.signalstrength.R;
import com.nest.signalstrength.persistance.entity.Graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter to hold data for all saved graphs.
 */
class GraphsAdapter extends RecyclerView.Adapter<GraphsViewHolder> implements GraphsViewHolder.OnItemClickListener {
    interface OnGraphSelectedListener {
        void onGraphSelected(Graph graph);
    }

    private List<Graph> graphs;
    private final OnGraphSelectedListener listener;

    GraphsAdapter(OnGraphSelectedListener listener) {
        this.graphs = new ArrayList<>();
        this.listener = listener;
    }

    @Override
    public GraphsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GraphsViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.r_saved_graph_item, parent, false), this);
    }

    @Override
    public void onBindViewHolder(GraphsViewHolder viewHolder, int position) {
        viewHolder.bindView(graphs.get(position));
    }

    @Override
    public int getItemCount() {
        return graphs.size();
    }

    @Override
    public void onItemClick(int position) {
        listener.onGraphSelected(graphs.get(position));
    }

    public void addAll(List<Graph> graphs) {
        this.graphs = graphs;
        notifyDataSetChanged();
    }
}

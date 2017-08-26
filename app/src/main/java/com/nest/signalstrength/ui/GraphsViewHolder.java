package com.nest.signalstrength.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.nest.signalstrength.R;
import com.nest.signalstrength.persistance.entity.Graph;

import java.text.DateFormat;
import java.util.Date;

/**
 * ViewHolder to display data for all saved graphs.
 */
class GraphsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    interface OnItemClickListener {
        void onItemClick(int position);
    }

    private final DateFormat dateFormat;
    private final OnItemClickListener listener;
    private final TextView timestamp;

    GraphsViewHolder(View itemView, OnItemClickListener listener) {
        super(itemView);

        this.dateFormat = DateFormat.getDateTimeInstance();
        this.listener = listener;
        this.timestamp = itemView.findViewById(R.id.timestamp);

        itemView.setOnClickListener(this);
    }

    void bindView(Graph graph) {
        timestamp.setText(dateFormat.format(new Date(graph.getTimestamp())));
    }

    @Override
    public void onClick(View v) {
        listener.onItemClick(getAdapterPosition());
    }
}

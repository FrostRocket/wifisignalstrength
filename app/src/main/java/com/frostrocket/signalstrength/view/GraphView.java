package com.frostrocket.signalstrength.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.frostrocket.signalstrength.R;
import com.frostrocket.signalstrength.data.WifiDataProvider;
import com.frostrocket.signalstrength.persistance.entity.DataPoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * View that draws a scaled X/Y axis, scaled chart grid, and datapoints.
 */

public class GraphView extends View {
    private float dotRadius = 12f;
    private float strokeWidth = 8f;
    private int indicatorSize = 20; // in pixels

    private float xUnitSize;
    private float yUnitSize;

    private List<DataPoint> dataPoints;

    private Point origin;

    private Path xPath;
    private Path yPath;
    private Path dataPath;

    private Paint gridPaint;
    private Paint axisPaint;
    private Paint dotPaint;
    private Paint dataPaint;

    private void initialize() {
        dataPoints = new ArrayList<>();

        origin = new Point();

        xPath = new Path();
        yPath = new Path();
        dataPath = new Path();

        gridPaint = new Paint();
        gridPaint.setAntiAlias(true);
        gridPaint.setStrokeWidth(strokeWidth / 2);
        gridPaint.setColor(Color.WHITE);
        gridPaint.setAlpha(10);
        gridPaint.setStyle(Paint.Style.STROKE);

        axisPaint = new Paint();
        axisPaint.setAntiAlias(true);
        axisPaint.setStrokeWidth(strokeWidth / 2);
        axisPaint.setColor(Color.WHITE);
        axisPaint.setStyle(Paint.Style.STROKE);

        dotPaint = new Paint();
        dotPaint.setAntiAlias(true);
        dotPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        dotPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        dataPaint = new Paint();
        dataPaint.setAntiAlias(true);
        dataPaint.setStrokeWidth(strokeWidth);
        dataPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        dataPaint.setPathEffect(new CornerPathEffect(10f));
        dataPaint.setStyle(Paint.Style.STROKE);
    }

    public GraphView(Context context) {
        this(context, null);
    }

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.GraphView, 0, 0);

        try {
            dotRadius = a.getFloat(R.styleable.GraphView_pointRadius, dotRadius);
            strokeWidth = a.getFloat(R.styleable.GraphView_strokeWidth, strokeWidth);
        } finally {
            a.recycle();
        }

        initialize();
    }

    public void setDataPoints(Collection<DataPoint> dataPoints) {
        this.dataPoints.clear();
        this.dataPoints.addAll(dataPoints);

        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Set the origin to be a traditional (0,0) starting at the bottom left
        origin.set(0, canvas.getHeight());

        // Calculate unit size based on canvas size and number of items (for each axis)
        xUnitSize = (float) canvas.getWidth() / (float) Math.max(1, dataPoints.size() - 1);
        yUnitSize = (float) canvas.getHeight() / (float) WifiDataProvider.MAX_SIGNAL_STRENGTH;

        // Draw the X-Axis
        drawXAxis(canvas);

        // Draw the Y-Axis
        drawYAxis(canvas);

        // Draw the data line
        drawDataLine(canvas);
    }

    /**
     * Draw the X-Axis
     * <p>
     * 1. Start by moving to the origin.
     * 2. Add a path section the width of our unit size.
     * 3. Draw a small unit indicator on the line, moving inward.
     * 4. Draw a grid line extending to the height of the graph.
     * 5. Repeat step 2 and 3 until we reach the end of the canvas.
     * 6. Draw the final axis path.
     */
    private void drawXAxis(Canvas canvas) {
        xPath.reset();
        xPath.moveTo(origin.x, origin.y);

        for (int i = 0; i < dataPoints.size(); i++) {
            float x = Math.max(xUnitSize, xUnitSize * i);
            int y = origin.y;

            xPath.lineTo(x, y);
            canvas.drawLine(x, y, x, 0, gridPaint);
            canvas.drawLine(x, y, x, y - indicatorSize, axisPaint);
        }

        canvas.drawPath(xPath, axisPaint);
    }

    /**
     * Draw the Y-Axis
     * <p>
     * 1. Start by moving to the origin.
     * 2. Add a path section the height of our unit size.
     * 3. Draw a small unit indicator on the line, moving inward.
     * 4. Draw a grid line extending to the width of the graph.
     * 5. Repeat step 2 and 3 until we reach the end of the canvas.
     * 6. Draw the final axis path.
     */
    private void drawYAxis(Canvas canvas) {
        yPath.reset();
        yPath.moveTo(origin.x, origin.y);

        for (int i = 0; i < WifiDataProvider.MAX_SIGNAL_STRENGTH; i++) {
            int x = origin.x;
            float y = yUnitSize * i;

            // Only drawing grid lines for multiples of 5
            if (i % 5 == 0) {
                yPath.lineTo(x, y);
                canvas.drawLine(x, y, canvas.getWidth(), y, gridPaint);
                canvas.drawLine(x, y, x + indicatorSize, y, axisPaint);
            }
        }

        canvas.drawPath(yPath, axisPaint);
    }

    /**
     * Draw the data line
     * <p>
     * 1. Start by moving our starting point to be the first item in our points list.
     * 2. Calculate x, y offset
     * 3. Draw a line connecting the coordinates.
     * 4. Draw a small dot for each point.
     * 5. Repeat until we run out of datapoints.
     */
    private void drawDataLine(Canvas canvas) {
        dataPath.reset();

        if (!dataPoints.isEmpty()) {
            dataPath.moveTo(0, canvas.getHeight() - dataPoints.get(0).getValue() * yUnitSize);

            for (int i = 0; i < dataPoints.size(); i++) {
                float x = i * xUnitSize;
                float y = canvas.getHeight() - dataPoints.get(i).getValue() * yUnitSize;

                dataPath.lineTo(x, y);
                canvas.drawCircle(x, y, dotRadius, dotPaint);
            }

            canvas.drawPath(dataPath, dataPaint);
        }
    }
}

package com.ohmatthew.indecisive.activities.Main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ohmatthew.indecisive.models.ChoiceSlice;
import com.ohmatthew.indecisive.R;

import java.util.ArrayList;


public class ChoicesRecyclerViewAdapter extends RecyclerView.Adapter {
    private ArrayList<ChoiceSlice> dataSet;
    private ActivityInterface activityInterface;

    public ChoicesRecyclerViewAdapter(ArrayList<ChoiceSlice> dataSet, ActivityInterface activityInterface) {
        this.dataSet = dataSet;
        this.activityInterface = activityInterface;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ChoiceRowView v = new ChoiceRowView(parent.getContext());
        RowViewHolder vh = new RowViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((RowViewHolder) holder).rowView.initChoiceSlice(dataSet.get(position), position, activityInterface);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class RowViewHolder extends RecyclerView.ViewHolder {
        public ChoiceRowView rowView;
        public RowViewHolder(ChoiceRowView rowView){
            super(rowView);
            this.rowView = rowView;
        }
    }


    public class ChoiceRowView extends RelativeLayout {
        int state = 0;
        ChoiceSlice choiceSlice;
        private Paint colorTabPaint;
        private Path colorTabPath;
        int position;

        private Context context;
        DisplayMetrics displayMetrics;

        public ChoiceRowView(final Context context) {
            super(context);
            this.context = context;

            inflate(getContext(), R.layout.list_item_choice, this);
            setBackgroundColor(Color.WHITE);
        }

        public void initChoiceSlice(ChoiceSlice slice, final int pos, final ActivityInterface viewInterface) {
            this.choiceSlice = slice;
            this.position = pos;

            displayMetrics = context.getResources().getDisplayMetrics();

            ((TextView) this.findViewById(R.id.item_name)).setText(choiceSlice.getName());

            colorTabPaint = new Paint();
            colorTabPaint.setStyle(Paint.Style.FILL);
            colorTabPaint.setColor((Integer) choiceSlice.getColor());

            this.findViewById(R.id.action_remove).setOnClickListener(new OnClickListener() {
                public void onClick(View _v) {
                    final ImageView v = (ImageView) _v;
                    if (state == 0) {
                        v.setImageResource(R.drawable.ic_highlight_remove);
                        state = 1;
                    } else if (state == 1) {
                        v.setImageResource(R.drawable.ic_remove_circle_outline);
                        state = 0;
                        viewInterface.removeChoice(pos);
                    }
                }

            });

            invalidate();
        }

        public void updatePosition(final int pos) {
            this.position = pos;

            this.findViewById(R.id.action_remove).setOnClickListener(new OnClickListener() {
                public void onClick(View _v) {
                    final ImageView v = (ImageView) _v;
                    if (state == 0) {
                        v.setImageResource(R.drawable.ic_highlight_remove);
                        state = 1;
                    } else if (state == 1) {
                        v.setImageResource(R.drawable.ic_remove_circle_outline);
                        state = 0;
                        dataSet.remove(pos);
                        activityInterface.removeChoice(pos);
                    }
                }

            });
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            int widthDp = 5;
            int widthPx = (int) (widthDp * displayMetrics.density);

            colorTabPath = new Path();
            colorTabPath.addRect(0, 0, widthPx, getMeasuredHeight(), Path.Direction.CW);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (colorTabPaint != null) {
                canvas.drawPath(colorTabPath, colorTabPaint);
            }
        }
    }
}
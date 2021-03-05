package com.example.rvscrolled;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

public class TvGridLayoutManager extends GridLayoutManager {


    public TvGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public TvGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public TvGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }


    /**
     * Base class which scrolls to selected view in onStop().
     */
    abstract class GridLinearSmoothScroller extends LinearSmoothScroller {


        public GridLinearSmoothScroller(Context context) {
            super(context);
        }


        @Override
        protected void onStop() {
            super.onStop();
            View targetView = findViewByPosition(getTargetPosition());

            if (targetView != null) {
                targetView.requestFocus();
            }
            super.onStop();
        }

    }


    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state,
                                       int position) {
        GridLinearSmoothScroller linearSmoothScroller =
                new GridLinearSmoothScroller(recyclerView.getContext()) {
                    @Override
                    public PointF computeScrollVectorForPosition(int targetPosition) {
                        return computeVectorForPosition(targetPosition);
                    }
                };
        linearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);
    }


    public PointF computeVectorForPosition(int targetPosition) {
        return super.computeScrollVectorForPosition(targetPosition);
    }



}

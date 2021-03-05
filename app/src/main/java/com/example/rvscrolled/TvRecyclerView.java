package com.example.rvscrolled;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



import static android.view.KeyEvent.KEYCODE_DPAD_DOWN;



public class TvRecyclerView extends RecyclerView {
    private static final String TAG = "TvRecyclerView";

    private int position;

    //焦点是否居中
    private boolean mSelectedItemCentered;

    private int mSelectedItemOffsetStart;

    private int mSelectedItemOffsetEnd;



    private int mLoadMoreBeforehandCount = 0;

    public TvRecyclerView(Context context) {
        this(context, null);
    }

    public TvRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public TvRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        initView();

    }

    private void initView() {
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setHasFixedSize(true);
        setWillNotDraw(true);
        setOverScrollMode(View.OVER_SCROLL_NEVER);
        setChildrenDrawingOrderEnabled(true);

        setClipChildren(false);
        setClipToPadding(false);

        setClickable(false);
        setFocusable(true);
        setFocusableInTouchMode(true);

        setItemAnimator(null);
    }



    private int getFreeWidth() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    private int getFreeHeight() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }


    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    @Override
    public boolean hasFocus() {
        return super.hasFocus();
    }

    @Override
    public boolean isInTouchMode() {

        if (Build.VERSION.SDK_INT == 19) {
            return !(hasFocus() && !super.isInTouchMode());
        } else {
            return super.isInTouchMode();
        }

        //return super.isInTouchMode();
    }

    @Override
    public void requestChildFocus(View child, View focused) {

        if (null != child) {
            if (mSelectedItemCentered) {
                mSelectedItemOffsetStart = !isVertical() ? (getFreeWidth() - child.getWidth()) : (getFreeHeight() - child.getHeight());
                mSelectedItemOffsetStart /= 2;
                mSelectedItemOffsetEnd = mSelectedItemOffsetStart;
            }
        }
        super.requestChildFocus(child, focused);
    }

    @Override
    public boolean requestChildRectangleOnScreen(View child, Rect rect, boolean immediate) {
        final int parentLeft = getPaddingLeft();
        final int parentRight = getWidth() - getPaddingRight();

        final int parentTop = getPaddingTop();
        final int parentBottom = getHeight() - getPaddingBottom();

        final int childLeft = child.getLeft() + rect.left;
        final int childTop = child.getTop() + rect.top;

        final int childRight = childLeft + rect.width();
        final int childBottom = childTop + rect.height();

        final int offScreenLeft = Math.min(0, childLeft - parentLeft - mSelectedItemOffsetStart);
        final int offScreenRight = Math.max(0, childRight - parentRight + mSelectedItemOffsetEnd);

        final int offScreenTop = Math.min(0, childTop - parentTop - mSelectedItemOffsetStart);
        final int offScreenBottom = Math.max(0, childBottom - parentBottom + mSelectedItemOffsetEnd);


        final boolean canScrollHorizontal = getLayoutManager().canScrollHorizontally();
        final boolean canScrollVertical = getLayoutManager().canScrollVertically();

        // Favor the "start" layout direction over the end when bringing one side or the other
        // of a large rect into view. If we decide to bring in end because start is already
        // visible, limit the scroll such that start won't go out of bounds.
        final int dx;
        if (canScrollHorizontal) {
            if (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                dx = offScreenRight != 0 ? offScreenRight
                        : Math.max(offScreenLeft, childRight - parentRight);
            } else {
                dx = offScreenLeft != 0 ? offScreenLeft
                        : Math.min(childLeft - parentLeft, offScreenRight);
            }
        } else {
            dx = 0;
        }

        // Favor bringing the top into view over the bottom. If top is already visible and
        // we should scroll to make bottom visible, make sure top does not go out of bounds.
        final int dy;
        if (canScrollVertical) {
            dy = offScreenTop != 0 ? offScreenTop : Math.min(childTop - parentTop, offScreenBottom);
        } else {
            dy = 0;
        }


        if (dx != 0 || dy != 0) {
            if (immediate) {
                scrollBy(dx, dy);
            } else {
                smoothScrollBy(dx, dy);
            }

            postInvalidate();
            return true;
        }


        return false;
    }


    @Override
    public int getBaseline() {
        return -1;
    }


    public int getSelectedItemOffsetStart() {
        return mSelectedItemOffsetStart;
    }

    public int getSelectedItemOffsetEnd() {
        return mSelectedItemOffsetEnd;
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
    }


    private boolean isVertical() {
        LayoutManager manager = getLayoutManager();
        if (manager != null) {
            LinearLayoutManager layout = (LinearLayoutManager) getLayoutManager();
            return layout.getOrientation() == LinearLayoutManager.VERTICAL;

        }
        return false;
    }


    public void setSelectedItemOffset(int offsetStart, int offsetEnd) {
        setSelectedItemAtCentered(false);
        mSelectedItemOffsetStart = offsetStart;
        mSelectedItemOffsetEnd = offsetEnd;
    }


    public void setSelectedItemAtCentered(boolean isCentered) {
        this.mSelectedItemCentered = isCentered;
    }


    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        View view = getFocusedChild();
        if (null != view) {

            position = getChildAdapterPosition(view) - getFirstVisiblePosition();
            if (position < 0) {
                return i;
            } else {
                if (i == childCount - 1) {
                    if (position > i) {
                        position = i;
                    }
                    return position;
                }
                if (i == position) {
                    return childCount - 1;
                }
            }
        }
        return i;
    }

    public int getFirstVisiblePosition() {
        if (getChildCount() == 0)
            return 0;
        else
            return getChildAdapterPosition(getChildAt(0));
    }

    public int getLastVisiblePosition() {
        final int childCount = getChildCount();
        if (childCount == 0)
            return 0;
        else
            return getChildAdapterPosition(getChildAt(childCount - 1));
    }




    private OnLoadMoreListener mOnLoadMoreListener;

    public interface OnLoadMoreListener {
        void onLoadMore();
    }


    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.mOnLoadMoreListener = onLoadMoreListener;
    }

    private OnInterceptListener mInterceptLister;

    public void setOnInterceptListener(OnInterceptListener listener) {
        this.mInterceptLister = listener;
    }


    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
        return 0;
    }

    @Override
    public void onScrollStateChanged(int state) {
        if (state == SCROLL_STATE_IDLE) {
            // 加载更多回调
            if (null != mOnLoadMoreListener) {
                if (getLastVisiblePosition() >= getAdapter().getItemCount() - (1 + mLoadMoreBeforehandCount)) {
                    mOnLoadMoreListener.onLoadMore();
                }
            }
        }
        super.onScrollStateChanged(state);
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mInterceptLister != null && mInterceptLister.onIntercept(event)) {
            return true;
        }

        boolean result = super.dispatchKeyEvent(event);
        View focusView = this.getFocusedChild();
        if (focusView == null) {
            return result;
        } else {

            int dy = 0;
            int dx = 0;
            if (getChildCount() > 0) {
                View firstView = this.getChildAt(0);
                dy = firstView.getHeight();
                dx = firstView.getWidth();
            }
            if (event.getAction() == KeyEvent.ACTION_UP) {
                return true;
            } else {
                switch (event.getKeyCode()) {
                    case KeyEvent.KEYCODE_0:
                        Log.d("KEYCODE_0", "dispatchKeyEvent: ");

                       // onUpLevelListener.OnUpLevel(url);
                        return true;
                    case KeyEvent.KEYCODE_BACK:

                        return true;

                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        View rightView = FocusFinder.getInstance().findNextFocus(this, focusView, View.FOCUS_RIGHT);

                        if (rightView != null) {
                            rightView.requestFocus();
                            return true;
                        } else {
                            this.smoothScrollBy(dx, 0);
                            return true;
                        }
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                        View leftView = FocusFinder.getInstance().findNextFocus(this, focusView, View.FOCUS_LEFT);
                        Log.i(TAG, "leftView is null:" + (leftView == null));
                        if (leftView != null) {
                            leftView.requestFocus();
                            return true;
                        } else {
                            this.smoothScrollBy(-dx, 0);
                            return true;
                        }
                    case KEYCODE_DPAD_DOWN:
                        View downView = FocusFinder.getInstance().findNextFocus(this, focusView, View.FOCUS_DOWN);
                        Log.i(TAG, " downView is null:" + (downView == null));
                        if (downView != null) {
                            downView.requestFocus();
                            Log.d("rightView", "rightView is not  null:" );
                            return true;
                        } else {
                            this.smoothScrollBy(0, dy);
                            Log.d("rightView", "rightView is   null:");

                            return true;
                        }
                    case KeyEvent.KEYCODE_DPAD_UP:
                        View upView = FocusFinder.getInstance().findNextFocus(this, focusView, View.FOCUS_UP);
                        Log.i("xxxxxxxxasasas", "upView is null:" + (upView == null));
                        if (event.getAction() == KeyEvent.ACTION_UP) {
                            return true;
                        } else {
                            if (upView != null) {
                                upView.requestFocus();
                                return true;
                            } else {
                                this.smoothScrollBy(0, -dy);

                                return true;
                            }

                        }
                }

            }

        }
        return result;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        return super.onInterceptTouchEvent(e);
    }


    int pos=0;

    public void setSelectedPosition(int pos) {
        this.pos=pos;
        this.smoothScrollToPosition(pos);
    }
    public int getSelectedPosition() {
        return pos;
    }
    public void deleteSelectPosition(){
        this.smoothScrollBy(0, 0);
    }

    //防止Activity时,RecyclerView崩溃
    @Override
    protected void onDetachedFromWindow() {
        if (getLayoutManager() != null) {
            super.onDetachedFromWindow();
        }
    }


    //right
    public boolean isRightEdge(int childPosition) {
        LayoutManager layoutManager = getLayoutManager();

        if (layoutManager instanceof GridLayoutManager) {

            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            GridLayoutManager.SpanSizeLookup spanSizeLookUp = gridLayoutManager.getSpanSizeLookup();

            int totalSpanCount = gridLayoutManager.getSpanCount();
            int totalItemCount = gridLayoutManager.getItemCount();
            int childSpanCount = 0;

            for (int i = 0; i <= childPosition; i++) {
                childSpanCount += spanSizeLookUp.getSpanSize(i);
            }
            if (isVertical()) {
                if (childSpanCount % gridLayoutManager.getSpanCount() == 0) {
                    return true;
                }
            } else {
                int lastColumnSize = totalItemCount % totalSpanCount;
                if (lastColumnSize == 0) {
                    lastColumnSize = totalSpanCount;
                }
                if (childSpanCount > totalItemCount - lastColumnSize) {
                    return true;
                }
            }

        } else if (layoutManager instanceof LinearLayoutManager) {
            if (isVertical()) {
                return true;
            }else{
                return childPosition == getLayoutManager().getItemCount() - 1;
            }
        }

        return false;
    }

    //left
    public boolean isLeftEdge(int childPosition) {
        LayoutManager layoutManager = getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            GridLayoutManager.SpanSizeLookup spanSizeLookUp = gridLayoutManager.getSpanSizeLookup();

            int totalSpanCount = gridLayoutManager.getSpanCount();
            int childSpanCount = 0;
            for (int i = 0; i <= childPosition; i++) {
                childSpanCount += spanSizeLookUp.getSpanSize(i);
            }
            if (isVertical()) {
                if (childSpanCount % gridLayoutManager.getSpanCount() == 1) {
                    return true;
                }
            } else {
                if (childSpanCount <= totalSpanCount) {
                    return true;
                }
            }

        } else if (layoutManager instanceof LinearLayoutManager) {
            if (isVertical()) {
                return true;
            } else {
                return childPosition == 0;
            }

        }

        return false;
    }

    //top
    public boolean isTopEdge(int childPosition) {
        LayoutManager layoutManager = getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            GridLayoutManager.SpanSizeLookup spanSizeLookUp = gridLayoutManager.getSpanSizeLookup();

            int totalSpanCount = gridLayoutManager.getSpanCount();

            int childSpanCount = 0;
            for (int i = 0; i <= childPosition; i++) {
                childSpanCount += spanSizeLookUp.getSpanSize(i);
            }

            if (isVertical()) {
                if (childSpanCount <= totalSpanCount) {
                    return true;
                }
            } else {
                if (childSpanCount % totalSpanCount == 1) {
                    return true;
                }
            }


        } else if (layoutManager instanceof LinearLayoutManager) {
            if (isVertical()) {
                return childPosition == 0;
            } else {
                return true;
            }

        }

        return false;
    }

    //bottom
    public boolean isBottomEdge(int childPosition) {
        LayoutManager layoutManager = getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            GridLayoutManager.SpanSizeLookup spanSizeLookUp = gridLayoutManager.getSpanSizeLookup();
            int itemCount = gridLayoutManager.getItemCount();
            int childSpanCount = 0;
            int totalSpanCount = gridLayoutManager.getSpanCount();
            for (int i = 0; i <= childPosition; i++) {
                childSpanCount += spanSizeLookUp.getSpanSize(i);
            }
            if (isVertical()) {

                int lastRowCount = itemCount % totalSpanCount;
                if (lastRowCount == 0) {
                    lastRowCount = gridLayoutManager.getSpanCount();
                }
                if (childSpanCount > itemCount - lastRowCount) {
                    return true;
                }
            } else {
                if (childSpanCount % totalSpanCount == 0) {
                    return true;
                }
            }

        } else if (layoutManager instanceof LinearLayoutManager) {
            if (isVertical()) {
                return childPosition == getLayoutManager().getItemCount() - 1;
            } else {
                return true;
            }

        }
        return false;
    }


}


package com.southernbox.editdeletelayout.widget;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.southernbox.editdeletelayout.adapter.EditDeleteAdapter;

/**
 * Created by SouthernBox on 2016/10/27 0027.
 * 侧滑删除控件
 */

public class EditDeleteLayout extends FrameLayout {

    private ViewDragHelper mDragHelper;
    private View mContentView;
    private View mLeftView;
    private View mRightView;
    private int mWidth;
    private int mHeight;
    private int mLeftWidth;
    private int mRightWidth;

    public EditDeleteLayout(Context context) {
        this(context, null);
    }

    public EditDeleteLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditDeleteLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        //限定移动范围
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == mContentView) {
                if (left < -mRightWidth) {
                    left = -mRightWidth;
                } else if (left > mLeftWidth) {
                    left = mLeftWidth;
                }
            } else if (child == mRightView) {
                if (left < mWidth - mRightWidth) {
                    left = mWidth - mRightWidth;
                } else if (left > mWidth) {
                    left = mWidth;
                }
            } else if (child == mLeftView) {
                if (left < mWidth - mRightWidth) {
                    left = mWidth - mRightWidth;
                } else if (left > -mLeftWidth) {
                    left = 0 - mLeftWidth;
                }
            }
            return left;
        }

        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (changedView == mContentView) {
                mRightView.offsetLeftAndRight(dx);
                mLeftView.offsetLeftAndRight(dx);
            } else if (changedView == mRightView) {
                mContentView.offsetLeftAndRight(dx);
                mLeftView.offsetLeftAndRight(dx);
            } else if (changedView == mLeftView) {
                mContentView.offsetLeftAndRight(dx);
                mRightView.offsetLeftAndRight(dx);
            }
            invalidate();
        }
    };

    public interface OnStateChangeListener {

        void onPreExecuted(EditDeleteLayout layout);

        void onLeftOpen(EditDeleteLayout layout);

        void onRightOpen(EditDeleteLayout layout);

        void onClose(EditDeleteLayout layout);

    }

    private OnStateChangeListener mOnStateChangeListener;

    public void setOnDragStateChangeListener(OnStateChangeListener onStateChangeListener) {
        mOnStateChangeListener = onStateChangeListener;
        mOnStateChangeListener.onPreExecuted(this);
    }

    public void openRight() {
        if (mOnStateChangeListener != null) {
            mOnStateChangeListener.onRightOpen(this);
        }
        mDragHelper.smoothSlideViewTo(mContentView, -mRightWidth, 0);
        invalidate();
    }

    public void openLeft() {
        if (mOnStateChangeListener != null) {
            mOnStateChangeListener.onLeftOpen(this);
        }
        mDragHelper.smoothSlideViewTo(mContentView, mLeftWidth, 0);
        invalidate();
    }

    public void close() {
        if (mOnStateChangeListener != null) {
            mOnStateChangeListener.onClose(this);
        }
        mDragHelper.smoothSlideViewTo(mContentView, 0, 0);
        invalidate();
    }

    private void init() {
        mDragHelper = ViewDragHelper.create(this, mCallback);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //判断是否为编辑模式,摆放每个子View的位置
        if (EditDeleteAdapter.isEdit) {
            mContentView.layout(mLeftWidth, 0, mLeftWidth + mWidth, mHeight);
            mRightView.layout(mWidth + mLeftWidth, 0, mRightWidth + mWidth + mLeftWidth, mHeight);
            mLeftView.layout(0, 0, mLeftWidth, mHeight);
        } else {
            mContentView.layout(0, 0, mWidth, mHeight);
            mRightView.layout(mWidth, 0, mRightWidth + mWidth, mHeight);
            mLeftView.layout(-mLeftWidth, 0, 0, mHeight);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mLeftView = getChildAt(0);
        mContentView = getChildAt(1);
        mRightView = getChildAt(2);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mRightWidth = mRightView.getMeasuredWidth();
        mLeftWidth = mLeftView.getMeasuredWidth();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

}
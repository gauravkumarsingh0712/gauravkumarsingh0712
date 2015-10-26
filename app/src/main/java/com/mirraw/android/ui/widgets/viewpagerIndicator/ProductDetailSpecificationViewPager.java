package com.mirraw.android.ui.widgets.viewpagerIndicator;

import android.content.Context;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by varun on 10/8/15.
 */
public class ProductDetailSpecificationViewPager extends ViewPager {

    private Boolean mAnimStarted = false;

    public ProductDetailSpecificationViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProductDetailSpecificationViewPager(Context context) {
        super(context);
    }

    private View mCurrentView;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        /*if (mCurrentView == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        int height = 0;

        mCurrentView.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

        int h = mCurrentView.getMeasuredHeight();
        if (h > height) {
            height = h;
        }

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);*/

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = 0;
        if (getAdapter() != null) {
            View child = ((FragmentPagerAdapter) getAdapter()).getItem(getCurrentItem()).getView();
            if (child != null) {
                child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                height = child.getMeasuredHeight();
            }

            // Not the best place to put this animation, but it works pretty good.
            int newHeight = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            if (getLayoutParams().height != 0) {
                if (heightMeasureSpec != newHeight && !mAnimStarted) {
                    final int targetHeight = height;
                    final int currentHeight = getLayoutParams().height;
                    final int heightChange = targetHeight - currentHeight;

                    Animation a = new Animation() {
                        @Override
                        protected void applyTransformation(float interpolatedTime, Transformation t) {
                            if (interpolatedTime >= 1) {
                                getLayoutParams().height = targetHeight;
                            } else {
                                int stepHeight = (int) (heightChange * interpolatedTime);
                                getLayoutParams().height = currentHeight + stepHeight;
                            }
                            requestLayout();
                        }

                        @Override
                        public boolean willChangeBounds() {
                            return true;
                        }
                    };

                    a.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            mAnimStarted = true;
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            mAnimStarted = false;
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });

                    a.setDuration(300);
                    startAnimation(a);
                    mAnimStarted = true;
                }
            } else {
                heightMeasureSpec = newHeight;
            }

            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }


    }

    public void measureCurrentView(View currentView) {
        mCurrentView = currentView;
        requestLayout();
    }

    public int measureFragment(View view) {
        if (view == null) {
            return 0;
        }

        view.measure(0, 0);
        return view.getMeasuredHeight();
    }

}

package com.mirraw.android.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by varun on 27/8/15.
 */
public class CustomNestedScrollView extends ScrollView {

    private float xDistance, yDistance, lastX, lastY;

    public CustomNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    /*@Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev) && mGestureDetector.onTouchEvent(ev);
    }*/

    int lastEvent = -1;

    boolean isLastEventIntercepted = false;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        /*switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                lastX = ev.getX();
                lastY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();
                xDistance += Math.abs(curX - lastX);
                yDistance += Math.abs(curY - lastY);
                lastX = curX;
                lastY = curY;
                if(xDistance > yDistance)
                    return false;
        }
        return super.onInterceptTouchEvent(ev);*/


        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                lastX = ev.getX();
                lastY = ev.getY();


                break;

            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();
                xDistance += Math.abs(curX - lastX);
                yDistance += Math.abs(curY - lastY);
                lastX = curX;
                lastY = curY;

                if (isLastEventIntercepted && lastEvent == MotionEvent.ACTION_MOVE) {
                    return false;
                }

                if (xDistance > yDistance) {

                    isLastEventIntercepted = true;
                    lastEvent = MotionEvent.ACTION_MOVE;
                    return false;
                }
        }

        lastEvent = ev.getAction();

        isLastEventIntercepted = false;
        return super.onInterceptTouchEvent(ev);
    }

    class YScrollDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//            return super.onScroll(e1, e2, distanceX, distanceY);
            return Math.abs(distanceY) > Math.abs(distanceX);
        }
    }
}

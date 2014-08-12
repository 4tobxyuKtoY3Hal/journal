package com.example.journal.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class CustomViewPager extends ViewPager {
    private boolean paging = true;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {

        int width = ScaleImageView.mWidth;
        float x = ScaleImageView.getTranslateX();
        int w = ScaleImageView.mW;

        if(x < 0 && (x + w)!=width)
           return false;

        if (paging) {
            return super.onInterceptTouchEvent(e);
        }

        return false;
    }

    public void setPaging(boolean p){ paging = p; }

}
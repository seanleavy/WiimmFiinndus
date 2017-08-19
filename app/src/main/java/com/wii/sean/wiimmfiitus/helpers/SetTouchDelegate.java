package com.wii.sean.wiimmfiitus.helpers;

import android.graphics.Rect;
import android.view.TouchDelegate;
import android.view.View;

public class SetTouchDelegate {
    public static void expandTouchArea(final View bigView, final View smallView, final int extraPadding) {
        bigView.post(new Runnable() {
            @Override
            public void run() {
                Rect rect = new Rect();
                smallView.getHitRect(rect);
                rect.top -= extraPadding;
                rect.left -= extraPadding;
                rect.right += extraPadding;
                rect.bottom += extraPadding;
                bigView.setTouchDelegate(new TouchDelegate(rect, smallView));
            }
        });
    }
}

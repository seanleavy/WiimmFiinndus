package com.wii.sean.wiimmfiitus.helpers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.wii.sean.wiimmfiitus.R;

//create a build pattern
public class SnackBarHelper {
//todo change this to a builder pattern
    public static void showSnackBar(Context context, View v, String resource, int length, Drawable d, final View... args) {
        Snackbar snackbar = Snackbar.make(v, resource, length);
        snackbar.setActionTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        snackbar.show();
        View snackBarView = snackbar.getView();
        if(d != null) {
            snackBarView.setBackground(d);
        }
        else
            snackBarView.setBackgroundColor(ContextCompat.getColor(context, R.color.nintendo_red_dark));
        TextView snackText = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        snackText.setGravity(Gravity.CENTER_HORIZONTAL);
        snackText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        SnackBarHelper.switchVisibiltiy(View.INVISIBLE, args);

        snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                SnackBarHelper.switchVisibiltiy(View.VISIBLE, args);
            }
        });
    }

    private static void switchVisibiltiy(int visibility, View... args) {
        for(int i = 0; i < args.length; i++) {
            args[i].setVisibility(visibility);
        }
    }
}

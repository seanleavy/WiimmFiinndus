package com.wii.sean.wiimmfiitus.helpers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

//create a build pattern
public class SnackBarHelper {

    public static void showSnackBar(Context context, View v, String resource, int length, Drawable d) {
        Snackbar snackbar = Snackbar.make(v, resource, length);
        snackbar.setActionTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        snackbar.show();
        View snackBarView = snackbar.getView();
        if(d != null) {
            snackBarView.setBackground(d);
        }
        TextView snackText = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        snackText.setGravity(Gravity.CENTER_HORIZONTAL);
        snackText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
    }
}

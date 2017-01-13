package com.wii.sean.wiimmfiitus.customViews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.wii.sean.wiimmfiitus.helpers.NintendoFonts;

public class NintendoTextview extends TextView {

    public NintendoTextview(Context context) {
        super(context);
        setTypeface(NintendoFonts.getInstanceOfNintender(context).getTypeface());
    }

    public NintendoTextview(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(NintendoFonts.getInstanceOfNintender(context).getTypeface());
    }

    public NintendoTextview(Context context, AttributeSet attrs,
                                  int defStyle) {
        super(context, attrs, defStyle);
        setTypeface(NintendoFonts.getInstanceOfNintender(context).getTypeface());
    }
}

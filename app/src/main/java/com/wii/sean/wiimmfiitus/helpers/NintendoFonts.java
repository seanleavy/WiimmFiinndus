package com.wii.sean.wiimmfiitus.helpers;


import android.content.Context;
import android.graphics.Typeface;

import com.wii.sean.wiimmfiitus.friendSearch.Constants.Fonts;

public class NintendoFonts {

    public static NintendoFonts instanceOfNintender;
    public static Typeface typeface;

    public static NintendoFonts getInstanceOfNintender(Context context) {
        synchronized (NintendoFonts.class) {
            if(instanceOfNintender == null) {
                instanceOfNintender = new NintendoFonts();
                typeface = Typeface.createFromAsset(context.getResources().getAssets(), Fonts.nintenderTTF);
            }
            return instanceOfNintender;
        }
    }

    public Typeface getTypeface() {
        return typeface;
    }
}

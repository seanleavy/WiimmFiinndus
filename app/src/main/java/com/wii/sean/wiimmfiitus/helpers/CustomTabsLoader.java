package com.wii.sean.wiimmfiitus.helpers;

import android.content.Context;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.wii.sean.wiimmfiitus.Constants.UrlConstants;
import com.wii.sean.wiimmfiitus.R;

public class CustomTabsLoader {

    public static void loadWebsite(String url, Context context) {
        Toast.makeText(context, R.string.pressed_wiimfii, Toast.LENGTH_SHORT).show();
        Uri uri = Uri.parse(UrlConstants.WiimFiiUrl);
        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
        intentBuilder.setToolbarColor(ContextCompat.getColor(context, R.color.nintendo_red_dark));
        intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(context, R.color.nintendo_red));
        CustomTabsIntent customTabsIntent = intentBuilder.build();
        customTabsIntent.launchUrl(context, uri);
    }
}

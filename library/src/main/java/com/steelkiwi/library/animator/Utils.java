package com.steelkiwi.library.animator;

import android.view.View;

class Utils {

    static float sin(double degree) {
        return (float) Math.sin(Math.toRadians(degree));
    }

    static float cos(double degree) {
        return (float) Math.cos(Math.toRadians(degree));
    }

    static float aSin(double value) {
        return (float) Math.toDegrees(Math.asin(value));
    }

    static float aCos(double value) {
        return (float) Math.toDegrees(Math.acos(value));
    }

    static float centerX(View view) {
        return view.getX() + view.getWidth() / 2;
    }

    static float centerY(View view) {
        return view.getY() + view.getHeight() / 2;
    }

}

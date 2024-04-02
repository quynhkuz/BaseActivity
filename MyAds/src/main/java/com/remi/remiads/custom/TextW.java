package com.remi.remiads.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TextW extends androidx.appcompat.widget.AppCompatTextView {


    public TextW(@NonNull Context context) {
        super(context);
    }

    public TextW(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TextW(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setupText(int weight, float perSize) {
        int w = Math.min(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            setTypeface(Typeface.create(Typeface.SANS_SERIF, weight, false));
        else
            setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
        setTextSize(TypedValue.COMPLEX_UNIT_PX, w * perSize / 100);
    }

    public void setTypeFace(String font) {
        setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/" + font));
    }
}

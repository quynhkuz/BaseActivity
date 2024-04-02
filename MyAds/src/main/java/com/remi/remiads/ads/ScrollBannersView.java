package com.remi.remiads.ads;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.remi.remiads.BuildConfig;
import com.remi.remiads.utils.RmSave;

public class ScrollBannersView extends LinearLayout {

    private AdView adView;

    public ScrollBannersView(Context context) {
        super(context);
        init();
    }

    public ScrollBannersView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScrollBannersView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (RmSave.getPay(getContext()))
            return;
        int w = getResources().getDisplayMetrics().widthPixels;
        int pa = w / 45;
        int width = w - pa * 2;
        float density = getResources().getDisplayMetrics().density;
        int adWidth = (int) (width / density);
        AdSize adSize = AdSize.getCurrentOrientationInlineAdaptiveBannerAdSize(getContext(), adWidth);
        if (adSize.getHeight() > 0)
            setMinimumHeight((int) (adSize.getHeight()*density));
        adView = new AdView(getContext());
        LayoutParams p = new LayoutParams(-1, -2);
        p.setMargins(pa, pa, pa, pa);
        addView(adView, p);

        adView.setAdUnitId(BuildConfig.banner_scroll);
        adView.setAdSize(adSize);
        adView.loadAd(new AdRequest.Builder().build());
    }

    public void onResume() {
        if (adView != null)
            adView.resume();
    }

    public void onPause() {
        if (adView != null)
            adView.pause();
    }

    public void onDestroy() {
        if (adView != null)
            adView.destroy();
    }
}
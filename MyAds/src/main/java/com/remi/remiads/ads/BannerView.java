package com.remi.remiads.ads;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.remi.remiads.BuildConfig;
import com.remi.remiads.utils.RmSave;

public class BannerView extends LinearLayout {

    private AdView adView;

    public BannerView(Context context) {
        super(context);
        init();
    }

    public BannerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BannerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (RmSave.getPay(getContext())) {
            setVisibility(GONE);
            return;
        }
        int w = getResources().getDisplayMetrics().widthPixels;
        int pa = w / 150;
        float density = getResources().getDisplayMetrics().density;
        int adWidth = (int) (w / density);
        AdSize adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(getContext(), adWidth);
        if (adSize.getHeight() > 0)
            setMinimumHeight((int) (adSize.getHeight() * density));
        adView = new AdView(getContext());
        LayoutParams p = new LayoutParams(-1, -2);
        p.setMargins(0, pa, 0, pa);
        addView(adView, p);

        adView.setAdUnitId(BuildConfig.banner);
        adView.setAdSize(adSize);
        adView.loadAd(new AdRequest.Builder().build());
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                setVisibility(GONE);
            }
        });
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

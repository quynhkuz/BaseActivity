package com.remi.remiads.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.remi.remiads.R;
import com.remi.remiads.custom.TextW;
import com.remi.remiads.nativenew.ViewNative;
import com.remi.remiads.utils.RmSave;
import com.remi.remiads.utils.RmUtils;

public class DialogCheckSub extends Dialog {

    public DialogCheckSub(@NonNull Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getWindow() != null)
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int w = getContext().getResources().getDisplayMetrics().widthPixels;
        LinearLayout lAll = new LinearLayout(getContext());
        lAll.setGravity(Gravity.CENTER);
        LinearLayout ll = new LinearLayout(getContext());
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setGravity(Gravity.CENTER_HORIZONTAL);
        ll.setBackground(bgLayout(Color.parseColor("#f9ffffff"), w / 20));
        lAll.addView(ll, w * 8 / 10, -2);
        setContentView(lAll);

        TextW tvTitle = new TextW(getContext());
        tvTitle.setTextColor(Color.parseColor("#121212"));
        tvTitle.setupText(600, 5.7f);
        tvTitle.setText(R.string.title_dialog_sub);
        tvTitle.setGravity(Gravity.CENTER_HORIZONTAL);
        tvTitle.setPadding(0, w / 15, 0, w / 40);
        ll.addView(tvTitle, -2, -2);

        TextW tvContent = new TextW(getContext());
        tvContent.setTextColor(Color.parseColor("#444444"));
        tvContent.setupText(400, 4f);
        tvContent.setText(R.string.content_dialog_sub);
        tvContent.setGravity(Gravity.CENTER_HORIZONTAL);
        tvContent.setPadding(w / 50, 0, w / 50, w / 14);
        ll.addView(tvContent, -2, -2);

        addDivider(ll);

        TextW tvRemove = new TextW(getContext());
        tvRemove.setupText(600, 4.5f);
        tvRemove.setTextColor(Color.parseColor("#0B71ED"));
        tvRemove.setText(R.string.manager_sub);
        tvRemove.setPadding(0, w / 30, 0, w / 30);
        tvRemove.setGravity(Gravity.CENTER_HORIZONTAL);
        ll.addView(tvRemove, -1, -2);

        addDivider(ll);

        TextW tvCancel = new TextW(getContext());
        tvCancel.setupText(400, 4.5f);
        tvCancel.setTextColor(Color.parseColor("#0B71ED"));
        tvCancel.setText(R.string.cancel);
        tvCancel.setPadding(0, w / 30, 0, w / 30);
        tvCancel.setGravity(Gravity.CENTER_HORIZONTAL);
        ll.addView(tvCancel, -1, -2);

        tvCancel.setOnClickListener(v -> cancel());
        tvRemove.setOnClickListener(v -> {
            cancel();
            RmUtils.revokeSub(getContext(), RmSave.getSubType(getContext()), RmSave.getSubToken(getContext()));
        });
    }

    private void addDivider(LinearLayout ll) {
        View v = new ViewNative(getContext());
        v.setBackgroundColor(Color.parseColor("#aaaaaa"));
        ll.addView(v, -1, 1);
    }

    private GradientDrawable bgLayout(int color, int size) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(size / 2f);
        drawable.setColor(color);
        return drawable;
    }
}

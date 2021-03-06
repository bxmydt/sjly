package com.zpj.shouji.market.ui.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.lihang.ShadowLayout;

public class SquareCardView extends ShadowLayout {

    public SquareCardView(@NonNull Context context) {
        super(context);
    }

    public SquareCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, heightMeasureSpec);
    }
}

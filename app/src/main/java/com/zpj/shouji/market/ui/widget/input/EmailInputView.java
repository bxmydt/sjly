package com.zpj.shouji.market.ui.widget.input;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import com.rengwuxian.materialedittext.validation.RegexpValidator;
import com.zpj.shouji.market.R;

/**
 * @author CuiZhen
 * @date 2019/5/15
 * GitHub: https://github.com/goweii
 */
public class EmailInputView extends InputView {

    private ImageView mIvAccountIcon;
    private ImageView mIvDeleteIcon;

    public EmailInputView(Context context) {
        super(context);
    }

    public EmailInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmailInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initViews(AttributeSet attrs) {
        super.initViews(attrs);
        getEditText().setHint("请输入注册邮箱");
        changeFocusMode(false);
    }

    @Override
    protected ImageView[] getLeftIcons() {
        mIvAccountIcon = new ImageView(getContext());
        mIvAccountIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mIvAccountIcon.setImageResource(R.drawable.ic_email_black_24dp);
        return new ImageView[]{mIvAccountIcon};
    }

    @Override
    protected ImageView[] getRightIcons() {
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getContext().getResources().getDisplayMetrics());
        mIvDeleteIcon = new ImageView(getContext());
        mIvDeleteIcon.setVisibility(INVISIBLE);
        mIvDeleteIcon.setPadding(padding, padding, padding, padding);
        mIvDeleteIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mIvDeleteIcon.setImageResource(R.drawable.ic_delete);
        mIvDeleteIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getEditText().setText("");
            }
        });
        return new ImageView[]{mIvDeleteIcon};
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        super.onFocusChange(v, hasFocus);
        changeFocusMode(hasFocus);
    }

    private void changeFocusMode(boolean focus) {
        if (focus) {
            if (isEmpty()) {
                mIvDeleteIcon.setVisibility(INVISIBLE);
            } else {
                mIvDeleteIcon.setVisibility(VISIBLE);
            }
            mIvAccountIcon.setColorFilter(mViewColorFocus);
        } else {
            mIvDeleteIcon.setVisibility(INVISIBLE);
            mIvAccountIcon.setColorFilter(mViewColorNormal);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        super.afterTextChanged(s);
        if (isEmpty()) {
            mIvDeleteIcon.setVisibility(INVISIBLE);
        } else {
            mIvDeleteIcon.setVisibility(VISIBLE);
        }
    }
}

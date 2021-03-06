package com.zpj.shouji.market.ui.widget.recommend;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.recyclerview.IEasy;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.CollectionInfo;
import com.zpj.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class RecommendCard<T> extends FrameLayout
        implements IEasy.OnBindViewHolderListener<T>,
        IEasy.OnItemClickListener<T> {

    protected final List<T> list = new ArrayList<>();

    protected final Context context;
    protected final TextView tvTitle;
    protected final TextView tvMore;
    protected final EasyRecyclerView<T> recyclerView;

//    private String title;

//    private IEasy.OnItemClickListener<T> onItemClickListener;

    public RecommendCard(Context context) {
        this(context, null);
    }

    public RecommendCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecommendCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RecommendCard);
        String title = typedArray.getString(R.styleable.RecommendCard_header_title);
        typedArray.recycle();

//        setCardElevation(8);
//        setRadius(ScreenUtils.dp2pxInt(context, 12));
        LayoutInflater.from(context).inflate(R.layout.layout_recommend_card, this, true);
        tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(title);
        tvMore = findViewById(R.id.tv_more);
        tvMore.setOnClickListener(this::onMoreClicked);
        recyclerView = new EasyRecyclerView<>(findViewById(R.id.recycler_view));
        recyclerView.setData(list)
                .setItemRes(getItemRes())
                .setLayoutManager(new GridLayoutManager(context, 4))
                .onBindViewHolder(this)
                .onItemClick(this);
        buildRecyclerView(recyclerView);
        recyclerView.build();
    }

    protected void buildRecyclerView(EasyRecyclerView<T> recyclerView) {

    }

//    public void setOnItemClickListener(IEasy.OnItemClickListener<T> onItemClickListener) {
//        this.onItemClickListener = onItemClickListener;
//    }

//    @Override
//    public void onClick(EasyViewHolder holder, View view, T data) {
//        if (this.onItemClickListener != null) {
//            onItemClickListener.onClick(holder, view, data);
//        }
//    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void setData(List<T> data) {
        list.clear();
        list.addAll(data);
        recyclerView.notifyDataSetChanged();
    }

    @LayoutRes
    public abstract int getItemRes();

    public abstract void onMoreClicked(View v);
}

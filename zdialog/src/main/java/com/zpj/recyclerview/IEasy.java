package com.zpj.recyclerview;

import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public interface IEasy {

//    interface OnCreateViewHolderCallback<T> {
//        EasyViewHolder onCreateViewHolder(List<T> list, View itemView, int position);
//    }

    interface OnItemClickCallback {
        boolean shouldIgnoreClick(View view);
    }

    interface OnClickListener<T> {
        void onClick(EasyViewHolder holder, View view, T data);
    }


    interface OnBindViewHolderCallback<T>{
        void onBindViewHolder(EasyViewHolder holder, List<T> list, int position, List<Object> payloads);
    }

    interface OnCreateViewHolderCallback<T>{
        void onCreateViewHolder(ViewGroup parent, View itemView, int viewType);
    }

    interface OnBindHeaderListener {
        void onBindHeader(EasyViewHolder holder);
    }

    interface OnCreateFooterCallback{
        void onCreateFooterView(View view);
    }

    interface OnLoadMoreListener {
        boolean onLoadMore(EasyAdapter.Enabled enabled, int currentPage);
    }
}

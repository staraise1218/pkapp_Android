package com.zhongyi.mind.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.zhongyi.mind.R;
import com.zhongyi.mind.data.Artical;
import com.zhongyi.mind.data.DynamicBean;
import com.zhongyi.mind.holder.DynamicHolder;

public class DynamicAdapter extends RecyclerArrayAdapter<Artical> {
    public DynamicAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hot_topic, null);
        return new DynamicHolder(itemView);
    }
}

package com.exasmart.androidtvexample;

import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter;

public class DetailsDescriptionPresenter extends AbstractDetailsDescriptionPresenter {
    @Override
    protected void onBindDescription(ViewHolder viewHolder, Object item) {
        Channel channel = (Channel) item;

        if (channel != null) {
            viewHolder.getTitle().setText(channel.getTitle());
            viewHolder.getSubtitle().setText(channel.getStudio());
            viewHolder.getBody().setText(channel.getDescription());
        }
    }
}

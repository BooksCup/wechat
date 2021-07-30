package com.bc.wechat.moments.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import byc.imagewatcher.ImageWatcher;

public class GlideSimpleLoader implements ImageWatcher.Loader {
    @Override
    public void load(Context context, Uri uri, final ImageWatcher.LoadCallback lc) {
        Glide.with(context).load(uri)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        lc.onResourceReady(resource);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        lc.onLoadFailed(errorDrawable);
                    }

                    @Override
                    public void onLoadStarted(@Nullable Drawable placeholder) {
                        lc.onLoadStarted(placeholder);
                    }
                });
    }
}

package com.movieous.media.mvp.model.select;

import android.content.Context;
import iknow.android.utils.callback.SimpleCallback;

public interface ILoader {
    void load(final Context mContext, final SimpleCallback listener);
}

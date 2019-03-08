package com.movieous.media.mvp.model.select;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import iknow.android.utils.callback.SimpleCallback;

public class MediaCursorLoader implements LoaderManager.LoaderCallbacks<Cursor>, ILoader {

    private Context mContext;
    private SimpleCallback mSimpleCallback;
    private MediaType mMediaType = MediaType.VIDEO;

    public enum MediaType {VIDEO, AUDIO}

    public MediaCursorLoader() {
    }

    public MediaCursorLoader(MediaType type) {
        mMediaType = type;
    }

    @Override
    public void load(final Context context, final SimpleCallback listener) {
        mContext = context;
        mSimpleCallback = listener;
        ((FragmentActivity) context).getSupportLoaderManager().initLoader(1, null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Uri uri = mMediaType == MediaType.VIDEO ? MediaStore.Video.Media.EXTERNAL_CONTENT_URI : MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        return new CursorLoader(mContext, uri, null, null, null, MediaStore.Video.Media.DATE_MODIFIED + " desc");
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (mSimpleCallback != null && cursor != null) {
            mSimpleCallback.success(cursor);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    }
}

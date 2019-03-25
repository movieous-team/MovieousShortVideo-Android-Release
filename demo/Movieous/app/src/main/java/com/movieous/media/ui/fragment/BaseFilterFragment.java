package com.movieous.media.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import com.movieous.media.R;
import com.movieous.media.base.BaseFragment;
import com.movieous.media.mvp.contract.FilterChangedListener;
import com.movieous.media.mvp.model.entity.BeautyParamEnum;
import com.movieous.media.mvp.model.entity.UFilter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 贴纸选择页面
 */
public class BaseFilterFragment extends BaseFragment implements FilterChangedListener {
    protected static final String TAG = "BaseFilterFragment";

    protected Activity mActivity;
    protected LayoutInflater mInflater;
    protected FilterChangedListener mFilterChangedListener;
    protected HashMap<String, ArrayList<UFilter>> mStickerFilterList = new HashMap<>();

    public void setOnFilterChangedListener(FilterChangedListener listener) {
        mFilterChangedListener = listener;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_sticker_filter;
    }

    @Override
    public void lazyLoad() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
        mInflater = LayoutInflater.from(mActivity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    @Override
    public void initView() {
    }

    @Override
    public void onMusicFilterTime(long time) {
        mFilterChangedListener.onMusicFilterTime(time);
    }

    @Override
    public void onBeautyValueChanged(float value, @NotNull BeautyParamEnum beautyType) {
        mFilterChangedListener.onBeautyValueChanged(value, beautyType);
    }

    @Override
    public void onBeautyFilterChanged(@NotNull UFilter filter) {
        mFilterChangedListener.onBeautyFilterChanged(filter);
    }

    @Override
    public void onMagicFilterChanged(@NotNull UFilter filter) {
        if (mFilterChangedListener != null) {
            mFilterChangedListener.onMagicFilterChanged(filter);
        }
    }

    @Override
    public void onRemoveLastFilter() {
        if (mFilterChangedListener != null) {
            mFilterChangedListener.onRemoveLastFilter();
        }
    }

    @Override
    public void onClearFilter() {
        if (mFilterChangedListener != null) {
            mFilterChangedListener.onClearFilter();
        }
    }
}

package com.movieous.media.ui.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import butterknife.BindView;
import com.faceunity.entity.Filter;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.movieous.media.R;
import com.movieous.media.mvp.model.entity.BeautyParamEnum;
import com.movieous.media.mvp.model.entity.TabEntity;
import com.movieous.media.ui.adapter.BeautyFilterAdapter;

import java.util.ArrayList;

/**
 * 美颜设置页面
 */
public class BeautyFilterFragment extends BaseFilterFragment implements SeekBar.OnSeekBarChangeListener, OnTabSelectListener {
    public static final boolean SDK_BEAUTY = false;
    public static final int SHOW_TYPE_BEAUTY = 1;
    public static final int SHOW_TYPE_FILTER = 0;
    public static float sBlurLevel = 0.7f;//磨皮
    public static float sEyeEnlarging = 0.4f;//大眼
    public static float sCheekThinning = 0.4f;//瘦脸

    private int mTitleButtonIndex = SHOW_TYPE_FILTER;

    @BindView(R.id.tv_title)
    TextView mTabTitle;
    @BindView((R.id.layout_content))
    LinearLayout mLayoutContent;
    @BindView(R.id.tab_title)
    CommonTabLayout mTabLayout;

    private String[] mTabTitles;
    private LinearLayout mLayoutFilter;
    private LinearLayout mLayoutBeauty;
    private BeautyFilterAdapter mBeautyFilterAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_beauty_filter;
    }

    @Override
    public void initView() {
        mTabTitles = new String[]{getString(R.string.btn_preview_filter), getString(R.string.btn_preview_beauty)};
        ArrayList<CustomTabEntity> tabEntities = new ArrayList<>();
        for (int i = 0; i < mTabTitles.length; i++) {
            tabEntities.add(new TabEntity(mTabTitles[i], 0, 0));
        }
        mTabLayout.setTabData(tabEntities);
        mTabLayout.setOnTabSelectListener(this);
        showContentLayout(mTitleButtonIndex);
    }

    @Override
    public void onTabSelect(int position) {
        showContentLayout(position);
    }

    @Override
    public void onTabReselect(int position) {
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int viewId = seekBar.getId();
        BeautyParamEnum beautyType = (viewId == R.id.value_progress_blur) ? BeautyParamEnum.FACE_BLUR : (viewId == R.id.value_progress_eye) ? BeautyParamEnum.EYE_ENLARGE : BeautyParamEnum.CHEEK_THINNING;
        onBeautyValueChanged(1.0f * progress / seekBar.getMax(), beautyType);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    private void showContentLayout(int index) {
        mTitleButtonIndex = index;
        mTabTitle.setText(mTabTitles[index]);
        if (index == SHOW_TYPE_BEAUTY) {
            showFaceBeautyLayout();
        } else if (index == SHOW_TYPE_FILTER) {
            showBeautyFilterLayout();
        }
    }

    /**
     * 显示美颜视图布局
     */
    private void showFaceBeautyLayout() {
        if (mLayoutBeauty == null) {
            mLayoutBeauty = (LinearLayout) mInflater.inflate(R.layout.view_beauty, null);
            SeekBar blurSeekbar = mLayoutBeauty.findViewById(R.id.value_progress_blur);
            blurSeekbar.setOnSeekBarChangeListener(this);
            blurSeekbar.setProgress((int) (sBlurLevel * 100));
            SeekBar faceSeekbar = mLayoutBeauty.findViewById(R.id.value_progress_face);
            faceSeekbar.setOnSeekBarChangeListener(this);
            faceSeekbar.setProgress((int) (sCheekThinning * 100));
            SeekBar eyeSeekbar = mLayoutBeauty.findViewById(R.id.value_progress_eye);
            eyeSeekbar.setOnSeekBarChangeListener(this);
            eyeSeekbar.setProgress((int) (sEyeEnlarging * 100));
        }
        mLayoutContent.removeAllViews();
        mLayoutContent.addView(mLayoutBeauty);
    }

    /**
     * 显示滤镜布局
     */
    private void showBeautyFilterLayout() {
        if (mLayoutFilter == null) {
            mLayoutFilter = (LinearLayout) mInflater.inflate(R.layout.view_filter, null);
            RecyclerView recyclerView = mLayoutFilter.findViewById(R.id.beauty_filter_recyclerView);
            LinearLayoutManager mFilterLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(mFilterLayoutManager);
            mBeautyFilterAdapter = new BeautyFilterAdapter(mActivity, this);
            mBeautyFilterAdapter.setFilterType(Filter.FILTER_TYPE_BEAUTY_FILTER);
            recyclerView.setAdapter(mBeautyFilterAdapter);
        }
        mLayoutContent.removeAllViews();
        mLayoutContent.addView(mLayoutFilter);
    }

}
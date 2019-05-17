package com.movieous.media.ui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.BindView;
import butterknife.OnClick;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.hhl.gridpagersnaphelper.GridPagerSnapHelper;
import com.hhl.recyclerviewindicator.LinePageIndicator;
import com.movieous.media.Constants;
import com.movieous.media.R;
import com.movieous.media.api.vendor.fusdk.FuSDKManager;
import com.movieous.media.mvp.contract.MusicSelectedListener;
import com.movieous.media.mvp.contract.OnSeekBarChangeListener;
import com.movieous.media.mvp.model.entity.BeautyParamEnum;
import com.movieous.media.mvp.model.entity.TabEntity;
import com.movieous.media.mvp.model.entity.UFilter;
import com.movieous.media.ui.activity.PlaybackActivity;
import com.movieous.media.utils.ScreenUtils;
import com.movieous.media.view.*;
import io.inchtime.recyclerkit.RecyclerAdapter;
import io.inchtime.recyclerkit.RecyclerKit;
import kotlin.Unit;
import org.jetbrains.annotations.NotNull;
import video.movieous.engine.UMediaTrimTime;
import video.movieous.engine.UVideoFrameListener;
import video.movieous.engine.media.util.MediaUtil;
import video.movieous.shortvideo.UMediaUtil;
import video.movieous.shortvideo.USticker;
import video.movieous.shortvideo.UVideoEditManager;
import video.movieous.shortvideo.UVideoPlayListener;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.movieous.media.ExtensionsKt.showToast;

public class VideoEditFragment extends VideoEditPreviewFragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, UVideoPlayListener,
        FilterSeekView.OnDataChangedListener, MusicSelectedListener {
    private static final String TAG = "VideoEditFragment";

    @BindView(R.id.video_edit_recyclerView)
    RecyclerView mMainRecyclerView;
    @BindView(R.id.layout_bottom_video_edit)
    LinearLayout mBottomMenuLayout;
    @BindView(R.id.main_menu)
    ImageButton mMainMenu;
    @BindView(R.id.add_text)
    ImageButton mTextEffect;
    @BindView(R.id.face_stick)
    ImageButton mStickerEffect;
    @BindView(R.id.btn_select_music)
    ImageButton mAddMusic;
    @BindView(R.id.fragment_container)
    FrameLayout mFragmentContainer;

    private int mCurrentLayout;
    private String mVideoPath;
    private int mVideoWidth;
    private int mVideoHeight;
    private RecyclerAdapter mMainAdapter;
    private SparseArray<List<RecyclerAdapter.ViewModel>> mViewModels = new SparseArray<>();
    private Map<Integer, Double> mRecordSpeed;
    private UMediaTrimTime mTrimTime;
    // for magic filter
    private FilterSeekView mFilterSeekView;
    private boolean mIsLongClick;
    private int mTimeFxMode = FilterSeekView.TIMELINE_FX_MODE_NONE;
    // for make cover
    private FrameListView mFrameListViewCover;
    private List<Integer> mSelectedFrameIndex;
    // for face sticker
    private StickerFilterFragment mStickerFilterFragment;
    // music list
    private MusicSelectFragment mMusicSelectFragment;
    // text effect
    private USticker mTitleSticker;
    private USticker mTimeAddrSticker;

    public synchronized static VideoEditFragment getInstance(String videoPath) {
        VideoEditFragment fragment = new VideoEditFragment();
        MediaUtil.Metadata metadata = UMediaUtil.getMetadata(videoPath);
        fragment.mVideoPath = videoPath;
        boolean needRotation = metadata.rotation / 90 % 2 != 0;
        fragment.mVideoWidth = needRotation ? metadata.height : metadata.width;
        fragment.mVideoHeight = needRotation ? metadata.width : metadata.height;
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(mVideoPath)) {
            startPlayback();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        pausePlayback();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mProcessingDialog = null;
        mActivity = null;
        stopPlayback();
        mVideoEditManager.release();
        mVideoEditManager = null;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_video_edit;
    }

    @Override
    public void initView() {
        Log.d(TAG, "initView");
        setupRecyclerView();
    }

    @Override
    public void lazyLoad() {
        super.lazyLoad();
        initVideoEditManager();
    }

    @Override
    protected void initProcessingDialog() {
        super.initProcessingDialog();
        mProcessingDialog.setOnCancelListener(dialog -> {
            mVideoEditManager.cancelSave();
        });
    }

    @OnClick({R.id.pause_playback, R.id.preview, R.id.main_menu, R.id.add_text, R.id.face_stick, R.id.btn_select_music,
            R.id.fragment_back_button, R.id.fragment_next_button})
    @Override
    public void onClick(View v) {
        if (v == null) return;
        switch (v.getId()) {
            case R.id.preview:
                mPlayButton.setVisibility(mPlayButton.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
                break;
            case R.id.pause_playback:
                onClickTogglePlayback();
                break;
            case R.id.main_menu: // 主菜单
                showMainMenuModels();
                setMenuTitleColor(R.id.main_menu);
                showBottomMenu(true);
                break;
            case R.id.add_text: // 文字
                showMenuViewModels(R.layout.view_add_text);
                setMenuTitleColor(R.id.add_text);
                showBottomMenu(true);
                break;
            case R.id.face_stick: // 贴纸
                if (!isFilterVendorEnabled(true)) return;
                showStickerFilterFragment();
                setMenuTitleColor(R.id.face_stick);
                showBottomMenu(true);
                break;
            case R.id.btn_select_music: // 背景音乐
                showMusicFragment();
                setMenuTitleColor(R.id.btn_select_music);
                showBottomMenu(true);
                break;
            case R.id.menu_back_button: // 菜单返回
                showMainMenuModels();
                break;
            case R.id.fragment_back_button: // 界面退出
                mActivity.onBackPressed();
                break;
            case R.id.fragment_next_button: // 保存
                saveVideoFile();
                break;
            default:
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int viewId = seekBar.getId();
        switch (viewId) {
            case R.id.seekbar_origin_value:
                mVideoEditManager.setOriginVolume(progress / 100f);
                break;
            case R.id.seekbar_music_value:
                mVideoEditManager.setMusicVolume(progress / 100f);
                break;
            case R.id.value_progress_blur:
            case R.id.value_progress_face:
            case R.id.value_progress_eye:
                BeautyParamEnum beautyType = (viewId == R.id.value_progress_blur) ? BeautyParamEnum.FACE_BLUR : (viewId == R.id.value_progress_eye) ? BeautyParamEnum.EYE_ENLARGE : BeautyParamEnum.CHEEK_THINNING;
                onBeautyValueChanged(1.0f * progress / seekBar.getMax(), beautyType);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    // USaveListener
    @Override
    public void onVideoSaveSuccess(String destFile) {
        mActivity.runOnUiThread(() -> {
            Log.i(TAG, "save video file success, filePath: " + destFile);
            mProcessingDialog.dismiss();
            PlaybackActivity.start(mActivity, destFile);
        });
    }

    // UVideoPlayListener
    @Override
    public void onPositionChanged(int position) {
        if (mTrimTime != null) {
            if (position >= mTrimTime.endTimeMs) {
                mVideoEditManager.seekTo(mTrimTime.startTimeMs);
                return;
            }
        }
        mActivity.runOnUiThread(() -> {
            if (mCurrentLayout == R.layout.view_cover && mFrameListViewCover != null) {
                mFrameListViewCover.scrollToTime(position);
            } else if (isTimeRangeFilter()) {
                setPlayTime(position, getVideoDuration());
            }
            if (mFilterSdkManager != null) {
                mFilterSdkManager.changeMusicFilterTime(-1);
            }
        });
    }

    @Override
    public void onCompletion() {
        super.onCompletion();
    }

    // FilterSeekView listener
    @Override
    public void onFirstDataChange(float value) {
        int position = (int) (mVideoDuration * value / 100f);
        if (position < 0 || position > mVideoDuration - 10) {
            Log.d(TAG, "onFirstDataChange: position = " + position + ", value = " + value + ", duration = " + mVideoDuration);
            return;
        }
        pausePlayback();
        if (Math.abs(mLastSeekValue - position) > 500) {
            mVideoEditManager.seekTo(position);
            mLastSeekValue = position;
        }
    }

    @Override
    protected void setSeekViewStart(UFilter filterItem) {
        mFilterSeekView.addingFilter(filterItem);
    }

    @Override
    protected void setSeekViewEnd(long duration) {
        mActivity.runOnUiThread(() -> mFilterSeekView.endAddingFilter(duration));
    }

    @Override
    protected boolean isTimeRangeFilter() {
        return mCurrentLayout == R.layout.view_magic_filter;
    }

    // 底部一级菜单
    private void handleBottomMenu(RecyclerAdapter.ViewHolder viewHolder) {
        if (viewHolder.haveView(R.id.menu_back_button)) {
            viewHolder.findView(R.id.menu_back_button).setOnClickListener(this);
        }
        if (viewHolder.haveView(R.id.main_menu)) {
            viewHolder.findView(R.id.main_menu).setOnClickListener(this);
        }
        if (viewHolder.haveView(R.id.add_text)) {
            viewHolder.findView(R.id.add_text).setOnClickListener(this);
        }
        if (viewHolder.haveView(R.id.face_stick)) {
            viewHolder.findView(R.id.face_stick).setOnClickListener(this);
        }
        if (viewHolder.haveView(R.id.btn_select_music)) {
            viewHolder.findView(R.id.btn_select_music).setOnClickListener(this);
        }
    }

    // 初始化 UVideoEditManager
    private void initVideoEditManager() {
        Log.i(TAG, "media file: " + mVideoPath);
        mVideoEditManager = new UVideoEditManager()
                .init(mPreview, mVideoPath)
                .setAVOptions(mAVOptions)
                .setVideoFrameListener(this)
                .setVideoSaveListener(this);
    }

    // 设置 RecyclerView
    private void setupRecyclerView() {
        mMainAdapter = RecyclerKit.INSTANCE.adapter(mActivity, 1)
                .recyclerView(mMainRecyclerView)
                .withLinearLayout(LinearLayoutManager.VERTICAL, false)
                .modelViewBind((index, viewModel, viewHolder) -> {
                    onModelViewBind(viewModel, viewHolder);
                    return Unit.INSTANCE;
                })
                .emptyViewBind((emptyViewHolder -> Unit.INSTANCE))
                .build();
        mMainAdapter.setEmptyView(R.layout.recyclerkit_view_empty);
        showMainMenuModels();
    }

    // 底部一级菜单选中状态
    private void setMenuTitleColor(int viewId) {
        mMainMenu.setSelected(mMainMenu.getId() == viewId);
        mTextEffect.setSelected(mTextEffect.getId() == viewId);
        mStickerEffect.setSelected(mStickerEffect.getId() == viewId);
        mAddMusic.setSelected(mAddMusic.getId() == viewId);
    }

    // 显示主菜单
    private void showMainMenuModels() {
        showMenuViewModels(R.layout.view_main_menu);
        showBottomMenu(true);
        setMenuTitleColor(R.id.main_menu);
    }

    // 显示底部一级菜单，只在主菜单中显示，主菜单的二级菜单不显示底部菜单
    private void showBottomMenu(boolean isShow) {
        mBottomMenuLayout.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    // 主菜单
    private void showMenuViewModels(int layout) {
        List<RecyclerAdapter.ViewModel> viewModels = mViewModels.get(layout);
        if (viewModels == null) {
            viewModels = new ArrayList<>();
            viewModels.add(new RecyclerAdapter.ViewModel(layout, 1, RecyclerAdapter.ModelType.LEADING_TRAILING, 0, false));
            mViewModels.put(layout, viewModels);
        }
        mMainAdapter.setModels(viewModels);
        showBottomMenu(false);
        setFragmentShowing(false);
        mCurrentLayout = layout;
    }

    private void setFragmentShowing(boolean isShowing) {
        mFragmentContainer.setVisibility(isShowing ? View.VISIBLE : View.GONE);
        mMainRecyclerView.setVisibility(!isShowing ? View.VISIBLE : View.GONE);
    }

    // 当前功能区显示的内容
    private void onModelViewBind(RecyclerAdapter.ViewModel viewModel, RecyclerAdapter.ViewHolder viewHolder) {
        if (viewHolder.getView().getTag() != null) return;
        viewHolder.getView().setTag(1);
        handleBottomMenu(viewHolder);
        switch (viewModel.getLayout()) {
            case R.layout.view_main_menu:
                bindMainMenuView(viewHolder);
                break;
            case R.layout.view_audio_volume:
                bindAudioVolume(viewHolder);
                break;
            case R.layout.menu_view_beauty:
                bindBeautyAdjust(viewHolder);
                break;
            case R.layout.menu_view_filter:
                bindBeautyFilter(viewHolder);
                break;
            case R.layout.view_video_cut:
                bindVideoCutView(viewHolder);
                break;
            case R.layout.view_magic_filter:
                bindMagicFilter(viewHolder);
                break;
            case R.layout.view_cover:
                bindMakeCover(viewHolder);
                break;
            case R.layout.view_add_text:
                bindAddTextView(viewHolder);
                break;
        }
    }

    // 主菜单
    private void bindMainMenuView(RecyclerAdapter.ViewHolder viewHolder) {
        RecyclerView tableRecyclerView = viewHolder.findView(R.id.main_function_recyclerView);
        tableRecyclerView.hasFixedSize();
        RecyclerAdapter adapter = RecyclerKit.INSTANCE.adapter(mActivity, 2)
                .recyclerView(tableRecyclerView)
                .withGridLayout(GridLayoutManager.HORIZONTAL, false)
                .modelViewBind((pIndex, pViewModel, pViewHolder) -> {
                    bindMainMenuItem(pViewModel, pViewHolder);
                    return Unit.INSTANCE;
                })
                .modelViewClick((pIndex, pViewModel, view) -> {
                    onMainMenuClick(pViewModel);
                    return Unit.INSTANCE;
                })
                .build();
        List<TextIcon> items = getMainFunctionItems();
        List<RecyclerAdapter.ViewModel> models = new ArrayList<>();
        for (TextIcon item : items) {
            models.add(new RecyclerAdapter.ViewModel(R.layout.item_grid, 1, RecyclerAdapter.ModelType.LEADING, item, false));
        }
        adapter.setModels(models);
        //attachToRecyclerView
        GridPagerSnapHelper gridPagerSnapHelper = new GridPagerSnapHelper();
        gridPagerSnapHelper.setRow(2).setColumn(3);
        gridPagerSnapHelper.attachToRecyclerView(tableRecyclerView);
        //indicator
        LinePageIndicator indicator = viewHolder.findView(R.id.main_function_indicator);
        indicator.setRecyclerView(tableRecyclerView);
        //Note: pageColumn must be config
        indicator.setPageColumn(3);
    }

    // 主菜单项
    private void bindMainMenuItem(RecyclerAdapter.ViewModel viewModel, RecyclerAdapter.ViewHolder viewHolder) {
        ViewGroup.LayoutParams layoutParams = viewHolder.getView().getLayoutParams();
        layoutParams.width = ScreenUtils.INSTANCE.getScreenWidth(mActivity) / 3;
        TextIcon item = (TextIcon) viewModel.getValue();
        ImageView imageView = viewHolder.findView(R.id.icon);
        imageView.setImageResource(item.getId());
        TextView textView = viewHolder.findView(R.id.name);
        textView.setText(item.getName());
    }

    // 显示子菜单界面
    private void onMainMenuClick(RecyclerAdapter.ViewModel viewModel) {
        TextIcon item = (TextIcon) viewModel.getValue();
        int resId = 0;
        switch (item.getId()) {
            case R.drawable.sound_set: // 声音调节
                resId = R.layout.view_audio_volume;
                break;
            case R.drawable.face_beauty_set: // 美颜调节
                if (!isFilterVendorEnabled(true)) return;
                resId = R.layout.menu_view_beauty;
                break;
            case R.drawable.filter: // 美颜滤镜
                if (!isFilterVendorEnabled(true)) return;
                resId = R.layout.menu_view_filter;
                break;
            case R.drawable.cut: // 视频剪辑
                resId = R.layout.view_video_cut;
                break;
            case R.drawable.facial_effects: // 滤镜特效、时间特效
                if (!isFilterVendorEnabled(true)) return;
                resId = R.layout.view_magic_filter;
                break;
            case R.drawable.cover: // GIF 动图
                resId = R.layout.view_cover;
                break;
            case R.drawable.video_combination: // 视频组合
                resId = R.layout.coming_soon_menu_view;
                break;
            case R.drawable.video_mv_combine: // MV 效果
                resId = R.layout.coming_soon_menu_view;
                break;
            case R.drawable.video_subsection: // 画面裁剪
                resId = R.layout.coming_soon_menu_view;
                break;
            case R.drawable.video_splice: // 视频前后拼接
                resId = R.layout.coming_soon_menu_view;
                break;
            case R.drawable.video_title_tail: // 片头片尾
                resId = R.layout.coming_soon_menu_view;
                break;
            case R.drawable.speed_set: // 视频变速
                resId = R.layout.coming_soon_menu_view;
                break;
        }
        if (resId > 0) {
            showMenuViewModels(resId);
        }
    }

    // 设置菜单标题
    private void setMenuTitle(RecyclerAdapter.ViewHolder viewHolder, int resId) {
        if (viewHolder.haveView(R.id.menu_title)) {
            TextView textView = viewHolder.findView(R.id.menu_title);
            textView.setText(resId);
        }
    }

    // 声音调节
    private void bindAudioVolume(RecyclerAdapter.ViewHolder viewHolder) {
        setMenuTitle(viewHolder, R.string.audio_volume_title);
        viewHolder.findView(R.id.btn_mute).setOnClickListener(v -> {
            //mVideoEditManager.muteOriginAudio(true);
            ((SeekBar) viewHolder.findView(R.id.seekbar_origin_value)).setProgress(0);
        });
        ((SeekBar) viewHolder.findView(R.id.seekbar_origin_value)).setOnSeekBarChangeListener(this);
        ((SeekBar) viewHolder.findView(R.id.seekbar_music_value)).setOnSeekBarChangeListener(this);
        // TODO 音乐截取界面
        viewHolder.findView(R.id.btn_select_music).setOnClickListener(view -> showTodoToast());
    }

    // 美颜调节
    private void bindBeautyAdjust(RecyclerAdapter.ViewHolder viewHolder) {
        setMenuTitle(viewHolder, R.string.btn_preview_beauty);
        ((SeekBar) viewHolder.findView(R.id.value_progress_blur)).setOnSeekBarChangeListener(this);
        ((SeekBar) viewHolder.findView(R.id.value_progress_face)).setOnSeekBarChangeListener(this);
        ((SeekBar) viewHolder.findView(R.id.value_progress_eye)).setOnSeekBarChangeListener(this);
    }

    // 美颜滤镜
    private void bindBeautyFilter(RecyclerAdapter.ViewHolder viewHolder) {
        boolean isVendorFu = isFuFilterSDK();
        setMenuTitle(viewHolder, R.string.btn_preview_filter);
        RecyclerView recyclerView = viewHolder.findView(R.id.beauty_filter_recyclerView);
        RecyclerAdapter adapter = RecyclerKit.INSTANCE.adapter(mActivity, 1)
                .recyclerView(recyclerView)
                .withLinearLayout(LinearLayoutManager.HORIZONTAL, false)
                .modelViewBind((pIndex, pViewModel, pViewHolder) -> {
                    UFilter item = (UFilter) pViewModel.getValue();
                    if (isVendorFu) {
                        ((ImageView) pViewHolder.findView(R.id.icon)).setImageResource(item.getResId());
                        TextView textView = pViewHolder.findView(R.id.name);
                        textView.setVisibility(View.VISIBLE);
                        textView.setText(item.getDescription());
                    } else {
                        ((ImageView) pViewHolder.findView(R.id.icon)).setImageBitmap(item.getIcon());
                        TextView textView = pViewHolder.findView(R.id.name);
                        textView.setVisibility(View.VISIBLE);
                        textView.setText(item.getName());
                    }
                    return Unit.INSTANCE;
                })
                .modelViewClick((pIndex, pViewModel, view) -> {
                    onBeautyFilterChanged((UFilter) pViewModel.getValue());
                    return Unit.INSTANCE;
                })
                .build();
        List<UFilter> items = isVendorFu ? FuSDKManager.getFilterList() : null;
        List<RecyclerAdapter.ViewModel> models = new ArrayList<>();
        for (UFilter item : items) {
            models.add(new RecyclerAdapter.ViewModel(R.layout.item_filter_view, 1, RecyclerAdapter.ModelType.LEADING, item, false));
        }
        adapter.setModels(models);
    }

    // 截取
    private void bindVideoCutView(RecyclerAdapter.ViewHolder viewHolder) {
        setMenuTitle(viewHolder, R.string.cut_clip);
        TileView til = viewHolder.getView().findViewById(R.id.timeLineView);
        til.setVideoPath(mVideoPath, getVideoDuration());
        ((RangeSeekBar) viewHolder.getView().findViewById(R.id.timeLineBar)).addOnRangeSeekBarListener(new OnSeekBarChangeListener() {
            @Override
            public void onCreate(RangeSeekBar rangeSeekBar, int index, float value) {
                setSeekBarPosition(rangeSeekBar);
                setVideoDuration(viewHolder, mTrimTime);
            }

            @Override
            public void onSeek(RangeSeekBar rangeSeekBar, int index, float value) {
                onSeekThumbs(index, value);
                setVideoDuration(viewHolder, mTrimTime);
            }
        });
    }

    private void onSeekThumbs(int index, float value) {
        int mDuration = (int) (getVideoDuration() / 1000);
        switch (index) {
            case SeekBarHandler.LEFT: {
                mTrimTime.startTimeMs = (int) (((mDuration * value) / 100L) * 1000);
                mVideoEditManager.seekTo(mTrimTime.startTimeMs);
                break;
            }
            case SeekBarHandler.RIGHT: {
                mTrimTime.endTimeMs = (int) ((mDuration * value) / 100L) * 1000;
                break;
            }
        }
    }

    private void setSeekBarPosition(RangeSeekBar rangeSeekBar) {
        int maxDuration = Constants.DEFAULT_MAX_RECORD_DURATION / 1000;
        int videoDuration = (int) getVideoDuration() / 1000;
        int st = 0;
        int et;
        if (videoDuration >= maxDuration) {
            et = maxDuration;
            rangeSeekBar.setThumbValue(0, (st * 100) / videoDuration);
            rangeSeekBar.setThumbValue(1, (et * 100) / videoDuration);
        } else {
            et = videoDuration;
        }
        rangeSeekBar.initMaxWidth();
        mTrimTime = new UMediaTrimTime(st * 1000, et * 1000);
    }

    private void setVideoDuration(RecyclerAdapter.ViewHolder viewHolder, UMediaTrimTime trimTime) {
        mVideoEditManager.setTrimTime(trimTime);
        ((TextView) viewHolder.findView(R.id.tv_select_tip)).setText(String.format(getString(R.string.select_clip_tip), trimTime.getDuration() / 1000));
    }

    // 特效
    private void bindMagicFilter(RecyclerAdapter.ViewHolder viewHolder) {
        pausePlayback();
        TileView thumbList = viewHolder.findView(R.id.thumb_list);
        thumbList.setVideoPath(mVideoPath, mVideoDuration);

        CommonTabLayout tabLayout = viewHolder.findView(R.id.tab_title);
        String[] tabTitle = getResources().getStringArray(R.array.magic_filter);
        ArrayList<CustomTabEntity> tabEntities = new ArrayList<>();
        for (int i = 0; i < tabTitle.length - 1; i++) { // TODO 时间特效
            tabEntities.add(new TabEntity(tabTitle[i], 0, 0));
        }
        tabLayout.setTabData(tabEntities);
        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
            }

            @Override
            public void onTabReselect(int position) {
            }
        });

        RecyclerView recyclerView = viewHolder.findView(R.id.magic_filter_recycler);
        RecyclerAdapter adapter = RecyclerKit.INSTANCE.adapter(mActivity, 1)
                .recyclerView(recyclerView)
                .withLinearLayout(LinearLayoutManager.HORIZONTAL, false)
                .modelViewBind((pIndex, pViewModel, pViewHolder) -> {
                    bindMagicFilterItem(pViewModel, pViewHolder);
                    return Unit.INSTANCE;
                })
                .modelViewLongClick((pIndex, pViewModel) -> {
                    mIsLongClick = true;
                    recyclerView.setTag(pIndex);
                    UFilter filterItem = (UFilter) pViewModel.getValue();
                    filterItem.setStart(1f);
                    filterItem.setEnabled(true);
                    onMagicFilterChanged(filterItem);
                    return Unit.INSTANCE;
                })
                .build();
        ArrayList<UFilter> items = mFilterSdkManager.getMagicFilterList(mFilterSdkManager.getMusicFilterIndex());
        List<RecyclerAdapter.ViewModel> models = new ArrayList<>();
        for (UFilter item : items) {
            models.add(new RecyclerAdapter.ViewModel(R.layout.item_filter_view, 1, RecyclerAdapter.ModelType.LEADING, item, false));
        }
        adapter.setModels(models);
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent event) {
                if (!mIsLongClick) {
                    return false;
                }
                int action = event.getAction();
                if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                    int position = (int) recyclerView.getTag();
                    Log.i(TAG, "ACTION_UP: stop filter: " + position);
                    mIsLongClick = false;
                    UFilter filterItem = (UFilter) ((RecyclerAdapter) recyclerView.getAdapter()).getViewModels().get(position).getValue();
                    filterItem.setEnabled(false);
                    onMagicFilterChanged(filterItem);
                    viewHolder.findView(R.id.undo_btn).setVisibility(View.VISIBLE);
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {
            }
        });
        mFilterSeekView = viewHolder.findView(R.id.filter_seek_view);
        mFilterSeekView.setFilterMode();
        mFilterSeekView.setOnDataChanged(this);
        setPlayTime(mVideoEditManager.getCurrentPosition(), mVideoDuration);
        viewHolder.findView(R.id.undo_btn).setOnClickListener(v -> {
            mFilterSeekView.removeLastFilter();
            onRemoveLastFilter();
        });
    }

    private void bindMagicFilterItem(RecyclerAdapter.ViewModel viewModel, RecyclerAdapter.ViewHolder viewHolder) {
        UFilter item = (UFilter) viewModel.getValue();
        ImageView imageView = viewHolder.findView(R.id.icon);
        if (isFuFilterSDK()) {
            imageView.setImageResource(item.getResId());
        } else {
            imageView.setImageBitmap(item.getIcon());
        }
        TextView textView = viewHolder.findView(R.id.name);
        textView.setVisibility(View.VISIBLE);
        textView.setText(item.getName());
    }

    private void setPlayTime(long position, long duration) {
        if (mFilterSeekView == null) return;
        float progress = (mTimeFxMode == FilterSeekView.TIMELINE_FX_MODE_REVERSE) ?
                (duration - position) * 100.f / duration :
                100.f * position / duration;
        mFilterSeekView.setFirstValue(progress);
    }

    // 封面
    private void bindMakeCover(RecyclerAdapter.ViewHolder viewHolder) {
        setMenuTitle(viewHolder, R.string.cover_title);
        FrameListView frameList = viewHolder.findView(R.id.frame_list_view);
        frameList.setVideoPath(mVideoPath);
        frameList.setOnVideoFrameScrollListener(this::onVideoFrameScrollChanged);
        frameList.setOnBindViewListener(holder -> holder.mImageView.setOnClickListener(this::onClickThumbnail));
        viewHolder.findView(R.id.make_cover).setOnClickListener(this::onClickMakeCover);
        mSelectedFrameIndex = new ArrayList<>();
        mFrameListViewCover = frameList;
    }

    // 文字特效
    private void bindAddTextView(RecyclerAdapter.ViewHolder viewHolder) {
        viewHolder.findView(R.id.btn_video_title).setOnClickListener(this::onClickVideoTitle);
        viewHolder.findView(R.id.btn_video_time_addr).setOnClickListener(this::onClickVideoTimeAddr);
        viewHolder.findView(R.id.btn_video_text_effect).setOnClickListener(this::onClickTextEffect);
    }

    // 标题
    private void onClickVideoTitle(View view) {
        if (mTitleSticker == null) {
            mTitleSticker = new USticker();
            String stickerText = "Movieous";
            int stickerWidth = mVideoWidth / 3;
            int stickerHeight = stickerWidth / stickerText.length();
            mTitleSticker.init(USticker.StickerType.TEXT, stickerWidth, stickerHeight)
                    .setText(stickerText, Color.WHITE)
                    .setDuration(0, (int) getVideoDuration())
                    .setPosition(mVideoWidth / 2 - stickerWidth / 2, mVideoHeight / 2);
        }
        if (view.getTag() == null) {
            view.setTag(1);
            mVideoEditManager.addSticker(mTitleSticker);
        } else {
            view.setTag(null);
            mVideoEditManager.removeSticker(mTitleSticker);
            mTitleSticker = null;
        }
    }

    // 时间地点
    private void onClickVideoTimeAddr(View view) {
        if (mTimeAddrSticker == null) {
            mTimeAddrSticker = new USticker();
            String stickerText = "十月 18 · 上海";
            int stickerWidth = mVideoWidth / 2;
            int stickerHeight = stickerWidth / stickerText.length();
            mTimeAddrSticker.init(USticker.StickerType.TEXT, stickerWidth, stickerHeight)
                    .setText(stickerText, Color.WHITE)
                    .setDuration(0, (int) getVideoDuration())
                    .setPosition(mVideoWidth / 2 - stickerWidth / 2, mVideoHeight / 2 + 100);
        }
        if (view.getTag() == null) {
            view.setTag(1);
            mVideoEditManager.addSticker(mTimeAddrSticker);
        } else {
            view.setTag(null);
            mVideoEditManager.removeSticker(mTimeAddrSticker);
            mTimeAddrSticker = null;
        }
    }

    // TODO 字幕
    private void onClickTextEffect(View view) {
        showToast(mActivity, getString(R.string.coming_soon_hint));
    }

    private void onVideoFrameScrollChanged(long timeMs) {
        pausePlayback();
        if (timeMs > 0) {
            mVideoEditManager.seekTo((int) timeMs);
        }
    }

    private void onClickThumbnail(View view) {
        pausePlayback();
        ImageView imageView = (ImageView) view;
        if (imageView.getTag() == null) {
            imageView.setTag(false);
        }
        if (imageView.getTag(imageView.getId()) == null) {
            return;
        }
        int keyframeIndex = (int) imageView.getTag(imageView.getId());
        boolean isSelected = !((imageView.getTag() != null) && (boolean) imageView.getTag());
        int padding = isSelected ? 5 : 0;
        imageView.setTag(isSelected);
        imageView.setPadding(padding, padding, padding, padding);
        imageView.setBackgroundResource(R.color.app_default_color);
        if (mSelectedFrameIndex.contains(keyframeIndex)) {
            mSelectedFrameIndex.remove(mSelectedFrameIndex.indexOf(keyframeIndex));
        } else {
            mSelectedFrameIndex.add(keyframeIndex);
        }
    }

    // TODO
    private void onClickMakeCover(View view) {
        showToast(mActivity, getString(R.string.coming_soon_hint));
    }

    // 视频组合

    // 前后拼接

    // MV 特效

    // 片头片尾

    // 视频切割

    // 变速

    private void showTodoToast() {
        showToast(mActivity, getString(R.string.coming_soon_hint));
    }

    // 功能列表
    private List<TextIcon> getMainFunctionItems() {
        ArrayList<TextIcon> items = new ArrayList<>();
        items.add(new TextIcon(R.drawable.sound_set, getString(R.string.audio_volume_title)));
        items.add(new TextIcon(R.drawable.cut, getString(R.string.cut_clip)));
        items.add(new TextIcon(R.drawable.face_beauty_set, getString(R.string.beauty_adjust_title)));
        items.add(new TextIcon(R.drawable.facial_effects, getString(R.string.effect_title)));
        items.add(new TextIcon(R.drawable.filter, getString(R.string.btn_preview_filter)));
        items.add(new TextIcon(R.drawable.cover, getString(R.string.cover_title)));
        items.add(new TextIcon(R.drawable.video_combination, getString(R.string.video_combination_title)));
        items.add(new TextIcon(R.drawable.video_splice, getString(R.string.video_front_back_merge_title)));
        items.add(new TextIcon(R.drawable.video_mv_combine, getString(R.string.mv_title)));
        items.add(new TextIcon(R.drawable.video_title_tail, getString(R.string.video_title_tail_title)));
        items.add(new TextIcon(R.drawable.video_subsection, getString(R.string.video_subsection_title)));
        items.add(new TextIcon(R.drawable.speed_set, getString(R.string.speed_adjust_title)));
        return items;
    }

    // 显示贴纸列表
    private void showStickerFilterFragment() {
        mVideoEditManager.setOutputBuffer(mFilterSdkManager.getRGBABuffer());
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        if (mStickerFilterFragment == null) {
            mStickerFilterFragment = new StickerFilterFragment();
            mStickerFilterFragment.setFilterSdkManager(mFilterSdkManager);
            mStickerFilterFragment.setOnFilterChangedListener(this);
            ft.add(R.id.fragment_container, mStickerFilterFragment);
        } else {
            ft.show(mStickerFilterFragment);
        }
        if (mMusicSelectFragment != null) {
            ft.hide(mMusicSelectFragment);
        }
        ft.commit();
        mCurrentLayout = R.layout.fragment_sticker_filter;
        setFragmentShowing(true);
    }

    // 音乐选择
    private void showMusicFragment() {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        if (mMusicSelectFragment == null) {
            mMusicSelectFragment = new MusicSelectFragment();
            mMusicSelectFragment.setMusicSelectedListener(this);
            ft.add(R.id.fragment_container, mMusicSelectFragment);
        } else {
            ft.show(mMusicSelectFragment);
        }
        if (mStickerFilterFragment != null) {
            ft.hide(mStickerFilterFragment);
        }
        ft.commit();
        mCurrentLayout = R.layout.fragment_music_select;
        setFragmentShowing(true);
    }

    // MusicSelectedListener
    @Override
    public void onMusicSelected(@NotNull String file) {
        mVideoEditManager.setMusicFile(file);
    }

    private boolean isPlaying() {
        return mEditorState == VideoEditorState.Playing;
    }

    private void onClickTogglePlayback() {
        if (isPlaying()) {
            pausePlayback();
        } else {
            startPlayback();
        }
    }

    private void saveVideoFile() {
        pausePlayback();
        showProcessingDialog();
        UVideoFrameListener listener = null;
        synchronized (mActivity) {
            if (isFilterVendorEnabled(false) && mFilterSdkManager != null) {
                if (isFuFilterSDK()) { // 相芯
                    Log.i(TAG, "FACEUNITY filter is enabled!");
                    mFilterSdkManager.destroy();
                    mFilterSdkManager = null;
                    FuSDKManager fuSDKManager = new FuSDKManager(mActivity);
                    fuSDKManager.setPreviewMode(false);
                    if (mCurrentFilter != null) {
                        fuSDKManager.changeFilter(mCurrentFilter);
                    }
                    if (mBeautyFilter != null) {
                        fuSDKManager.changeBeautyFilter(mBeautyFilter);
                    }
                    listener = new UVideoFrameListener() {
                        @Override
                        public void onSurfaceCreated() {
                            Log.i(TAG, "onSurfaceCreated");
                            fuSDKManager.init(mActivity, false);
                        }

                        @Override
                        public void onSurfaceDestroyed() {
                            fuSDKManager.destroy();
                        }

                        @Override
                        public int onDrawFrame(int texId, int texWidth, int texHeight) {
                            int outTexId = (mCurrentFilter != null || mBeautyFilter != null) ? fuSDKManager.onDrawFrame(texId, texWidth, texHeight) : texId;
                            return outTexId;
                        }
                    };
                }
            }
        }
        mVideoEditManager.save(Constants.EDIT_FILE_PATH, listener);
    }

    private long getVideoDuration() {
        if (mVideoDuration == 0) {
            mVideoDuration = UMediaUtil.getMetadata(mVideoPath).duration;
        }
        return mVideoDuration;
    }

}

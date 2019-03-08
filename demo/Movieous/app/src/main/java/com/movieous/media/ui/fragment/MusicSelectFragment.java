package com.movieous.media.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import com.hhl.gridpagersnaphelper.GridPagerSnapHelper;
import com.hhl.recyclerviewindicator.LinePageIndicator;
import com.movieous.media.R;
import com.movieous.media.base.BaseFragment;
import com.movieous.media.mvp.contract.MusicSelectedListener;
import com.movieous.media.mvp.model.entity.MusicFileItem;
import com.movieous.media.utils.ScreenUtils;
import com.movieous.media.utils.Utils;
import io.inchtime.recyclerkit.RecyclerAdapter;
import io.inchtime.recyclerkit.RecyclerKit;
import kotlin.Unit;

import java.util.ArrayList;
import java.util.List;

/**
 * 音乐选择页面, 两行、三列分页显示
 */
public class MusicSelectFragment extends BaseFragment {
    private static final String TAG = "MusicSelectFragment";

    @BindView(R.id.music_list_recyclerView)
    RecyclerView mMusicRecyclerView;
    @BindView(R.id.music_page_indicator)
    LinePageIndicator mLinePageIndicator;

    private Activity mActivity;
    private LayoutInflater mInflater;
    private ArrayList<MusicFileItem> mMusicFileList;
    private MusicSelectedListener mMusicSelectedListener;
    private RecyclerAdapter mMusicAdapter;
    private String mSelectMusicFile;

    public void setMusicSelectedListener(MusicSelectedListener listener) {
        mMusicSelectedListener = listener;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_music_select;
    }

    @Override
    public void lazyLoad() {
        if (mMusicFileList == null) {
            getMusicFileList();
        }
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
        getMusicFileList();
        setupRecyclerView();
    }

    private void getMusicFileList() {
        mMusicFileList = Utils.getMusicList(mActivity);
    }

    // 设置 RecyclerView
    private void setupRecyclerView() {
        mMusicAdapter = RecyclerKit.INSTANCE.adapter(mActivity, 2)
                .recyclerView(mMusicRecyclerView)
                .withGridLayout(GridLayoutManager.HORIZONTAL, false)
                .modelViewBind((pIndex, pViewModel, pViewHolder) -> {
                    bindMusicItem(pViewModel, pViewHolder);
                    return Unit.INSTANCE;
                })
                .modelViewClick((pIndex, pViewModel, view) -> {
                    onMusicMenuClick(pViewModel);
                    return Unit.INSTANCE;
                })
                .build();
        List<RecyclerAdapter.ViewModel> models = new ArrayList<>();
        for (MusicFileItem item : mMusicFileList) {
            models.add(new RecyclerAdapter.ViewModel(R.layout.item_music_grid, 1, RecyclerAdapter.ModelType.LEADING, item, false));
        }
        mMusicAdapter.setEmptyView(R.layout.recyclerkit_view_empty);
        mMusicAdapter.setModels(models);
        //attachToRecyclerView
        GridPagerSnapHelper gridPagerSnapHelper = new GridPagerSnapHelper();
        gridPagerSnapHelper.setRow(2).setColumn(3);
        gridPagerSnapHelper.attachToRecyclerView(mMusicRecyclerView);
        //indicator
        mLinePageIndicator.setRecyclerView(mMusicRecyclerView);
        //Note: pageColumn must be config
        mLinePageIndicator.setPageColumn(3);
    }

    // 主菜单项
    private void bindMusicItem(RecyclerAdapter.ViewModel viewModel, RecyclerAdapter.ViewHolder viewHolder) {
        ViewGroup.LayoutParams layoutParams = viewHolder.getView().getLayoutParams();
        layoutParams.width = ScreenUtils.INSTANCE.getScreenWidth(mActivity) / 3;
        MusicFileItem item = (MusicFileItem) viewModel.getValue();
        // title
        TextView tvTittle = viewHolder.findView(R.id.title);
        tvTittle.setText(item.getTitle());
        // artist
        TextView tvArtist = viewHolder.findView(R.id.artist);
        tvArtist.setText(item.getArtist());
    }

    // 显示子菜单界面
    private void onMusicMenuClick(RecyclerAdapter.ViewModel viewModel) {
        MusicFileItem item = (MusicFileItem) viewModel.getValue();
        String file = item.getPath();
        if (mMusicSelectedListener != null) {
            if (file.equals(mSelectMusicFile)) {
                file = null; // 删除背景音乐
            }
            mMusicSelectedListener.onMusicSelected(file);
        }
        mSelectMusicFile = file;
    }
}

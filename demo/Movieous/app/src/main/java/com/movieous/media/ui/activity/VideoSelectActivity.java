package com.movieous.media.ui.activity;

import android.database.Cursor;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.movieous.media.R;
import com.movieous.media.base.BaseActivity;
import com.movieous.media.mvp.model.select.MediaCursorLoader;
import com.movieous.media.mvp.model.select.MediaLoadManager;
import com.movieous.media.ui.adapter.VideoSelectAdapter;
import iknow.android.utils.callback.SimpleCallback;

@Deprecated
public class VideoSelectActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "VideoSelectActivity";

    @BindView(R.id.video_gridview)
    GridView videoGridView;
    @BindView(R.id.btn_next_step)
    TextView mNextStep;

    private VideoSelectAdapter mVideoSelectAdapter;
    private String mVideoPath;
    private MediaLoadManager mVideoLoadManager;

    @Override
    public int layoutId() {
        return R.layout.activity_video_select;
    }

    @Override
    public void initData() {
        mVideoLoadManager = new MediaLoadManager();
        mVideoLoadManager.setLoader(new MediaCursorLoader(MediaCursorLoader.MediaType.VIDEO));
        mVideoLoadManager.load(this, new SimpleCallback() {
            @SuppressWarnings("unchecked")
            @Override
            public void success(Object obj) {
                if (mVideoSelectAdapter == null) {
                    mVideoSelectAdapter = new VideoSelectAdapter(VideoSelectActivity.this, (Cursor) obj);
                    mVideoSelectAdapter.setItemClickCallback((isSelected, videoPath) -> {
                        mVideoPath = videoPath;
                        mNextStep.setEnabled(true);
                        mNextStep.setTextColor(getResources().getColor(R.color.color_black));
                    });
                } else {
                    mVideoSelectAdapter.swapCursor((Cursor) obj);
                }
                if (videoGridView.getAdapter() == null) {
                    videoGridView.setAdapter(mVideoSelectAdapter);
                }
                mVideoSelectAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void initView() {
        mNextStep.setEnabled(false);
    }

    @Override
    public void start() {
    }

    @OnClick(R.id.btn_next_step)
    @Override
    public void onClick(View v) {
        //VideoEditActivity.start(this, mVideoPath);
        finish();
    }
}

package video.movieous.media.demo.player;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.util.Log;
import android.view.Surface;
import video.movieous.droid.player.MediaPlayer;
import video.movieous.droid.player.listener.OnCompletionListener;
import video.movieous.droid.player.listener.OnErrorListener;
import video.movieous.droid.player.listener.OnPreparedListener;
import video.movieous.engine.video.player.IMediaPlayer;

public class MovieousPlayer implements IMediaPlayer, OnPreparedListener, OnCompletionListener, OnErrorListener {
    private static final String TAG = "MovieousPlayer";

    private MediaPlayer mMediaPlayer;
    private OnPreparedListener mPreparedListener;
    private OnCompletionListener mCompletionListener;
    private OnErrorListener mErrorListener;

    public MovieousPlayer(Context context) {
        initPlayer(context);
    }

    @Override
    public void setDataSource(Context context, Uri uri) {
        mMediaPlayer.setDataSource(uri);
    }

    @Override
    public void prepareAsync() {
        mMediaPlayer.prepare();
    }

    @Override
    public void start() {
        if (mMediaPlayer.getCurrentPosition() >= mMediaPlayer.getDuration()) {
            mMediaPlayer.restart();
        } else {
            mMediaPlayer.start();
        }
    }

    @Override
    public void stop() throws IllegalStateException {
        mMediaPlayer.stop();
    }

    @Override
    public void pause() throws IllegalStateException {
        mMediaPlayer.pause();
    }

    @Override
    public void reset() {
        mMediaPlayer.restart();
    }

    @Override
    public int getVideoWidth() {
        return mMediaPlayer.getVideoWidth();
    }

    @Override
    public int getVideoHeight() {
        return mMediaPlayer.getVideoHeight();
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    @Override
    public void seekTo(int timeMs) {
        mMediaPlayer.seekTo(timeMs);
    }

    @Override
    public int getCurrentPosition() {
        return (int) mMediaPlayer.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return (int) mMediaPlayer.getDuration();
    }

    @Override
    public void release() {
        mMediaPlayer.release();
    }

    @Override
    public void setVolume(float left, float right) {
        mMediaPlayer.setVolume(left, right);
    }

    @Override
    public void setOnPreparedListener(OnPreparedListener listener) {
        mPreparedListener = listener;
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener listener) {
        mCompletionListener = listener;
    }

    @Override
    public void setOnErrorListener(OnErrorListener listener) {
        mErrorListener = listener;
    }

    @Override
    public void setLooping(boolean loop) {
        mMediaPlayer.setLooping(loop);
    }

    @Override
    public boolean isLooping() {
        return true;
    }

    @Override
    public void setPlaySpeed(float speed) {
        mMediaPlayer.setPlaySpeed(speed);
    }

    @Override
    public void setSurface(Surface surface, SurfaceTexture surfaceTexture) {
        Log.i(TAG, "setSurface");
        mMediaPlayer.setSurface(surface, surfaceTexture);
    }

    @Override
    public void onPrepared() {
        if (mPreparedListener != null) {
            mPreparedListener.onPrepared(this);
        }
    }

    @Override
    public void onCompletion() {
        if (mCompletionListener != null) {
            mCompletionListener.onCompletion(this);
        }
    }

    @Override
    public boolean onError(Exception e) {
        return mErrorListener == null || mErrorListener.onError(this, -1, 0);
    }

    private void initPlayer(Context context) {
        mMediaPlayer = new MediaPlayer(context);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
        setLooping(true);
    }
}

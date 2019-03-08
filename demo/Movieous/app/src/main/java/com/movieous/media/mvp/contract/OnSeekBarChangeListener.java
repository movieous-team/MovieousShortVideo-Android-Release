package com.movieous.media.mvp.contract;

import com.movieous.media.view.RangeSeekBar;

public interface OnSeekBarChangeListener {
    default void onCreate(RangeSeekBar RangeSeekBar, int index, float value) {
    }

    default void onSeek(RangeSeekBar RangeSeekBar, int index, float value) {
    }

    default void onSeekStart(RangeSeekBar RangeSeekBar, int index, float value) {
    }

    default void onSeekStop(RangeSeekBar RangeSeekBar, int index, float value) {
    }
}

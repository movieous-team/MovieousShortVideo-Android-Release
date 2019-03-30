package com.movieous.media.view;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.movieous.media.R;
import com.movieous.media.mvp.model.entity.FilterVendor;
import com.movieous.media.mvp.model.entity.MediaParam;
import com.movieous.media.utils.SharePrefUtils;

public class CustomSettingDialog {
    private Context mContext;
    private MediaParam mMediaParam;

    public CustomSettingDialog(Context context, MediaParam param) {
        mContext = context;
        mMediaParam = param;
    }

    public void showDialog() {
        AlertDialog.Builder builder;
        AlertDialog alertDialog;
        View layout = LayoutInflater.from(mContext).inflate(R.layout.custom_setting_view, null);

        // vendor
        RadioGroup rtcVendor = layout.findViewById(R.id.vendor);
        if (mMediaParam.vendor == FilterVendor.FACEUNITY) {
            RadioButton button = layout.findViewById(R.id.vendor_agora);
            button.setChecked(true);
        } else if (mMediaParam.vendor == FilterVendor.SENSETIME) {
            RadioButton button = layout.findViewById(R.id.vendor_zego);
            button.setChecked(true);
        } else {
            RadioButton button = layout.findViewById(R.id.vendor_none);
            button.setChecked(true);
        }
        rtcVendor.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.vendor_agora:
                    mMediaParam.vendor = FilterVendor.FACEUNITY;
                    break;
                case R.id.vendor_zego:
                    mMediaParam.vendor = FilterVendor.SENSETIME;
                    break;
                case R.id.vendor_none:
                    mMediaParam.vendor = FilterVendor.NONE;
                    break;
                default:
                    break;
            }
        });

        CheckBox cbVideoSizeRamain = layout.findViewById(R.id.checkbox_video_size_remain);
        cbVideoSizeRamain.setChecked(mMediaParam.remainVideoSize);

        // width
        SeekBar widthSeekBar = layout.findViewById(R.id.text_set_width);
        widthSeekBar.setEnabled(!mMediaParam.remainVideoSize);
        widthSeekBar.setMax(1024);
        widthSeekBar.setProgress(mMediaParam.width);
        final TextView widthTextView = layout.findViewById(R.id.show_width);
        widthTextView.setText(mMediaParam.width + "");
        widthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    progress = 1;
                }
                widthTextView.setText(progress + "");
                mMediaParam.width = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        widthSeekBar.setProgress(mMediaParam.width);

        // height
        final TextView heightTextView = layout.findViewById(R.id.show_height);
        heightTextView.setText(mMediaParam.height + "");
        SeekBar heightSeekBar = layout.findViewById(R.id.text_set_height);
        heightSeekBar.setMax(1024);
        heightSeekBar.setProgress(mMediaParam.height);
        heightSeekBar.setEnabled(!mMediaParam.remainVideoSize);
        heightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    progress = 1;
                }
                heightTextView.setText(progress + "");
                mMediaParam.height = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

        });
        heightSeekBar.setProgress(mMediaParam.height);

        cbVideoSizeRamain.setOnCheckedChangeListener((buttonView, isChecked) -> {
            boolean isCustomVideoSizeEnabled = !isChecked;
            mMediaParam.remainVideoSize = isChecked;
            widthSeekBar.setEnabled(isCustomVideoSizeEnabled);
            heightSeekBar.setEnabled(isCustomVideoSizeEnabled);
        });

        // video bitrate
        final SeekBar bitrateSeekBar = layout.findViewById(R.id.text_set_bitrate);
        bitrateSeekBar.setProgress(mMediaParam.videoBitrate);
        bitrateSeekBar.setMax(2 * 1024);
        final TextView bitRateTextView = layout.findViewById(R.id.show_bitrate);
        bitRateTextView.setText(mMediaParam.videoBitrate + "");
        bitrateSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    progress = 1;
                }
                bitRateTextView.setText(progress + "");
                mMediaParam.videoBitrate = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        bitrateSeekBar.setProgress(mMediaParam.videoBitrate);

        // fps
        SeekBar fpsSeekBar = layout.findViewById(R.id.text_set_fps);
        fpsSeekBar.setMax(30);
        fpsSeekBar.setProgress(mMediaParam.videoFrameRate);
        final TextView fpsTextView = layout.findViewById(R.id.show_fps);
        fpsTextView.setText(mMediaParam.videoFrameRate + "");
        fpsSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    progress = 1;
                }
                fpsTextView.setText(progress + "");
                mMediaParam.videoFrameRate = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        fpsSeekBar.setProgress(mMediaParam.videoFrameRate);

        // sample rate
        if (mMediaParam.audioSampleRate == 32000) {
            RadioButton button = layout.findViewById(R.id.samplerate_32);
            button.setChecked(true);
        } else if (mMediaParam.audioSampleRate == 44100) {
            RadioButton button = layout.findViewById(R.id.samplerate_44_1);
            button.setChecked(true);
        } else if (mMediaParam.audioSampleRate == 48000) {
            RadioButton button = layout.findViewById(R.id.samplerate_48);
            button.setChecked(true);
        }
        RadioGroup sample = layout.findViewById(R.id.samplerate);
        sample.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.samplerate_32:
                    mMediaParam.audioSampleRate = 32000;
                    break;
                case R.id.samplerate_44_1:
                    mMediaParam.audioSampleRate = 44100;
                    break;
                case R.id.samplerate_48:
                    mMediaParam.audioSampleRate = 48000;
                    break;
            }
        });

        // audio bitrate
        if (mMediaParam.audioBitrate == 48) {
            RadioButton button = layout.findViewById(R.id.transcoding_audio_bitrate_48);
            button.setChecked(true);
        } else if (mMediaParam.audioBitrate == 128) {
            RadioButton button = layout.findViewById(R.id.transcoding_audio_bitrate_128);
            button.setChecked(true);
        }
        RadioGroup audioBitrate = layout.findViewById(R.id.transcoding_audio_bitrate);
        audioBitrate.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.transcoding_audio_bitrate_48:
                    mMediaParam.audioBitrate = 48;
                    break;
                case R.id.transcoding_audio_bitrate_128:
                    mMediaParam.audioBitrate = 128;
                    break;
            }
        });

        // audio channel
        if (mMediaParam.audioChannels == 1) {
            RadioButton button = layout.findViewById(R.id.transcoding_audio_channe_mono);
            button.setChecked(true);
        } else if (mMediaParam.audioChannels == 2) {
            RadioButton button = layout.findViewById(R.id.transcoding_audio_channe_stereo);
            button.setChecked(true);
        }
        RadioGroup audioStereo = layout.findViewById(R.id.transcoding_audio_channel);
        audioStereo.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.transcoding_audio_channe_mono:
                    mMediaParam.audioChannels = 1;
                    break;
                case R.id.transcoding_audio_channe_stereo:
                    mMediaParam.audioChannels = 2;
                    break;
            }
        });

        builder = new AlertDialog.Builder(mContext);
        builder.setView(layout);
        alertDialog = builder.create();
        alertDialog.setOnDismissListener(dialog -> saveSetting(mMediaParam));
        alertDialog.show();
    }

    private void saveSetting(MediaParam setting) {
        SharePrefUtils.save(mContext, setting);
    }
}

package com.movieous.media.view;

import android.app.ProgressDialog;
import android.content.Context;
import com.movieous.media.R;

public class SaveProgressDialog extends ProgressDialog {
    private String mDefaultMsg;
    private int mDefaultProgress;

    public SaveProgressDialog(Context context) {
        super(context);
        mDefaultProgress = 0;
        mDefaultMsg = context.getString(R.string.save_file_tip);
        setMessage(mDefaultMsg);
        setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        setCanceledOnTouchOutside(false);
        setCancelable(true);
        setMax(100);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        resetProgressDialog();
    }

    @Override
    public void cancel() {
        super.cancel();
        resetProgressDialog();
    }

    private void resetProgressDialog() {
        setProgress(mDefaultProgress);
        setMessage(mDefaultMsg);
    }
}

package video.movieous.media.demo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import video.movieous.engine.media.util.MediaUtil;
import video.movieous.media.demo.R;
import video.movieous.shortvideo.UMediaUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MediaFileListAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mFileList = new ArrayList<>();

    public MediaFileListAdapter(Context context) {
        mContext = context;
    }

    public void addFileList(List<String> fileList) {
        mFileList = fileList;
    }

    public void addFile(String filepath) {
        mFileList.add(filepath);
    }

    public void removeFile(int position) {
        mFileList.remove(position);
    }

    public List<String> getFileList() {
        return mFileList;
    }

    @Override
    public int getCount() {
        return mFileList.size();
    }

    @Override
    public Object getItem(int position) {
        return mFileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_video, null, false);
            ViewHolder holder = new ViewHolder();
            holder.fileThumbnail = convertView.findViewById(R.id.file_thumbnail);
            holder.fileName = convertView.findViewById(R.id.file_name);
            holder.videoInfo = convertView.findViewById(R.id.video_info);
            holder.audioInfo = convertView.findViewById(R.id.audio_info);
            holder.fileDuration = convertView.findViewById(R.id.file_duration);
            convertView.setTag(holder);
        }

        String filepath = (String) getItem(position);
        if (filepath != null) {
            ViewHolder holder = (ViewHolder) convertView.getTag();
            Bitmap bitmap = getFileThumbnail(filepath);
            if (bitmap != null) {
                holder.fileThumbnail.setImageBitmap(bitmap);
            }
            MediaUtil.Metadata metadata = UMediaUtil.getMetadata(filepath);
            holder.fileName.setText("文件名：" + new File(filepath).getName());
            if (metadata.hasVideo) {
                String videoParams = "视频：" + metadata.width + "x" + metadata.height + ", " + metadata.rotation + "度";
                holder.videoInfo.setText(videoParams);
            }
            if (metadata.hasAudio) {
                String audioParams = "音频：" + metadata.sampleRate + "Hz, ";
                if (metadata.channelCount == 1) {
                    audioParams += "单通道";
                } else {
                    audioParams += "立体声";
                }
                holder.audioInfo.setText(audioParams);
            } else {
                holder.audioInfo.setText("音频：无");
            }
            holder.fileDuration.setText("时长： " + metadata.duration / 1000 + "秒");
        }

        return convertView;
    }

    private Bitmap getFileThumbnail(String videoPath) {
        return ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.MICRO_KIND);
    }

    private class ViewHolder {
        ImageView fileThumbnail;
        TextView fileName;
        TextView videoInfo;
        TextView audioInfo;
        TextView fileDuration;
    }
}

package Helper;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jarvis.sproject.R;
import com.example.jarvis.sproject.Video;

import java.util.List;
import java.util.Objects;

import Model.VideoFile;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private List<VideoFile> files;
    private Video activity;

    public VideoAdapter(List<VideoFile> files, Video context) {
        this.files = files;
        this.activity = context;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_1, parent, false);
        return new VideoAdapter.VideoViewHolder(itemView, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        VideoFile videoFile = files.get(position);

        holder.icon.setImageResource(R.drawable.video);
        ;
        String originalPath = videoFile.getOriginalPath();
        String name = FileHelper.getFileName(originalPath);
        holder.name.setText(name);

        String duration = FileHelper.getReadableVideoDuration(Long.parseLong(videoFile.getDuration()));

        String details = duration + " | " + videoFile.getDate();
        holder.details.setText(details);

        //checking for action mode status
        if(!activity.isInActionMode) {
            holder.cb.setVisibility(View.GONE);
            holder.cb.setChecked(false);
        } else {
            holder.cb.setVisibility(View.VISIBLE);
            if (activity.isAllSelected) {
                holder.cb.setChecked(true);
            } else {
                holder.cb.setChecked(false);
            }
        }

        holder.itemView.setOnClickListener(v -> {
            if (activity.isInActionMode) {
                CheckBox cb = v.findViewById(R.id.item_cb);
                activity.prepareSelection(cb, position);
            } else {
                onFileClick(videoFile);
            }
        });
    }

    private void onFileClick(VideoFile item) {
        Objects.requireNonNull(activity.fileClickDialog.getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation;
        Objects.requireNonNull(activity.fileClickDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        activity.fileClickDialog.show();

        //file click popup on click listeners
        LinearLayout decryptBtn = activity.fileClickDialog.findViewById(R.id.decrypt_btn);
        LinearLayout openBtn = activity.fileClickDialog.findViewById(R.id.open_btn);
        decryptBtn.setOnClickListener(view -> {
            //decrypt file
            FileHelper.decryptVideoFile(activity, item);
            updateAdapter(activity.fetchVideoFiles());
            activity.fileClickDialog.dismiss();
        });
        openBtn.setOnClickListener(view -> {
            //open file
            FileHelper.openEncryptedFile(activity, item.getNewPath(), item.getOriginalPath());
            activity.fileClickDialog.dismiss();
        });
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public void updateAdapter(List<VideoFile> list) {
        files.clear();
        files.addAll(list);
        notifyDataSetChanged();
    }

    public void filterList(List<VideoFile> filteredList) {
        files = filteredList;
        notifyDataSetChanged();
    }

    public void deleteItems(List<VideoFile> list, SqliteDatabaseHandler db) {
        for (VideoFile f : list) {
            String path = f.getNewPath();
            FileHelper.deleteFile(path);
            db.deleteVideo(f);
        }
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView icon;
        TextView details;
        CheckBox cb;

        Video activity;

        VideoViewHolder(View itemView, Video context) {
            super(itemView);

            name = itemView.findViewById(R.id.item_name);
            icon = itemView.findViewById(R.id.item_icon);
            details = itemView.findViewById(R.id.item_details);
            cb = itemView.findViewById(R.id.item_cb);
            this.activity = context;

            itemView.setOnLongClickListener(activity);
        }
    }
}

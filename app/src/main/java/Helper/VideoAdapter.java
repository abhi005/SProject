package Helper;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jarvis.sproject.R;
import com.example.jarvis.sproject.Video;

import java.util.ArrayList;
import java.util.List;

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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_video, parent, false);
        return new VideoAdapter.VideoViewHolder(itemView, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        VideoFile videoFile = files.get(position);

        String originalPath = videoFile.getOriginalPath();
        String name = FileHelper.getFileName(originalPath);
        holder.name.setText(name);

        String details = videoFile.getDuration() + " | " + videoFile.getDate();
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


        holder.itemView.setOnLongClickListener(view -> {
            activity.setActionMode();
            return false;
        });

        holder.itemView.setOnClickListener(v -> {
            if (activity.isInActionMode) {
                CheckBox cb = (CheckBox) v.findViewById(R.id.item_cb);
                activity.prepareSelection(cb, position);
            } else {
            }
        });
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView details;
        CheckBox cb;

        Video activity;

        public VideoViewHolder(View itemView, Video context) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.item_name);
            details = (TextView) itemView.findViewById(R.id.item_details);
            cb = (CheckBox) itemView.findViewById(R.id.item_cb);
            this.activity = context;
        }
    }

    public void filterList(List<VideoFile> filteredList) {
        files = filteredList;
        notifyDataSetChanged();
    }

    public void updateAdapter(List<VideoFile> list) {
        for(VideoFile f : list) {
            files.remove(f);
        }
        notifyDataSetChanged();
    }
}

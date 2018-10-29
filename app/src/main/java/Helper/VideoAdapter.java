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

import Model.VideoFile;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private ArrayList<VideoFile> files;
    private Video activity;

    public VideoAdapter(ArrayList<VideoFile> files, Video context) {
        this.files = files;
        this.activity = context;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item_layout, parent, false);
        return new VideoAdapter.VideoViewHolder(itemView, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        VideoFile videoFile = files.get(position);

        holder.name.setText(videoFile.getName());
        double temp = videoFile.getSize();
        String mode = "MB";
        if (temp > 1024) {
            temp /= 1024;
            mode = "GB";
        }

        String size = temp + " " + mode;
        String duration = videoFile.getDuration();
        String details = size + " | " + duration;
        holder.details.setText(details);
        holder.image.setImageResource(videoFile.getImage());


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
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name;
        TextView details;
        CheckBox cb;

        Video activity;

        public VideoViewHolder(View itemView, Video context) {
            super(itemView);


            image = (ImageView) itemView.findViewById(R.id.video_item_image);
            name = (TextView) itemView.findViewById(R.id.video_item_name_tv);
            details = (TextView) itemView.findViewById(R.id.video_item_details_tv);
            cb = (CheckBox) itemView.findViewById(R.id.video_item_cb);
            this.activity = context;


            itemView.setOnLongClickListener(activity);

            itemView.setOnClickListener(v -> {
                if (activity.isInActionMode) {
                    CheckBox cb = (CheckBox) v.findViewById(R.id.video_item_cb);
                    activity.prepareSelection(cb, getAdapterPosition());
                } else {
                }
            });
        }
    }


    public void filterList(ArrayList<VideoFile> filteredList) {
        files = filteredList;
        notifyDataSetChanged();
    }

    public void updateAdapter(ArrayList<VideoFile> list) {
        for(VideoFile f : list) {
            files.remove(f);
        }
        notifyDataSetChanged();
    }
}

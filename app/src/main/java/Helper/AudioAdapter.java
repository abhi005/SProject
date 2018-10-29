package Helper;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jarvis.sproject.Audio;
import com.example.jarvis.sproject.R;

import java.util.ArrayList;

import Model.AudioFile;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.AudioViewHolder> {

    private ArrayList<AudioFile> files;
    private Audio activity;

    public AudioAdapter(ArrayList<AudioFile> files, Audio context) {
        this.files = files;
        this.activity = context;
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.audio_item_layout, parent, false);
        return new AudioAdapter.AudioViewHolder(itemView, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {
        AudioFile audioFile = files.get(position);

        holder.name.setText(audioFile.getName());
        double temp = audioFile.getSize();
        String mode = "MB";
        if (temp > 1024) {
            temp /= 1024;
            mode = "GB";
        }

        String size = temp + " " + mode;
        String date = audioFile.getLastModifiedDate();
        String time = audioFile.getLastModifiedTime();
        String details = size + " | " + date + " " + time;
        holder.details.setText(details);
        holder.image.setImageResource(audioFile.getImage());


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

    public class AudioViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name;
        TextView details;
        CheckBox cb;

        Audio activity;

        public AudioViewHolder(View itemView, Audio activity) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.audio_item_image);
            name = (TextView) itemView.findViewById(R.id.audio_item_name_tv);
            details = (TextView) itemView.findViewById(R.id.audio_item_details_tv);
            cb = (CheckBox) itemView.findViewById(R.id.audio_item_cb);
            this.activity = activity;


            itemView.setOnLongClickListener(activity);

            itemView.setOnClickListener(v -> {
                if (activity.isInActionMode) {
                    CheckBox cb = (CheckBox) v.findViewById(R.id.audio_item_cb);
                    activity.prepareSelection(cb, getAdapterPosition());
                } else {
                }
            });
        }
    }

    public void filterList(ArrayList<AudioFile> filteredList) {
        files = filteredList;
        notifyDataSetChanged();
    }

    public void updateAdapter(ArrayList<AudioFile> list) {
        for(AudioFile f : list) {
            files.remove(f);
        }
        notifyDataSetChanged();
    }
}

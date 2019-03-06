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
import java.util.List;

import Model.AudioFile;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.AudioViewHolder> {

    private List<AudioFile> files;
    private Audio activity;

    public AudioAdapter(List<AudioFile> files, Audio context) {
        this.files = files;
        this.activity = context;
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_audio, parent, false);
        return new AudioAdapter.AudioViewHolder(itemView, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {
        AudioFile audioFile = files.get(position);

        String originalPath = audioFile.getOriginalPath();
        String name = FileHelper.getFileName(originalPath);
        holder.name.setText(name);

        String details = audioFile.getSize() + " | " + audioFile.getDate();
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

    class AudioViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView details;
        CheckBox cb;

        Audio activity;

        AudioViewHolder(View itemView, Audio activity) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.item_name);
            details = (TextView) itemView.findViewById(R.id.item_details);
            cb = (CheckBox) itemView.findViewById(R.id.item_cb);
            this.activity = activity;
        }
    }

    public void filterList(List<AudioFile> filteredList) {
        files = filteredList;
        notifyDataSetChanged();
    }

    public void updateAdapter(List<AudioFile> list) {
        for(AudioFile f : list) {
            files.remove(f);
        }
        notifyDataSetChanged();
    }
}

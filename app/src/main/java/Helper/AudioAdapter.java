package Helper;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jarvis.sproject.Audio;
import com.example.jarvis.sproject.R;

import java.util.List;
import java.util.Objects;

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

        holder.itemView.setOnClickListener(v -> {

            if (activity.isInActionMode) {
                CheckBox cb = v.findViewById(R.id.item_cb);
                activity.prepareSelection(cb, position);
            } else {
                onFileClick(audioFile);
            }
        });
    }

    private void onFileClick(AudioFile item) {
        Objects.requireNonNull(activity.fileClickDialog.getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation;
        Objects.requireNonNull(activity.fileClickDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        activity.fileClickDialog.show();

        //file click popup on click listeners
        LinearLayout decryptBtn = activity.fileClickDialog.findViewById(R.id.decrypt_btn);
        LinearLayout openBtn = activity.fileClickDialog.findViewById(R.id.open_btn);
        decryptBtn.setOnClickListener(view -> {
            //decrypt file
            FileHelper.decryptAudioFile(activity, item);
            updateAdapter(activity.fetchAudioFiles());
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

    public void updateAdapter(List<AudioFile> list) {
        files.clear();
        files.addAll(list);
        notifyDataSetChanged();
    }

    public void filterList(List<AudioFile> filteredList) {
        files = filteredList;
        notifyDataSetChanged();
    }

    public void deleteItems(List<AudioFile> list, SqliteDatabaseHandler db) {
        for (AudioFile f : list) {
            String path = f.getNewPath();
            FileHelper.deleteFile(path);
            db.deleteAudio(f);
        }
    }

    class AudioViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView details;
        CheckBox cb;

        Audio activity;

        AudioViewHolder(View itemView, Audio activity) {
            super(itemView);

            name = itemView.findViewById(R.id.item_name);
            details = itemView.findViewById(R.id.item_details);
            cb = itemView.findViewById(R.id.item_cb);
            this.activity = activity;

            itemView.setOnLongClickListener(activity);
        }
    }
}

package Helper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jarvis.sproject.FilePicker;
import com.example.jarvis.sproject.R;
import com.example.jarvis.sproject.Vault;

import java.util.List;

import Model.FileManagerItem;

public class FilePickerAdapter extends RecyclerView.Adapter<FilePickerAdapter.ItemViewHolder> {

    private List<FileManagerItem> files;
    private FilePicker activity;

    public FilePickerAdapter(List<FileManagerItem> files, FilePicker activity) {
        this.files = files;
        this.activity = activity;
    }

    @NonNull
    @Override
    public FilePickerAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_1, parent, false);
        return new FilePickerAdapter.ItemViewHolder(itemView, activity);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull FilePickerAdapter.ItemViewHolder holder, int position) {
        FileManagerItem file = files.get(position);
        holder.name.setText(file.getName());

        if (file.getType().equals("dir")) {
            holder.icon.setImageResource(R.drawable.folder);
        } else {
            holder.icon.setImageResource(FileHelper.getFileIcon(file.getExt()));
        }

        String size = file.getData();
        String date = file.getDate();
        holder.details.setText(size + " | " + date);

        holder.itemView.setTag(position);

        holder.itemView.setOnClickListener(v -> {
            if (file.getType().toLowerCase().equals("dir")) {
                activity.forwardDirectory(file.getPath());
            } else {
                onFileClick(file);
            }
        });
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    private void onFileClick(FileManagerItem item) {
        VaultHelper.addVaultFile(activity, item.getPath());
        Intent intent = new Intent(activity, Vault.class);
        activity.startActivity(intent);
        activity.finish();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView icon;
        TextView details;
        CheckBox cb;

        FilePicker activity;

        ItemViewHolder(View itemView, FilePicker context) {
            super(itemView);

            name = itemView.findViewById(R.id.item_name);
            icon = itemView.findViewById(R.id.item_icon);
            details = itemView.findViewById(R.id.item_details);
            cb = itemView.findViewById(R.id.item_cb);
            cb.setVisibility(View.GONE);
            this.activity = context;
        }
    }
}

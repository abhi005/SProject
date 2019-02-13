package Helper;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jarvis.sproject.FileManager;
import com.example.jarvis.sproject.R;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import Model.FileManagerItem;

public class FileManagerAdapter extends RecyclerView.Adapter<FileManagerAdapter.ItemViewHolder> {

    private List<FileManagerItem> files;
    private FileManager activity;

    public FileManagerAdapter(List<FileManagerItem> files, FileManager context) {
        this.files = files;
        this.activity = context;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_vault, parent, false);
        return new FileManagerAdapter.ItemViewHolder(itemView, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull FileManagerAdapter.ItemViewHolder holder, int position) {
        FileManagerItem file = files.get(position);
        holder.itemName.setText(file.getName());

        if (file.getType().equals("dir")) {
            holder.icon.setImageResource(R.drawable.folder);
        } else {
            if (Global.docFileTypes.contains(file.getExt().toLowerCase())) {
                holder.icon.setImageResource(R.drawable.doc);
            } else if(Global.imageFileTypes.contains(file.getExt().toLowerCase())) {
                holder.icon.setImageResource(R.drawable.image);
            } else if(Global.audioFileTypes.contains(file.getExt().toLowerCase())) {
                holder.icon.setImageResource(R.drawable.audio);
            } else if(Global.videoFileTypes.contains(file.getExt().toLowerCase())) {
                holder.icon.setImageResource(R.drawable.video);
            }
        }

        String size = file.getData();
        String date = file.getDate();
        holder.itemDetails.setText(size + " | " + date);

        //action mode
        if(!activity.isInActionMode) {
            holder.checkBox.setVisibility(View.GONE);
            holder.checkBox.setChecked(false);
        } else {
            holder.checkBox.setVisibility(View.VISIBLE);
            if (activity.isAllSelected) {
                holder.checkBox.setChecked(true);
            } else {
                holder.checkBox.setChecked(false);
            }
        }
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemDetails;
        ImageView icon;
        FileManager activity;
        CheckBox checkBox;

        public ItemViewHolder(View itemView, FileManager context) {
            super(itemView);

            this.itemName = (TextView) itemView.findViewById(R.id.vault_item_name);
            this.itemDetails = (TextView) itemView.findViewById(R.id.vault_item_tv1);
            this.icon = (ImageView) itemView.findViewById(R.id.vault_item_icon);
            this.activity = context;
            this.checkBox = (CheckBox) itemView.findViewById(R.id.vault_item_cb);

            itemView.setOnLongClickListener(context);

            itemView.setOnClickListener(v -> {
                if (activity.isInActionMode) {
                    CheckBox cb = v.findViewById(R.id.vault_item_cb);
                    activity.prepareSelection(cb, getAdapterPosition());
                } else {
                    FileManagerItem item = getItem(getAdapterPosition());
                    if(item.getType().toLowerCase().equals("dir")) {
                         activity.forwardDirectory(item.getPath());
                    } else {
                        onFileClick(item);
                    }
                }
            });
        }
    }

    private void onFileClick(FileManagerItem item) {

    }

    public FileManagerItem getItem(int position) {
        return files.get(position);
    }


    public void filterList(List<FileManagerItem> filteredList) {
        files = filteredList;
        notifyDataSetChanged();
    }

    public void updateAdapter(List<FileManagerItem> list) {
        for(FileManagerItem f : list) {
            files.remove(f);
        }
        notifyDataSetChanged();
    }
}

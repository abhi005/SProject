package Helper;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jarvis.sproject.FileManager;
import com.example.jarvis.sproject.R;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Model.File;
import Model.FileManagerItem;

public class FileManagerAdapter extends RecyclerView.Adapter<FileManagerAdapter.ItemViewHolder> {

    private List<FileManagerItem> files;
    private FileManager activity;
    private String sdCardPath;

    public FileManagerAdapter(List<FileManagerItem> files, FileManager context, String sdCardPath) {
        this.files = files;
        this.activity = context;
        this.sdCardPath = sdCardPath;
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
            holder.icon.setImageResource(FileHelper.getFileIcon(file.getExt()));
        }

        String size = file.getData();
        String date = file.getDate();
        holder.itemDetails.setText(size + " | " + date);

        holder.itemView.setTag(position);

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



        holder.itemView.setOnClickListener(v -> {
            if (activity.isInActionMode) {
                CheckBox cb = v.findViewById(R.id.vault_item_cb);
                activity.prepareSelection(cb, (int) v.getTag());
            } else {
                FileManagerItem item = getItem(position);
                if(item.getType().toLowerCase().equals("dir")) {
                    activity.forwardDirectory(item.getPath());
                } else {
                    onFileClick(item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemDetails;
        ImageView icon;
        FileManager activity;
        CheckBox checkBox;

        ItemViewHolder(View itemView, FileManager context) {
            super(itemView);

            this.itemName = (TextView) itemView.findViewById(R.id.vault_item_name);
            this.itemDetails = (TextView) itemView.findViewById(R.id.vault_item_tv1);
            this.icon = (ImageView) itemView.findViewById(R.id.vault_item_icon);
            this.activity = context;
            this.checkBox = (CheckBox) itemView.findViewById(R.id.vault_item_cb);

            itemView.setOnLongClickListener(context);
        }
    }

    private void onFileClick(FileManagerItem item) {
        Objects.requireNonNull(activity.fileClickDialog.getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation;
        Objects.requireNonNull(activity.fileClickDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        activity.fileClickDialog.show();

        //file click popup on click listeners
        LinearLayout internalStorageBtn = (LinearLayout) activity.fileClickDialog.findViewById(R.id.encrypt_btn);
        LinearLayout sdcardStorageBtn = (LinearLayout) activity.fileClickDialog.findViewById(R.id.open_btn);
        internalStorageBtn.setOnClickListener(view -> {
            //encrypt file
            FileHelper.encryptFile(activity, item.getPath(), sdCardPath);
            activity.fileClickDialog.dismiss();
            Toast.makeText(activity, "file encrypted", Toast.LENGTH_LONG).show();
            activity.forwardDirectory(activity.currentDir.getPath());
        });
        sdcardStorageBtn.setOnClickListener(view -> {
            //open file
            FileHelper.openFile(activity, item.getPath());
            activity.fileClickDialog.dismiss();
        });
    }

    private FileManagerItem getItem(int position) {
        return files.get(position);
    }


    public void filterList(List<FileManagerItem> filteredList) {
        files = filteredList;
        notifyDataSetChanged();
    }

    public void deleteItems(List<FileManagerItem> list) {
        for(FileManagerItem f : list) {
            String path = f.getPath();
            FileHelper.deleteFile(path);
        }
        activity.forwardDirectory(activity.currentDir.getPath());
    }
}

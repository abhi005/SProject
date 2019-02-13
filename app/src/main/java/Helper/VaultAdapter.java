package Helper;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jarvis.sproject.R;
import com.example.jarvis.sproject.Vault;

import java.util.ArrayList;

import Model.File;
import utils.PortraitActivity;

public class VaultAdapter extends RecyclerView.Adapter<VaultAdapter.ItemViewHolder> {

    private ArrayList<File> files;
    private Vault activity;

    public VaultAdapter(ArrayList<File> files, Vault context) {
        this.files = files;
        this.activity = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_vault, parent, false);
        return new VaultAdapter.ItemViewHolder(itemView, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        File file = files.get(position);
        holder.itemName.setText(file.getName());

        switch (file.getType()) {
            case "dir" :
                holder.icon.setImageResource(R.drawable.folder);
                break;

            case "doc" :
                holder.icon.setImageResource(R.drawable.doc);
                break;

            case "image" :
                holder.icon.setImageResource(R.drawable.image);
                break;

            case "audio" :
                holder.icon.setImageResource(R.drawable.audio);
                break;

            case "video" :
                holder.icon.setImageResource(R.drawable.video);
                break;
        }

        double temp = file.getSize();
        String mode = "MB";
        if (temp > 1024) {
            temp /= 1024;
            mode = "GB";
        }

        String size = temp + " " + mode;
        String date = file.getLastModifiedDate();
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
        Vault activity;
        CheckBox checkBox;

        public ItemViewHolder(View itemView, Vault context) {
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

                }
            });
        }
    }

    public void filterList(ArrayList<File> filteredList) {
        files = filteredList;
        notifyDataSetChanged();
    }

    public void updateAdapter(ArrayList<File> list) {
        for(File f : list) {
            files.remove(f);
        }
        notifyDataSetChanged();
    }
}

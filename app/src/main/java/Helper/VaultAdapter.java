package Helper;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import com.example.jarvis.sproject.R;
import com.example.jarvis.sproject.Vault;

import java.util.List;
import java.util.Objects;

import Model.VaultFile;

public class VaultAdapter extends RecyclerView.Adapter<VaultAdapter.ItemViewHolder> {

    private List<VaultFile> files;
    private Vault activity;

    public VaultAdapter(List<VaultFile> files, Vault context) {
        this.files = files;
        this.activity = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_1, parent, false);
        return new VaultAdapter.ItemViewHolder(itemView, activity);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        VaultFile file = files.get(position);

        holder.name.setText(file.getName());
        holder.icon.setImageResource(FileHelper.getFileIcon(file.getOriginalExt()));
        holder.details.setText(file.getSize() + " | " + file.getDate());

        //action mode
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
                activity.prepareSelection(holder.cb, position);
            } else {
                onFileClick(file);
            }
        });
    }

    private void onFileClick(VaultFile file) {
        Objects.requireNonNull(activity.itemClickDialog.getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation;
        Objects.requireNonNull(activity.itemClickDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        activity.itemClickDialog.show();

        //file click popup on click listeners
        LinearLayout exportBtn = activity.itemClickDialog.findViewById(R.id.export_btn);
        LinearLayout openBtn = activity.itemClickDialog.findViewById(R.id.open_btn);
        exportBtn.setOnClickListener(view -> {
            //export file
            VaultHelper.exportFile(activity, file);
            Toast.makeText(activity, "file exported to : " + file.getOriginalPath(), Toast.LENGTH_LONG).show();
            updateAdapter(activity.fetchVaultFiles());
            activity.itemClickDialog.dismiss();
        });
        openBtn.setOnClickListener(view -> {
            //open file
            VaultHelper.openEncryptedFile(activity, file.getName(), file.getOriginalPath());
            activity.itemClickDialog.dismiss();
        });
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public void updateAdapter(List<VaultFile> list) {
        files.clear();
        files.addAll(list);
        notifyDataSetChanged();
    }

    public void filterList(List<VaultFile> filteredList) {
        files = filteredList;
        notifyDataSetChanged();
    }

    public void deleteItems(List<VaultFile> list, SqliteDatabaseHandler db) {
        for (VaultFile f : list) {
            String name = f.getName();
            VaultHelper.deleteFile(activity, name);
            db.deleteVaultFile(f);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView icon;
        TextView details;
        CheckBox cb;

        Vault activity;

        ItemViewHolder(View itemView, Vault context) {
            super(itemView);

            name = itemView.findViewById(R.id.item_name);
            icon = itemView.findViewById(R.id.item_icon);
            details = itemView.findViewById(R.id.item_details);
            cb = itemView.findViewById(R.id.item_cb);
            this.activity = context;

            itemView.setOnLongClickListener(context);
        }
    }
}

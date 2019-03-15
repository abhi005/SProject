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

import com.example.jarvis.sproject.R;
import com.example.jarvis.sproject.Zip;

import java.util.List;
import java.util.Objects;

import Model.ZipFile;

public class ZipAdapter extends RecyclerView.Adapter<ZipAdapter.ViewHolder>{

    private List<ZipFile> files;
    private Zip activity;

    public ZipAdapter(List<ZipFile> files, Zip context) {
        this.files = files;
        this.activity = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_zip, parent, false);
        return new ZipAdapter.ViewHolder(itemView, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ZipFile zipFile = files.get(position);

        String originalPath = zipFile.getOriginalPath();
        String name = FileHelper.getFileName(originalPath);
        holder.name.setText(name);

        String details = zipFile.getSize() + " | " + zipFile.getDate();
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
                onFileClick(zipFile);
            }
        });
    }

    private void onFileClick(ZipFile item) {
        Objects.requireNonNull(activity.fileClickDialog.getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation;
        Objects.requireNonNull(activity.fileClickDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        activity.fileClickDialog.show();

        //file click popup on click listeners
        LinearLayout decryptBtn = activity.fileClickDialog.findViewById(R.id.decrypt_btn);
        LinearLayout openBtn = activity.fileClickDialog.findViewById(R.id.open_btn);
        decryptBtn.setOnClickListener(view -> {
            //decrypt file
            FileHelper.decryptZipFile(activity, item);
            updateAdapter(activity.fetchZipFiles());
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

    public void updateAdapter(List<ZipFile> list) {
        files.clear();
        files.addAll(list);
        notifyDataSetChanged();
    }

    public void filterList(List<ZipFile> filteredList) {
        files = filteredList;
        notifyDataSetChanged();
    }

    public void deleteItems(List<ZipFile> list, SqliteDatabaseHandler db) {
        for (ZipFile f : list) {
            String path = f.getNewPath();
            FileHelper.deleteFile(path);
            db.deleteZip(f);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        TextView details;
        CheckBox cb;

        Zip activity;

        public ViewHolder(View itemView, Zip activity) {
            super(itemView);

            name = itemView.findViewById(R.id.item_name);
            details = itemView.findViewById(R.id.item_details);
            cb = itemView.findViewById(R.id.item_cb);
            this.activity = activity;

            itemView.setOnLongClickListener(activity);
        }
    }
}

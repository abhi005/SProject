package Helper;

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

import com.example.jarvis.sproject.Document;
import com.example.jarvis.sproject.R;

import java.io.File;
import java.util.List;
import java.util.Objects;

import Model.DocFile;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocViewHolder> {


    private List<DocFile> files;
    private Document activity;

    public DocumentAdapter(List<DocFile> files, Document context) {
        this.files = files;
        this.activity = context;
    }


    @NonNull
    @Override
    public DocViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_1, parent, false);
        return new DocumentAdapter.DocViewHolder(itemView, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull DocViewHolder holder, int position) {
        DocFile docFile = files.get(position);

        holder.icon.setImageResource(R.drawable.doc);
        String originalPath = docFile.getOriginalPath();
        String name = FileHelper.getFileName(originalPath);
        holder.name.setText(name);

        String details = docFile.getSize() + " | " + docFile.getDate();
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
                onFileClick(docFile);
            }
        });
    }

    private void onFileClick(DocFile item) {
        Objects.requireNonNull(activity.fileClickDialog.getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation;
        Objects.requireNonNull(activity.fileClickDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        activity.fileClickDialog.show();

        //file click popup on click listeners
        LinearLayout decryptBtn = activity.fileClickDialog.findViewById(R.id.decrypt_btn);
        LinearLayout openBtn = activity.fileClickDialog.findViewById(R.id.open_btn);
        decryptBtn.setOnClickListener(view -> {
            //decrypt file
            FileHelper.decryptDocFile(activity, item);
            updateAdapter(activity.fetchDocFiles());
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

    public void updateAdapter(List<DocFile> list) {
        files.clear();
        files.addAll(list);
        notifyDataSetChanged();
    }


    public void filterList(List<DocFile> filteredList) {
        files = filteredList;
        notifyDataSetChanged();
    }

    public void deleteItems(List<DocFile> list, SqliteDatabaseHandler db) {
        for (DocFile f : list) {
            String path = f.getNewPath();
            File file = new File(path);
            db.decreaseUserData(file.length());
            FileHelper.deleteFile(path);
            db.deleteDoc(f);
        }
    }

    class DocViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView details;
        CheckBox cb;
        ImageView icon;

        Document activity;

        DocViewHolder(View itemView, Document activity) {
            super(itemView);


            name = itemView.findViewById(R.id.item_name);
            icon = itemView.findViewById(R.id.item_icon);
            details = itemView.findViewById(R.id.item_details);
            cb = itemView.findViewById(R.id.item_cb);
            this.activity = activity;

            itemView.setOnLongClickListener(activity);
        }
    }
}

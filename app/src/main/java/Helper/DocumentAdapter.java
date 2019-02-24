package Helper;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.jarvis.sproject.Document;
import com.example.jarvis.sproject.R;

import java.util.ArrayList;
import java.util.List;

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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_document, parent, false);
        return new DocumentAdapter.DocViewHolder(itemView, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull DocViewHolder holder, int position) {
        DocFile docFile = files.get(position);

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

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                activity.setActionMode();
                return false;
            }
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

    public class DocViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView details;
        CheckBox cb;

        Document activity;

        public DocViewHolder(View itemView, Document activity) {
            super(itemView);


            name = (TextView) itemView.findViewById(R.id.item_name);
            details = (TextView) itemView.findViewById(R.id.item_details);
            cb = (CheckBox) itemView.findViewById(R.id.item_cb);
            this.activity = activity;
        }
    }


    public void filterList(List<DocFile> filteredList) {
        files = filteredList;
        notifyDataSetChanged();
    }

    public void updateAdapter(List<DocFile> list) {
        for(DocFile f : list) {
            files.remove(f);
        }
        notifyDataSetChanged();
    }
}

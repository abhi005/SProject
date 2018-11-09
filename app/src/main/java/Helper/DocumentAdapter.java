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

import Model.DocFile;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocViewHolder> {


    private ArrayList<DocFile> files;
    private Document activity;

    public DocumentAdapter(ArrayList<DocFile> files, Document context) {
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

        holder.name.setText(docFile.getName());
        double temp = docFile.getSize();
        String mode = "MB";
        if (temp > 1024) {
            temp /= 1024;
            mode = "GB";
        }

        String size = temp + " " + mode;
        String date = docFile.getLastModifiedDate();
        String time = docFile.getLastModifiedTime();
        String details = size + " | " + date + " " + time;
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


            name = (TextView) itemView.findViewById(R.id.document_item_name_tv);
            details = (TextView) itemView.findViewById(R.id.document_item_details_tv);
            cb = (CheckBox) itemView.findViewById(R.id.document_item_cb);
            this.activity = activity;


            itemView.setOnLongClickListener(activity);

            itemView.setOnClickListener(v -> {
                if (activity.isInActionMode) {
                    CheckBox cb = (CheckBox) v.findViewById(R.id.document_item_cb);
                    activity.prepareSelection(cb, getAdapterPosition());
                } else {
                }
            });
        }
    }


    public void filterList(ArrayList<DocFile> filteredList) {
        files = filteredList;
        notifyDataSetChanged();
    }

    public void updateAdapter(ArrayList<DocFile> list) {
        for(DocFile f : list) {
            files.remove(f);
        }
        notifyDataSetChanged();
    }
}

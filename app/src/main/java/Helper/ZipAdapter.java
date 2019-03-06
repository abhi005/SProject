package Helper;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.jarvis.sproject.R;
import com.example.jarvis.sproject.Zip;

import java.util.List;

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

        holder.itemView.setOnLongClickListener(view -> {
            activity.setActionMode();
            return false;
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

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        TextView details;
        CheckBox cb;

        Zip activity;

        public ViewHolder(View itemView, Zip activity) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.item_name);
            details = (TextView) itemView.findViewById(R.id.item_details);
            cb = (CheckBox) itemView.findViewById(R.id.item_cb);
            this.activity = activity;
        }
    }

    public void filterList(List<ZipFile> filteredList) {
        files = filteredList;
        notifyDataSetChanged();
    }

    public void updateAdapter(List<ZipFile> list) {
        for(ZipFile f : list) {
            files.remove(f);
        }
        notifyDataSetChanged();
    }
}

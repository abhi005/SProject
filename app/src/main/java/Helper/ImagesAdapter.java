package Helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.example.jarvis.sproject.Images;
import com.example.jarvis.sproject.R;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Objects;

import Model.ImageFile;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {

    private Images activity;
    private List<ImageFile> files;

    public ImagesAdapter(Context context, List<ImageFile> files) {
        this.activity = (Images) context;
        this.files = files;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_images, parent, false);
        return new ImagesAdapter.ViewHolder(itemView, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageFile imageFile = files.get(position);

        ByteArrayInputStream bais = new ByteArrayInputStream(imageFile.getThumbnail());
        Bitmap thumbnailImage = BitmapFactory.decodeStream(bais);
        holder.thumbnail.setImageBitmap(thumbnailImage);
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
                onFileClick(imageFile);
            }
        });
    }

    private void onFileClick(ImageFile item) {
        Objects.requireNonNull(activity.fileClickDialog.getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation;
        Objects.requireNonNull(activity.fileClickDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        activity.fileClickDialog.show();

        //file click popup on click listeners
        LinearLayout decryptBtn = activity.fileClickDialog.findViewById(R.id.decrypt_btn);
        LinearLayout openBtn = activity.fileClickDialog.findViewById(R.id.open_btn);
        decryptBtn.setOnClickListener(view -> {
            //decrypt file
            FileHelper.decryptImageFile(activity, item);
            updateAdapter(activity.fetchImageFiles());
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

    public void updateAdapter(List<ImageFile> list) {
        files.clear();
        files.addAll(list);
        notifyDataSetChanged();
    }

    public void deleteItems(List<ImageFile> list, SqliteDatabaseHandler db) {
        for (ImageFile f : list) {
            String path = f.getNewPath();
            FileHelper.deleteFile(path);
            db.deleteImage(f);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView thumbnail;
        private CheckBox cb;

        private Images activity;

        public ViewHolder(View itemView, Images activity) {
            super(itemView);

            thumbnail = itemView.findViewById(R.id.image_thumbnail);
            cb = itemView.findViewById(R.id.item_cb);
            this.activity = activity;

            itemView.setOnLongClickListener(activity);
        }
    }
}

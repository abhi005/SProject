package Helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.example.jarvis.sproject.Images;
import com.example.jarvis.sproject.R;

import java.io.InputStream;
import java.util.List;

import Model.ImageFile;

public class ImagesAdapter extends BaseAdapter {

    private Images activity;
    private List<ImageFile> images;

    public ImagesAdapter(Context context, List<ImageFile> images) {
        this.activity = (Images) context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int position) {
        return images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(activity).inflate(R.layout.item_layout_images, parent, false);
        ImageView thumbnail = (ImageView) convertView.findViewById(R.id.image_thumbnail);
        CheckBox cb = (CheckBox) convertView.findViewById(R.id.image_cb);
        if(!activity.isInActionMode) {
            cb.setVisibility(View.GONE);
            cb.setChecked(false);
        } else {
            cb.setVisibility(View.VISIBLE);
            if (activity.isAllSelected) {
                cb.setChecked(true);
            } else {
                cb.setChecked(false);
            }
        }
        try {
            byte[] imageData = images.get(position).getThumbnail();
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            thumbnail.setImageBitmap(bitmap);
        } catch (Exception e) {
            Log.i("image_trace", "error: " + e.getMessage());
        }

        convertView.setOnClickListener(v -> {
            if(activity.isInActionMode) {
                activity.prepareSelection(v, position);
            }
        });

        convertView.setOnLongClickListener(v -> {
            activity.isInActionMode = true;
            Animation bottomUp = AnimationUtils.loadAnimation(activity, R.anim.bottom_up);
            activity.setActionMode(bottomUp);
            return true;
        });
        return convertView;
    }

    public void updateAdapter(List<ImageFile> list) {
        for(ImageFile c : list) {
            images.remove(c);
        }
        this.notifyDataSetChanged();
    }
}

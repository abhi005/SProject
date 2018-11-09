package Helper;

import android.content.Context;
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

import java.util.List;

import Model.ImageFile;

public class ImagesAdapter extends BaseAdapter {

    private Images activity;
    private List<ImageFile> thumbnails;

    public ImagesAdapter(Context context, List<ImageFile> thumbnails) {
        this.activity = (Images) context;
        this.thumbnails = thumbnails;
    }

    @Override
    public int getCount() {
        return thumbnails.size();
    }

    @Override
    public Object getItem(int position) {
        return thumbnails.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(activity).inflate(R.layout.item_layout_images, parent, false);
        ImageView image = (ImageView) convertView.findViewById(R.id.image_thumbnail);
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
        image.setImageResource(thumbnails.get(position).getId());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activity.isInActionMode) {
                    activity.prepareSelection(v, position);
                }
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                activity.isInActionMode = true;
                Animation bottomUp = AnimationUtils.loadAnimation(activity, R.anim.bottom_up);
                activity.setActionMode(bottomUp);
                return true;
            }
        });
        return convertView;
    }

    public void updateAdapter(List<ImageFile> list) {
        for(ImageFile c : list) {
            thumbnails.remove(c);
        }
        this.notifyDataSetChanged();
    }
}

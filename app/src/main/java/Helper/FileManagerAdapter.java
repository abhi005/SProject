package Helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jarvis.sproject.R;

import Model.File;

public class FileManagerAdapter extends BaseAdapter {

    Context context;
    File[] files;
    LayoutInflater inflater;

    public FileManagerAdapter(Context context, File[] files) {
        this.context = context;
        this.files = files;
        this.inflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return files.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.item_layout_manager, null);
        ImageView icon = (ImageView) convertView.findViewById(R.id.file_type_icon);
        TextView name = (TextView) convertView.findViewById(R.id.file_name);
        TextView size = (TextView) convertView.findViewById(R.id.file_size);
        TextView creationDate = (TextView) convertView.findViewById(R.id.file_creation_date);
        name.setText(files[position].getName());
        double temp = files[position].getSize();
        String mode = "MB";
        if(temp > 1024) {
            temp /= 1024;
            mode = "GB";
        }
        size.setText(new String(temp + mode));
        creationDate.setText(files[position].getCreationDate());
        String fileType = files[position].getType();

        switch (fileType) {
            case "dir" :
                icon.setImageResource(R.drawable.folder);
                break;

            case "doc" :
                icon.setImageResource(R.drawable.doc);
                break;

            case "image" :
                icon.setImageResource(R.drawable.image);
                break;

            case "audio" :
                icon.setImageResource(R.drawable.audio);
                break;

            case "video" :
                icon.setImageResource(R.drawable.video);
                break;
        }
        return convertView;
    }
}

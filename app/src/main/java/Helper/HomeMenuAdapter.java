package Helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jarvis.sproject.R;

public class HomeMenuAdapter extends BaseAdapter{
    Context context;
    int[] icons;
    LayoutInflater inflater;
    String[] titles;
    public HomeMenuAdapter(Context context, int[] icons, String[] titles) {
        this.context = context;
        this.icons = icons;
        this.titles = titles;
        inflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return icons.length;
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
        convertView = inflater.inflate(R.layout.home_menu_icon, null);
        ImageView iconImage = (ImageView) convertView.findViewById(R.id.menu_icon);
        TextView title = (TextView) convertView.findViewById(R.id.icon_title);
        title.setText(titles[position]);
        iconImage.setImageResource(icons[position]);
        return convertView;
    }
}

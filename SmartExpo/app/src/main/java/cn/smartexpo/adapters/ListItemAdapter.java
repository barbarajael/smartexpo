package cn.smartexpo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.smartexpo.R;

public class ListItemAdapter extends ArrayAdapter<ListItem>
{
    public ListItemAdapter(Context context, ArrayList<ListItem> item)
    {
        super(context, 0, item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // Get the data item for this position
        ListItem item = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_button_layout, parent, false);
        }

        // Lookup view for data population
        TextView menu_item_text = (TextView) convertView.findViewById(R.id.childList_placeName);
        ImageView menu_item_image = (ImageView) convertView.findViewById(R.id.childList_buttonFav);

        // Populate the data into the template view using the data object
        menu_item_text.setText(item.textPlace);
        menu_item_image.setImageResource(item.imgPlace_id);

        // Return the completed view to render on screen
        return convertView;
    }
}
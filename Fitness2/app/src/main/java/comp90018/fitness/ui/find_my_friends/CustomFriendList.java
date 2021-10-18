package comp90018.fitness.ui.find_my_friends;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import comp90018.fitness.R;

public class CustomFriendList extends ArrayAdapter {
    private ArrayList<String> names;
    private ArrayList<String> distances;
    private ArrayList<Integer> imageId;
    private Activity context;

    public CustomFriendList(Activity context, ArrayList<String> names, ArrayList<String> distances,
                            ArrayList<Integer> imageId) {
        super(context, R.layout.friend_list_row_item, names);
        this.context = context;
        this.names = names;
        this.distances = distances;
        this.imageId = imageId;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row=convertView;
        LayoutInflater inflater = context.getLayoutInflater();
        if(convertView==null)
            row = inflater.inflate(R.layout.friend_list_row_item, null, true);
        TextView name = (TextView) row.findViewById(R.id.find_friends_bottom_sheet_name);
        TextView distance = (TextView) row.findViewById(R.id.find_friends_bottom_sheet_distance);
        ImageView avatar = (ImageView) row.findViewById(R.id.find_friends_bottom_sheet_avatar);

        name.setText(names.get(position));
        if (distances != null){
            distance.setText(distances.get(position));
        }
        avatar.setImageResource(imageId.get(position));
        return  row;
    }
}

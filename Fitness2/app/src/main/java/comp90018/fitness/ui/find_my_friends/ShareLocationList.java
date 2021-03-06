package comp90018.fitness.ui.find_my_friends;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Map;

import comp90018.fitness.R;

/**
 * A class for defining the location sharing friend list
 */
public class ShareLocationList extends ArrayAdapter {
    private ArrayList<String> names;
    private ArrayList<Integer> imageId;
    private Activity context;
    private String TAG = this.getClass().getSimpleName();

    public ShareLocationList(Activity context, ArrayList<String> names, ArrayList<Integer> imageId) {
        super(context, R.layout.share_location_list_row_item, names);
        this.context = context;
        this.names = names;
        this.imageId = imageId;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row=convertView;
        LayoutInflater inflater = context.getLayoutInflater();
        if(convertView==null)
            row = inflater.inflate(R.layout.share_location_list_row_item, null, true);
        TextView name = (TextView) row.findViewById(R.id.find_friends_bottom_sheet_name);
        ImageView avatar = (ImageView) row.findViewById(R.id.find_friends_bottom_sheet_avatar);
        Button button = (Button) row.findViewById(R.id.shareLocationBtn);

        name.setText(names.get(position));
        avatar.setImageResource(imageId.get(position));
        if (FindFriendsFragment.friends.get(position).get("locationShared") != null &&
                (Boolean) FindFriendsFragment.friends.get(position).get("locationShared")){
            button.setText("Stop Sharing");
        }
        if (FindFriendsFragment.friends.get(position).get("locationShared") != null &&
                !(Boolean) FindFriendsFragment.friends.get(position).get("locationShared")){
            button.setText("Share");
        }

        // listen to any changes on the share button and update when it is clicked
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button.getText().equals("Share")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Are you sure to share current location with " +
                            names.get(position) + "?");
                    builder.setCancelable(true);

                    builder.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    Log.d(TAG, String.valueOf(position));
                                    FindFriendsFragment.shareLocation(position);
                                    button.setText("Stop Sharing");
                                }
                            });

                    builder.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog dialog = builder.create();
                    dialog.show();

                }else {
                    FindFriendsFragment.stopSharingLocation(position);
                    button.setText("Share");
                }

            }
        });


        return  row;
    }




}

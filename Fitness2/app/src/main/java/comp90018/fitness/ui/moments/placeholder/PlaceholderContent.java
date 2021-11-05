package comp90018.fitness.ui.moments.placeholder;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import comp90018.fitness.ui.moments.ItemFragment;
import comp90018.fitness.ui.moments.MyItemRecyclerViewAdapter;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class PlaceholderContent implements MyItemRecyclerViewAdapter.ViewHolder.OnItemListener {
    /**
     * An array of sample (placeholder) items.
     */
    public static final List<PlaceholderItem> ITEMS = new ArrayList<PlaceholderItem>();

    /**
     * A map of sample (placeholder) items, by ID.
     */
    public static final Map<String, PlaceholderItem> ITEM_MAP = new HashMap<String, PlaceholderItem>();

    public static void addItem(PlaceholderItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }


    @Override
    public void onItemClick(int position) {
//        Intent intent = new Intent(this, MomentDetailsActivity.java);
//        startActivity(intent);
    }

    /**
     * A placeholder item representing a piece of content.
     */
    public static class PlaceholderItem implements Parcelable {
        public String id;
        public String content;
        public String title;
        public String url;
        public String time;
        public String authorName;
        public String authorAvatarUrl;
        public String authorDistance;
        public ArrayList<String> imgList = new ArrayList<String>();

        public PlaceholderItem() {
        }

        public static final Parcelable.Creator<PlaceholderItem> CREATOR = new Creator<PlaceholderItem>() {//必须实现的方法
            public PlaceholderItem createFromParcel(Parcel source) {
                PlaceholderItem placeholderItem = new PlaceholderItem();
                placeholderItem.id = source.readString();
                placeholderItem.content = source.readString();
                placeholderItem.title = source.readString();
                placeholderItem.url = source.readString();
                placeholderItem.time = source.readString();
                placeholderItem.authorName = source.readString();
                placeholderItem.authorAvatarUrl = source.readString();
                placeholderItem.authorDistance = source.readString();
                placeholderItem.imgList = source.readArrayList(null);
                return placeholderItem;
            }

            @Override
            public PlaceholderItem[] newArray(int i) {
                return new PlaceholderItem[i];
            }
        };

        public PlaceholderItem(String id, String content, String title, String url, String time, String aName, String aAvatarUrl, String aDistance, ArrayList aImgList) {
            this.id = id;
            this.content = content;
            this.title = title;
            this.url = url;
            this.time = time;
            this.authorName = aName;
            this.authorAvatarUrl = aAvatarUrl;
            this.authorDistance = aDistance;
            this.imgList = aImgList;
        }

        @Override
        public String toString() {
            return title + content;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(id);
            parcel.writeString(content);
            parcel.writeString(title);
            parcel.writeString(url);
            parcel.writeString(time);
            parcel.writeString(authorName);
            parcel.writeString(authorAvatarUrl);
            parcel.writeString(authorDistance);
            parcel.writeList(imgList);
        }
    }
}
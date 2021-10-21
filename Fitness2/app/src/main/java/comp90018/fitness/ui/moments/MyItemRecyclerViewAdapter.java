package comp90018.fitness.ui.moments;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import comp90018.fitness.R;
import comp90018.fitness.databinding.FragmentItemBinding;
import comp90018.fitness.ui.moments.placeholder.PlaceholderContent;
import comp90018.fitness.ui.moments.placeholder.PlaceholderContent.PlaceholderItem;
//import comp90018.fitness.ui.moments.databinding.FragmentItemBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {
    ConstraintLayout constraintLayout;
    ConstraintSet constraintSet;

    private Context mContext;

    private final List<PlaceholderItem> mValues;

    private MyItemRecyclerViewAdapter.ViewHolder.OnItemListener mOnItemListener;

    public MyItemRecyclerViewAdapter(List<PlaceholderItem> items, Context context, ViewHolder.OnItemListener onItemListener) {
        mValues = items;
        mContext = context;

        mOnItemListener = onItemListener;
    }

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(FragmentItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), mOnItemListener);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).title);
//        holder.mContentView.setText(mValues.get(position).content);
        String url = mValues.get(position).url;
        holder.mUserName.setText(mValues.get(position).authorName);
        holder.mUserDistance.setText(mValues.get(position).authorDistance);
        String urlAvatar = mValues.get(position).authorAvatarUrl;

        Glide.with(holder.itemView)
                .load(url)
                .thumbnail(Glide.with(holder.itemView).load(R.drawable.ic_menu_moments))
                .fitCenter()
                .override(500,700)
                .centerCrop()
                .into(holder.mImageView);
        Glide.with(holder.itemView)
                .load(urlAvatar).placeholder(R.drawable.ic_menu_moments)
                .into(holder.mUserAvatar);

        holder.mTime.setText(mValues.get(position).time);
        ArrayList<String> itemImgUrlList = new ArrayList<String>();
        ArrayList<ImageView> ivList = new ArrayList<ImageView>();

//        int currentImgIndex = 0;
//        for (int i = 0; i < mValues.get(position).imgList.size(); i++) {
//            String itemImgUrl = mValues.get(position).imgList.get(i);
//            ImageView iv = new ImageView(mContext);
//            iv.setId(currentImgIndex);
//            holder.mItemImgList.addView(iv);
//            currentImgIndex++;
//            itemImgUrlList.add(itemImgUrl);
//            ivList.add(iv);
//        }
//        constraintLayout = (ConstraintLayout) constraintLayout.findViewById(R.id.item_img_list);
//        constraintSet = new ConstraintSet();
//        constraintSet.clone(constraintLayout);
//        constraintSet.connect(ivList.get(0).getId(), ConstraintSet.LEFT,constraintLayout.getId(), ConstraintSet.LEFT, 0);
//        constraintSet.connect(ivList.get(0).getId(), ConstraintSet.TOP,constraintLayout.getId(), ConstraintSet.TOP, 0);
//        for (int i = 1; i < itemImgUrlList.size(); i++) {
//            constraintSet.connect(ivList.get(i).getId(), ConstraintSet.LEFT, ivList.get(i-1).getId(), ConstraintSet.RIGHT, 5);
//            constraintSet.connect(ivList.get(i).getId(), ConstraintSet.TOP, ivList.get(i-1).getId(), ConstraintSet.TOP, 0);
//            constraintSet.connect(ivList.get(i).getId(), ConstraintSet.BOTTOM, ivList.get(i-1).getId(), ConstraintSet.BOTTOM, 0);
//            constraintSet.applyTo(constraintLayout);
//        }

    }




    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mIdView;
//        public final TextView mContentView;
        public PlaceholderItem mItem;
        public final ImageView mImageView;
        public final TextView mUserName;
        public final ImageView mUserAvatar;
        public final TextView mUserDistance;
        public final TextView mTime;
        private ConstraintLayout mItemImgList;

        OnItemListener mOnItemListener;



        public ViewHolder(FragmentItemBinding binding, OnItemListener onItemListener) {
            super(binding.getRoot());

            mOnItemListener = onItemListener;

            mIdView = binding.itemTitle;
//            mContentView = binding.itemContent;
            mImageView = binding.myImageView;
            mUserName = binding.userName;
            mUserAvatar = binding.userAvatar;
            mUserDistance = binding.userDistance;
            mTime = binding.itemTime;
//            mItemImgList = binding.itemImgList;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnItemListener.onItemClick(getAbsoluteAdapterPosition());
        }


//        @Override
//        public String toString() {
//            return super.toString() + " '" + mContentView.getText() + "'";
//        }


        public interface OnItemListener{
            void onItemClick(int position);
        }

    }

}
package comp90018.fitness.ui.moments;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import comp90018.fitness.R;

public class LooperPagerAdapter extends PagerAdapter {


    private ArrayList<String> mImgList;


    @Override
    public int getCount() {
        if (mImgList != null){
            return mImgList.size();
        }
        return 0;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object){
        container.removeView((View) object);
    }

    public Object instantiateItem(ViewGroup container, int position){
        ImageView imageView = new ImageView(container.getContext());
        container.addView(imageView);
        System.out.println(mImgList.get(position));
        Glide.with(container.getContext())
                .load(mImgList.get(position))
//                .override(300, 300)
                .placeholder(R.drawable.ic_menu_moments)
                .centerCrop()
                .into(imageView);
        return imageView;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public void setData(ArrayList<String> imgUrlList) {
        this.mImgList = imgUrlList;
    }
}

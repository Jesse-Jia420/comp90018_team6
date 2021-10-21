package comp90018.fitness.ui.moments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import comp90018.fitness.R;
import comp90018.fitness.ui.moments.placeholder.PlaceholderContent;

public class MomentDetailsActivity extends AppCompatActivity {


    private static final String TAG = "MomentDetailsActivity";

    private ViewPager mLoopPager;
    private LooperPagerAdapter mLoopPagerAdapter;
    private ArrayList<String> imgUrlList = new ArrayList<String>();
    private TextView mTitle;
    private TextView mContent;
    private TextView mTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moment_details);

        initView();

        Log.d(TAG,"onCreate: called.");
//        TextView userName = (TextView)findViewById(R.id.textView2);
        Bundle bundle = getIntent().getExtras();
        if(bundle.getParcelable("some_content") != null)
        {
            //TODO here get the string stored in the string variable and do
            // setText() on userName
            PlaceholderContent.PlaceholderItem item = bundle.getParcelable("some_content");
//            userName.setText(item.content);
            imgUrlList = item.imgList;
            mTitle.setText(item.title);
            mContent.setText(item.content);
            mTime.setText(item.time);
        }
        LooperPagerAdapter looperPagerAdapter = new LooperPagerAdapter();

        mLoopPagerAdapter.setData(imgUrlList);
        mLoopPagerAdapter.notifyDataSetChanged();
    }

    private void initView() {
        mLoopPager = (ViewPager) this.findViewById(R.id.loop_pager);
        mLoopPagerAdapter = new LooperPagerAdapter();
        mLoopPager.setAdapter(mLoopPagerAdapter);

        mTitle = this.findViewById(R.id.moment_detail_title);
        mContent = this.findViewById(R.id.moment_detail_content);
        mTime = this.findViewById(R.id.moment_detail_time);

    }
}
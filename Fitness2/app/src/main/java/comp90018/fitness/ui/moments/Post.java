package comp90018.fitness.ui.moments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Post {
    private String mTitle;
    private String mContent;
    private ArrayList<String> mImageUrlList;
    private String mAuthorName;
    private String mAuthorAvatarUrl;
    private String mTime;
    private double mPosition_Latitude;
    private double mPosition_Longitude;
    private String mAuthorId;

    public Post(String title, String content, ArrayList<String> postImgUrl, String userName, String userImageUrl, String auhorId){
        if(title.trim().equals("")){
            title = "No Title";
        }else if(content.trim().equals("")){
            content = "No Content";
        }else if(userName.trim().equals("")){
            userName = "Default User";
        }

        mTitle = title;
        mContent = content;
        mImageUrlList = postImgUrl;
        mAuthorName = userName;
        mAuthorAvatarUrl = userImageUrl;
//        mPosition_Latitude = Latitude;
//        mPosition_Longitude = Longitude;
        mAuthorId = auhorId;
        mTime = getTime();
    }

    private String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss a");// a为am/pm的标记
        Date date = new Date();// 获取当前时间
        System.out.println("现在时间：" + sdf.format(date)); // 输出已经格式化的现在时间（24小时制）
        return sdf.format(date);
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmContent() {
        return mContent;
    }

    public List<String> getmImageUrl() {
        return mImageUrlList;
    }

    public String getmAuthorName() {
        return mAuthorName;
    }

    public String getmAuthorAvatarUrl() {
        return mAuthorAvatarUrl;
    }

    public String getmTime() {
        return mTime;
    }

    public double getmPosition_Latitude() {
        return mPosition_Latitude;
    }

    public double getmPosition_Longitude() {
        return mPosition_Longitude;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setmContent(String mContent) {
        this.mContent = mContent;
    }

    public void setmImageUrl(ArrayList<String> mImageUrl) {
        this.mImageUrlList = mImageUrl;
    }

    public void setmAuthorName(String mAuthorName) {
        this.mAuthorName = mAuthorName;
    }

    public void setmAuthorAvatarUrl(String mAuthorAvatarUrl) {
        this.mAuthorAvatarUrl = mAuthorAvatarUrl;
    }

    public void setmTime(String mTime) {
        this.mTime = mTime;
    }

    public void setmPosition_Latitude(int mPosition_Latitude) {
        this.mPosition_Latitude = mPosition_Latitude;
    }

    public void setmPosition_Longitude(int mPosition_Longitude) {
        this.mPosition_Longitude = mPosition_Longitude;
    }
}

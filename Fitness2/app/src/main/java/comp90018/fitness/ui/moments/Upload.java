package comp90018.fitness.ui.moments;

public class Upload {
    private String mName;
    private String mImageUrl;

    public Upload(){
        //empty constructor needed
    }
    public Upload(String name, String imageUrl){
        if(name.trim().equals("")){
            name = "No Name";
        }
        mName = name;
        mImageUrl = imageUrl;
    }
    public String getmName(){
        return mName;
    }
    public void setmName(String name){
        mName = name;
    }

    public String getmImageUrl(){
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }
}

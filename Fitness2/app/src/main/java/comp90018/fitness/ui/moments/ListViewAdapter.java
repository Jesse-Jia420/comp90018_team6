package comp90018.fitness.ui.moments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import comp90018.fitness.R;
import comp90018.fitness.databinding.FragmentItemBinding;
import comp90018.fitness.ui.moments.MomentDetailsActivity.CommentsItem;

import java.util.ArrayList;
import java.util.List;

public class ListViewAdapter extends RecyclerView.Adapter<ListViewAdapter.ViewHolder> {
    private ListView mCommentsList;
    private Context mContext;
    private List<CommentsItem> mComments = new ArrayList<CommentsItem>();

    public ListViewAdapter(Context context) {
        mContext = context;
//        mComments = items;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setUserName(mComments.get(position).userName);
        holder.setCommentContent(mComments.get(position).content);
        holder.setCommentTime(mComments.get(position).time);
    }


    @Override
    public int getItemCount() {
        return mComments == null ? 0 : mComments.size();
    }

    public void setData(ArrayList<CommentsItem> comments) {
        this.mComments = comments;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public final TextView mCommentUserName;
        public final TextView mCommentContent;
        public final TextView mCommentTime;

        private int mPosition;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mCommentUserName = itemView.findViewById(R.id.comment_user_name);
            mCommentContent = itemView.findViewById(R.id.comment_content);
            mCommentTime = itemView.findViewById(R.id.comment_time);
        }

        public void setPosition(int position) {
            this.mPosition = position;
        }

        public void setUserName(String name) {
            mCommentUserName.setText(name);
        }
        public void setCommentContent(String content) {
            mCommentContent.setText(content);
        }
        public void setCommentTime(String time) {
            mCommentTime.setText(time);
        }
    }
}

package com.example.a2;

import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.*;
import android.widget.LinearLayout;

import java.util.List;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder>{
    static private int CHANGE_LINE = 30;

    static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout layout_recived;
        LinearLayout layout_sent;
        TextView recived_msg;
        TextView sent_msg;

        public ViewHolder(View view){
            super(view);
            layout_recived = view.findViewById(R.id.layout_recived);
            layout_sent = view.findViewById(R.id.layout_sent);
            recived_msg = view.findViewById(R.id.recived_msg);
            sent_msg = view.findViewById(R.id.sent_msg);
        }
    }
    private List<MessageContent> list;

    public MsgAdapter(List<MessageContent> list){
        this.list = list;
    }


    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder( ViewHolder holder, int position) {
        MessageContent messageContent = list.get(position);
        StringBuilder str = new StringBuilder(messageContent.getContent());
        StringBuilder newstr = new StringBuilder("");
        int blank =0;
        int l=0;
        int x=1;
        for(int i=0; i<messageContent.getContent().length();i++){
            newstr.append(str.charAt(i));
            l++;
            if(i%CHANGE_LINE ==0 && i!=0){blank++;}
            if(blank>0 && (str.charAt(i) == ' '|| str.charAt(i) == '\n') ){
                newstr.append("\n");
                blank--;
                l=0;
                x++;
            }
            if( l>CHANGE_LINE+6){
                newstr.append("\n");
                blank--;
                l=0;
                x++;
            }
        }

        if(!messageContent.isSent()){
            ViewGroup.LayoutParams ly = holder.layout_sent.getLayoutParams();
            ly.height = x*50+ly.height;
            holder.layout_sent.setLayoutParams(ly );

            holder.layout_recived.setVisibility(View.VISIBLE);
            holder.recived_msg.setText(newstr.toString());
            holder.layout_sent .setVisibility(View.GONE);
        }else{
            ViewGroup.LayoutParams ly = holder.layout_sent.getLayoutParams();
            ly.height = x*50+ly.height;
            holder.layout_sent.setLayoutParams(ly );
            holder.layout_sent.setVisibility(View.VISIBLE);
            holder.sent_msg.setText(newstr.toString());
            holder.layout_recived.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
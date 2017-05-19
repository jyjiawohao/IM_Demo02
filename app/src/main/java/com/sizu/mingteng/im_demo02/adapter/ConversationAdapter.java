package com.sizu.mingteng.im_demo02.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.util.DateUtils;
import com.sizu.mingteng.im_demo02.R;

import java.util.Date;
import java.util.List;

/**
 * Created by lenovo on 2017/5/19.
 */

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>{
    private List<EMConversation> mEMConversationList;

    public interface OnItemClickListener{
        void onItemClick(EMConversation conversation);
    }
    private OnItemClickListener mOnItemClickListener;
    public  void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.mOnItemClickListener = onItemClickListener;
    }

    public ConversationAdapter(List<EMConversation> emConversationList) {
        mEMConversationList = emConversationList;

    }

    @Override
    public ConversationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_conversation, parent, false);
        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ConversationViewHolder holder, int position) {
        final EMConversation emConversation = mEMConversationList.get(position);
        String name = emConversation.conversationId();//TODO ()获取聊天对象的用户名  头像图片无法获取
        holder.mTvUsername.setText(name);

        EMMessage lastMessage = emConversation.getLastMessage();
        long msgTime = lastMessage.getMsgTime(); //消息的时间
        holder.mTvTime.setText(DateUtils.getTimestampString(new Date(msgTime)));

       /* EMTextMessageBody lastMessageBody = (EMTextMessageBody) lastMessage.getBody();
        String lastMessageBodyMessage = lastMessageBody.getMessage();
        holder.mTvMsg.setText(lastMessageBodyMessage);*/

        EMMessage latestMessageFromOthers = emConversation.getLatestMessageFromOthers();//从其他人那里获取的最新消息
        if (latestMessageFromOthers!=null){
            EMTextMessageBody lastMessageBody= (EMTextMessageBody) latestMessageFromOthers.getBody();
            String message = lastMessageBody.getMessage();
            holder.mTvMsg.setText(message);
        }

        int unreadMsgCount = emConversation.getUnreadMsgCount(); //获取未读消息数量
        if (unreadMsgCount>99){ //消息数量大于99
            holder.mTvUnread.setVisibility(View.VISIBLE);
            holder.mTvUnread.setText("99+");
        }else if (unreadMsgCount>0){
            holder.mTvUnread.setVisibility(View.VISIBLE);
            holder.mTvUnread.setText(unreadMsgCount+"");
        }else{
            holder.mTvUnread.setVisibility(View.GONE);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //点击事件
                if (mOnItemClickListener!=null){
                    mOnItemClickListener.onItemClick(emConversation);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mEMConversationList==null?0:mEMConversationList.size();
    }

    class ConversationViewHolder extends RecyclerView.ViewHolder{

        TextView mTvUsername;
        TextView mTvTime;
        TextView mTvMsg;
        TextView mTvUnread;

        public ConversationViewHolder(View itemView) {
            super(itemView);
            mTvUsername = (TextView) itemView.findViewById(R.id.tv_username);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
            mTvMsg = (TextView) itemView.findViewById(R.id.tv_msg);
            mTvUnread = (TextView) itemView.findViewById(R.id.tv_unread);
        }
    }
}

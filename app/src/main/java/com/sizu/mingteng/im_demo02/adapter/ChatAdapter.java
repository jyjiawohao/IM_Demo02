package com.sizu.mingteng.im_demo02.adapter;


import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.util.DateUtils;
import com.sizu.mingteng.im_demo02.R;

import java.util.Date;
import java.util.List;

/**
 * Created by lenovo on 2017/5/16.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder>{
    private List<EMMessage> mEMMessageList;

    public ChatAdapter(List<EMMessage> emMessageList) {
        mEMMessageList = emMessageList;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_receiver, parent, false);
        } else if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_send, parent, false);
        }
        ChatViewHolder chatViewHolder = new ChatViewHolder(view);
        return chatViewHolder;
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        EMMessage emMessage = mEMMessageList.get(position);
        long msgTime = emMessage.getMsgTime();
        //需要将消息body转换为EMTextMessageBody
        EMTextMessageBody body = (EMTextMessageBody) emMessage.getBody();
        String message = body.getMessage();
        holder.mTvMsg.setText(message);
        holder.mTvTime.setText(DateUtils.getTimestampString(new Date(msgTime)));
        if (position==0){
            holder.mTvTime.setVisibility(View.VISIBLE);
        }else{
            EMMessage preMessage = mEMMessageList.get(position - 1);
            long preMsgTime = preMessage.getMsgTime();//获取实际 判断俩条消息是否 短时间发送的  俩条消息相近
            if (DateUtils.isCloseEnough(msgTime,preMsgTime)){
                holder.mTvTime.setVisibility(View.GONE);
            }else{
                holder.mTvTime.setVisibility(View.VISIBLE);
            }
        }
        if (emMessage.direct()==EMMessage.Direct.SEND){ //获取消息状态
            switch (emMessage.status()) { //根据状态
                case INPROGRESS: // 发送状态
                    holder.mIvState.setVisibility(View.VISIBLE);
                    holder.mIvState.setImageResource(R.drawable.msg_state_animation);//设置帧动画
                    AnimationDrawable drawable = (AnimationDrawable) holder.mIvState.getDrawable(); //转换为动画 播放帧动画
                    if (drawable.isRunning()){//判断是否是正在运行 如果是就关闭 在重新 开启播放
                        drawable.stop();
                    }
                    drawable.start(); //播放
                    break;
                case SUCCESS: //发送成功
                    holder.mIvState.setVisibility(View.GONE);
                    break;
                case FAIL: //失败
                    holder.mIvState.setVisibility(View.VISIBLE);
                    holder.mIvState.setImageResource(R.mipmap.msg_error);
                    break;
                case CREATE:
                    break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        EMMessage emMessage = mEMMessageList.get(position);
        return emMessage.direct() == EMMessage.Direct.RECEIVE ? 0 : 1;
    }

    @Override
    public int getItemCount() {
        return mEMMessageList == null ? 0 : mEMMessageList.size();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {

        TextView mTvTime;
        TextView mTvMsg;
        ImageView mIvState;

        public ChatViewHolder(View itemView) {
            super(itemView);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
            mTvMsg = (TextView) itemView.findViewById(R.id.tv_msg);
            mIvState = (ImageView) itemView.findViewById(R.id.iv_state);
        }
    }
}

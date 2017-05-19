package com.sizu.mingteng.im_demo02.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sizu.mingteng.im_demo02.R;
import com.sizu.mingteng.im_demo02.utils.StringUtils;
import com.sizu.mingteng.im_demo02.widget.IContactAdapter;

import java.util.List;

/**
 * Created by lenovo on 2017/5/15.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder>implements IContactAdapter {


    private List<String> data;
    private OnItemClickListener mOnItemClickListener;

    @Override
    public List<String> getData() {
        return data;
    }

    public interface OnItemClickListener{
        void onItemLongClick(String contact, int position);
        void onItemClick(String contact, int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.mOnItemClickListener = onItemClickListener;
    }

    public ContactAdapter(List<String> contacts) {
        data = contacts;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, final int position) {
        final String contact = data.get(position);
        holder.mTvUsername.setText(contact);
        String initial = StringUtils.getInitial(contact);
        holder.mTvSection.setText(initial);
        if (position==0){
            holder.mTvSection.setVisibility(View.VISIBLE); //显示字母
        }else{
            //获取上一个首字母
            String preContact = data.get(position - 1);
            //获取下一个字母
            String preInitial = StringUtils.getInitial(preContact);
            if (preInitial.equals(initial))
                holder.mTvSection.setVisibility(View.GONE); //隐藏字母
            else
                holder.mTvSection.setVisibility(View.VISIBLE); //显示字母
        }
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemClickListener !=null){
                    mOnItemClickListener.onItemLongClick(contact,position);
                }
                return true;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener!=null){
                    mOnItemClickListener.onItemClick(contact,position);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView mTvSection;
        TextView mTvUsername;

        public ContactViewHolder(View itemView) {
            super(itemView);
            mTvSection = (TextView) itemView.findViewById(R.id.tv_section);
            mTvUsername = (TextView) itemView.findViewById(R.id.tv_username);
        }
    }



    public void setData(List<String> data) {
        this.data = data;
    }
}

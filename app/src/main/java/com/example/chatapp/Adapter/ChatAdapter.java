package com.example.chatapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.model.Message;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{

    List<Message> messageList;
    Context context;

    public ChatAdapter(List<Message> messageList, Context context) {
        this.messageList = messageList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_recycler_view_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Message message = messageList.get(position);

        switch (message.getPosition()){
            case "single":
                if (message.isSender()){
                    holder.sendMessage.setText(message.getMessage());
                    setVisibility(holder.sendMessage, holder);
                }else {
                    holder.receiveMessage.setText(message.getMessage());
                    setVisibility(holder.receiveMessage, holder);
                }
                break;
            case "first":
                if (message.isSender()){
                    holder.sendMessageFirst.setText(message.getMessage());
                    setVisibility(holder.sendMessageFirst, holder);
                }else {
                    holder.receiveMessageFirst.setText(message.getMessage());
                    setVisibility(holder.receiveMessageFirst, holder);
                }
                break;
            case "middle":
                if (message.isSender()){
                    holder.sendMessageMiddle.setText(message.getMessage());
                    setVisibility(holder.sendMessageMiddle, holder);
                }else {
                    holder.receiveMessageMiddle.setText(message.getMessage());
                    setVisibility(holder.receiveMessageMiddle, holder);
                }
                break;
            case "last":
                if (message.isSender()){
                    holder.sendMessageLast.setText(message.getMessage());
                    setVisibility(holder.sendMessageLast, holder);
                }else {
                    holder.receiveMessageLast.setText(message.getMessage());
                    setVisibility(holder.receiveMessageLast, holder);
                }
                break;
        }

    }

    private void setVisibility(TextView view, ViewHolder holder) {
        holder.sendMessage.setVisibility(View.GONE);
        holder.sendMessageFirst.setVisibility(View.GONE);
        holder.sendMessageLast.setVisibility(View.GONE);
        holder.sendMessageMiddle.setVisibility(View.GONE);
        holder.receiveMessage.setVisibility(View.GONE);
        holder.receiveMessageFirst.setVisibility(View.GONE);
        holder.receiveMessageMiddle.setVisibility(View.GONE);
        holder.receiveMessageLast.setVisibility(View.GONE);

        view.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView sendMessage, sendMessageFirst, sendMessageMiddle, sendMessageLast,
                         receiveMessage, receiveMessageFirst, receiveMessageMiddle, receiveMessageLast;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            sendMessage = (TextView) itemView.findViewById(R.id.send_message);
            sendMessageFirst = (TextView) itemView.findViewById(R.id.send_message_first);
            sendMessageMiddle = (TextView) itemView.findViewById(R.id.send_message_middle);
            sendMessageLast = (TextView) itemView.findViewById(R.id.send_message_last);
            receiveMessage = (TextView) itemView.findViewById(R.id.recieve_message);
            receiveMessageFirst = (TextView) itemView.findViewById(R.id.recieve_message_first);
            receiveMessageMiddle = (TextView) itemView.findViewById(R.id.recieve_message_middle);
            receiveMessageLast = (TextView) itemView.findViewById(R.id.recieve_message_last);


        }
    }

}

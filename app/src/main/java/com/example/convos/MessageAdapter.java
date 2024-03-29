package com.example.convos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<MessageModelClass> messagesAdapterArrayList;
    int ITEM_SEND=1;
    int ITEM_RECIVE=2;
    FirebaseAuth auth;

    public MessageAdapter(Context context, ArrayList<MessageModelClass> messagesAdapterArrayList) {
        this.context = context;
        this.messagesAdapterArrayList = messagesAdapterArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==ITEM_SEND){
            View view= LayoutInflater.from(context).inflate(R.layout.sender_layout,parent,false);
            return new senderViewHolder(view);
        }
        else{
            View view=LayoutInflater.from(context).inflate(R.layout.reciever_layout,parent,false);
            return new recieverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModelClass msgs=messagesAdapterArrayList.get(position);
        if(holder.getClass()==senderViewHolder.class){
            senderViewHolder viewHolder=(senderViewHolder) holder;
            viewHolder.msgtxt.setText(msgs.getMessage());
            Picasso.get().load(ChatActivity.senderImg).into(viewHolder.circleImageView);
        }
        else{
            recieverViewHolder viewHolder=(recieverViewHolder) holder;
            viewHolder.msgtxt.setText(msgs.getMessage());
            Picasso.get().load(ChatActivity.reciverIImg).into(viewHolder.circleImageView);
        }
    }

    @Override
    public int getItemCount() {
        return messagesAdapterArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        MessageModelClass messages=messagesAdapterArrayList.get(position);
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messages.getSenderid())){
            return ITEM_SEND;
        }
        else{
            return ITEM_RECIVE;
        }
    }

    class senderViewHolder extends RecyclerView.ViewHolder{
        CircleImageView circleImageView;
        TextView msgtxt;

        public senderViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView=itemView.findViewById(R.id.profilerggg);
            msgtxt=itemView.findViewById(R.id.msgsendertyp);
        }
    }
    class recieverViewHolder extends RecyclerView.ViewHolder{
        CircleImageView circleImageView;
        TextView msgtxt;

        public recieverViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView=itemView.findViewById(R.id.pro);
            msgtxt=itemView.findViewById(R.id.recivertextset);
        }
    }
}

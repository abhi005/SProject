package Helper;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jarvis.sproject.Chat;
import com.example.jarvis.sproject.Messaging;
import com.example.jarvis.sproject.R;

import java.io.Serializable;
import java.util.List;

import Model.Conversation;

public class MessagingAdapter extends RecyclerView.Adapter<MessagingAdapter.MessageViewHolder> {

    private List<Conversation> conversations;
    private Messaging messaging;

    public MessagingAdapter(List<Conversation> conversations, Messaging context) {
        this.conversations = conversations;
        this.messaging = context;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_messaging, parent, false);
        return new MessageViewHolder(itemView, messaging);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Conversation conversation = conversations.get(position);

        holder.tv1.setText(conversation.getPersonName());
        holder.tv2.setText(conversation.getLatestTime());
        holder.tv3.setText(conversation.getLastMessage());

        //checking for action mode status
        if(!messaging.isInActionMode) {
            holder.checkBox.setVisibility(View.GONE);
            holder.checkBox.setChecked(false);
        } else {
            holder.checkBox.setVisibility(View.VISIBLE);
            if (messaging.isAllSelected) {
                holder.checkBox.setChecked(true);
            } else {
                holder.checkBox.setChecked(false);
            }
        }
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{
        TextView tv1, tv2, tv3;
        CheckBox checkBox;
        ImageView personBtn;

        Messaging messaging;
        MessageViewHolder(View itemView, Messaging messaging) {
            super(itemView);
            tv1 = (TextView) itemView.findViewById(R.id.messaging_item_tv1);
            tv2 = (TextView) itemView.findViewById(R.id.messaging_item_tv2);
            tv3 = (TextView) itemView.findViewById(R.id.messaging_item_tv3);
            checkBox = (CheckBox) itemView.findViewById(R.id.messaging_item_cb);
            personBtn = (ImageView) itemView.findViewById(R.id.messaging_item_person_btn);
            this.messaging = messaging;

            itemView.setOnLongClickListener(messaging);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (messaging.isInActionMode) {
                        CheckBox cb = (CheckBox) v.findViewById(R.id.messaging_item_cb);
                        messaging.prepareSelection(cb, getAdapterPosition());
                    } else {
                        messaging.isAllSelected = false;
                        Intent intent = new Intent(messaging, Chat.class);
                        intent.putExtra("MESSAGES", (Serializable) conversations.get(getAdapterPosition()).getMessages());
                        intent.putExtra("PERSON_NAME", conversations.get(getAdapterPosition()).getPersonName());
                        intent.putExtra("PERSON_CONTACT", conversations.get(getAdapterPosition()).getPersonContact());
                        messaging.startActivity(intent);
                    }
                }
            });
        }
    }

    public void filterList(List<Conversation> filteredList) {
        conversations = filteredList;
        notifyDataSetChanged();
    }

    public void updateAdapter(List<Conversation> list) {
        for(Conversation c : list) {
            conversations.remove(c);
        }
        notifyDataSetChanged();
    }
}

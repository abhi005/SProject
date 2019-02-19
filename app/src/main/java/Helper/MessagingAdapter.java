package Helper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

        String dateStr = conversation.getLatestDate();
        Date dateFormat= new Date(Long.valueOf(dateStr));
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String finalDate = formatter.format(dateFormat);

        if (conversation.getRead() == 0) {
            holder.seenFlag.setAlpha(Float.valueOf("1.0"));
        } else {
            holder.seenFlag.setAlpha(Float.valueOf("0.0"));
        }

        String contactName = SmsHelper.getContactName(messaging, conversation.getAddress());
        if (contactName == null || contactName.equals("")) {
            contactName = conversation.getAddress();
        }
        holder.tv1.setText(contactName);
        holder.tv2.setText(finalDate);
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

        holder.itemView.setOnClickListener(view -> {
            if (messaging.isInActionMode) {
                CheckBox cb = (CheckBox) view.findViewById(R.id.cb);
                messaging.prepareSelection(cb, position);
            } else {
                messaging.isAllSelected = false;
                Intent intent = new Intent(messaging, Chat.class);
                intent.putExtra("ADDRESS", conversation.getAddress());
                messaging.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder{
        TextView tv1, tv2, tv3;
        CheckBox checkBox;
        ImageView personBtn;
        ImageView seenFlag;

        Messaging messaging;
        MessageViewHolder(View itemView, Messaging messaging) {
            super(itemView);
            tv1 = (TextView) itemView.findViewById(R.id.tv1);
            tv2 = (TextView) itemView.findViewById(R.id.tv2);
            tv3 = (TextView) itemView.findViewById(R.id.tv3);
            checkBox = (CheckBox) itemView.findViewById(R.id.cb);
            personBtn = (ImageView) itemView.findViewById(R.id.person_btn);
            seenFlag = (ImageView) itemView.findViewById(R.id.seen_flag);
            this.messaging = messaging;

            itemView.setOnLongClickListener(messaging);
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

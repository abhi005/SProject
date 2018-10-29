package Helper;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jarvis.sproject.Chat;
import com.example.jarvis.sproject.R;

import java.util.ArrayList;

import Model.Message;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageHolder> {

    private ArrayList<Message> messages;
    private Chat chat;

    public ChatAdapter(ArrayList<Message> messages, Chat context) {
        this.messages = messages;
        this.chat = context;
    }
    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_item_layout, parent, false);
        return new ChatAdapter.MessageHolder(itemView, chat);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        Message message = messages.get(position);
        RelativeLayout layout = holder.container;
        holder.messageText.setText(message.getMessageText());
        String temp = message.getDate() + " | " + message.getTime();
        holder.timeDate.setText(temp);
        String sender = message.getSender();

        if (sender.equals("self")) {
            RelativeLayout.LayoutParams lp1 = (RelativeLayout.LayoutParams) holder.messageText.getLayoutParams();
            RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) holder.timeDate.getLayoutParams();

            lp1.addRule(RelativeLayout.ALIGN_PARENT_END);
            lp2.addRule(RelativeLayout.ALIGN_PARENT_END);
        } else {
            RelativeLayout.LayoutParams lp1 = (RelativeLayout.LayoutParams) holder.messageText.getLayoutParams();
            RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) holder.timeDate.getLayoutParams();

            lp1.addRule(RelativeLayout.ALIGN_PARENT_START);

            Drawable bg = chat.getDrawable(R.drawable.chat_edittext_blue_background);
            holder.messageText.setBackground(bg);
            holder.messageText.setPadding(32, 32, 32, 32);
            holder.messageText.setTextColor(chat.getResources().getColor(R.color.white));
            lp2.addRule(RelativeLayout.ALIGN_PARENT_START);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MessageHolder extends RecyclerView.ViewHolder {
        RelativeLayout container;
        TextView messageText;
        TextView timeDate;

        Chat chat;

        public MessageHolder(View itemView, Chat chat) {
            super(itemView);

            this.container = (RelativeLayout) itemView.findViewById(R.id.chat_message_item_container);
            this.messageText = (TextView) itemView.findViewById(R.id.chat_item_message_text);
            this.timeDate = (TextView) itemView.findViewById(R.id.chat_item_message_date_time);
            this.chat = chat;

            itemView.setOnLongClickListener(chat);
        }
    }
}

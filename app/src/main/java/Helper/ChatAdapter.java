package Helper;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jarvis.sproject.Chat;
import com.example.jarvis.sproject.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import Model.LocalSms;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageHolder> {

    private List<LocalSms> smses;
    private Chat chat;

    public ChatAdapter(List<LocalSms> smses, Chat context) {
        this.smses = smses;
        this.chat = context;
    }
    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_chat, parent, false);
        return new ChatAdapter.MessageHolder(itemView, chat);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        LocalSms sms = smses.get(position);
        RelativeLayout layout = holder.container;
        holder.messageText.setText(sms.getBody());
        String temp = sms.getDate();
        Date dateFormat= new Date(Long.valueOf(temp));
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String finalDate = formatter.format(dateFormat);
        holder.timeDate.setText(finalDate);
        int type = sms.getType();

        if (type == 2) {
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
            holder.messageText.setPadding(32, 24, 32, 24);
            holder.messageText.setTextColor(chat.getResources().getColor(R.color.white));
            lp2.addRule(RelativeLayout.ALIGN_PARENT_START);
        }

        holder.itemView.setOnLongClickListener(view -> {
            ClipboardManager clipboardManager = (ClipboardManager) chat.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("copied_sms", sms.getBody());
            assert clipboardManager != null;
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(chat, "copied!", Toast.LENGTH_SHORT).show();
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return smses.size();
    }

    class MessageHolder extends RecyclerView.ViewHolder {
        RelativeLayout container;
        TextView messageText;
        TextView timeDate;

        Chat chat;

        MessageHolder(View itemView, Chat chat) {
            super(itemView);

            this.container = (RelativeLayout) itemView.findViewById(R.id.chat_message_item_container);
            this.messageText = (TextView) itemView.findViewById(R.id.chat_item_message_text);
            this.timeDate = (TextView) itemView.findViewById(R.id.chat_item_message_date_time);
            this.chat = chat;
        }
    }
}

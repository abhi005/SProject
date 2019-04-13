package Helper;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jarvis.sproject.CallLogs;
import com.example.jarvis.sproject.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import Model.LocalCall;

public class CallLogsAdapter extends RecyclerView.Adapter<CallLogsAdapter.MyViewHolder> {

    private List<LocalCall> callList;
    private CallLogs activity;

    public CallLogsAdapter(List<LocalCall> localCallHistory, CallLogs context) {
        this.callList = localCallHistory;
        this.activity = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_call_logs, parent, false);
        return new MyViewHolder(itemView, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        LocalCall localCall = callList.get(position);
        String contactName = SmsHelper.getContactName(activity, localCall.getNumber());
        if (contactName == null || contactName.equals("")) {
            holder.tv1.setText(localCall.getNumber());
        } else {
            holder.tv1.setText(contactName);
        }

        String dateStr = localCall.getDate();
        Date dateFormat = new Date(Long.valueOf(dateStr));
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String finalDate = formatter.format(dateFormat);

        String type = "";
        switch (localCall.getType()) {
            case 1:
                holder.typeIcon.setImageResource(R.drawable.call_dialed_24dp);
                type = "Outgoing";
                break;
            case 2:
                holder.typeIcon.setImageResource(R.drawable.call_received_24dp);
                type = "Incoming";
                break;
            case 3:
                holder.typeIcon.setImageResource(R.drawable.call_missed_24dp);
                type = "Missed";
                break;
            default:
                holder.typeIcon.setImageResource(R.drawable.call_received_24dp);
                break;
        }

        String temp = finalDate + " | " + type + " : " + FileHelper.getReadableVideoDuration(Long.parseLong(localCall.getDuration()) * 1000);
        holder.tv3.setText(temp);

        //checking for action mode status
        if (!activity.isInActionMode) {
            holder.checkBox.setVisibility(View.GONE);
            holder.checkBox.setChecked(false);
        } else {
            holder.checkBox.setVisibility(View.VISIBLE);
            if (activity.isAllSelected) {
                holder.checkBox.setChecked(true);
            } else {
                holder.checkBox.setChecked(false);
            }
        }


        holder.itemView.setOnClickListener(v -> {
            if (activity.isInActionMode) {
                CheckBox cb = v.findViewById(R.id.call_history_item_cb);
                activity.prepareSelection(cb, position);
            } else {
                CallLogsHelper.makeCall(activity, localCall.getNumber());
            }
        });

    }

    @Override
    public int getItemCount() {
        return callList.size();
    }

    public void filterList(List<LocalCall> filteredList) {
        callList = filteredList;
        notifyDataSetChanged();
    }

    public void updateAdapter(List<LocalCall> list) {
        callList.clear();
        callList.addAll(list);
        notifyDataSetChanged();
    }

    public void deleteItems(List<LocalCall> list) {
        for (LocalCall c : list) {
            int id = c.getId();
            CallLogsHelper.deleteCallLog(activity, id);
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv1, tv3;
        ImageView typeIcon;
        CheckBox checkBox;
        ImageView personBtn;
        CallLogs callLogs;

        MyViewHolder(View view, CallLogs activity) {
            super(view);
            tv1 = view.findViewById(R.id.call_history_item_tv1);
            tv3 = view.findViewById(R.id.call_history_item_tv3);
            typeIcon = view.findViewById(R.id.call_history_type_icon);
            checkBox = view.findViewById(R.id.call_history_item_cb);
            personBtn = view.findViewById(R.id.call_history_item_person_btn);
            this.callLogs = activity;

            view.setOnLongClickListener(activity);
        }
    }
}

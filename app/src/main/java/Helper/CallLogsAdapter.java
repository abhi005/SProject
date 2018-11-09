package Helper;

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

import java.util.List;

import Model.Call;

public class CallLogsAdapter extends RecyclerView.Adapter<CallLogsAdapter.MyViewHolder> {

    private List<Call> callHistory;
    private CallLogs callLogs;

    public CallLogsAdapter(List<Call> callHistory, CallLogs context) {
        this.callHistory = callHistory;
        this.callLogs = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_call_logs, parent, false);
        return new MyViewHolder(itemView, callLogs);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Call call = callHistory.get(position);
        if(call.getName().equals("")) {
            holder.tv1.setText(call.getContact());
            holder.tv2.setText(call.getLocation());
        } else {
            holder.tv1.setText(call.getName());
            String temp = call.getContact() + " " + call.getLocation();
            holder.tv2.setText(temp);
        }

        String temp = call.getTime() + " " + call.getType() + ":" + call.getDuration();
        holder.tv3.setText(temp);
        switch (call.getType()) {
            case "Incoming":
                holder.typeIcon.setImageResource(R.drawable.call_received_24dp);
                break;
            case "Outgoing":
                holder.typeIcon.setImageResource(R.drawable.call_dialed_24dp);
                break;
            case "Missed":
                holder.typeIcon.setImageResource(R.drawable.call_missed_24dp);
                break;
            default:
                holder.typeIcon.setImageResource(R.drawable.call_received_24dp);
                break;
        }

        //checking for action mode status
        if(!callLogs.isInActionMode) {
            holder.checkBox.setVisibility(View.GONE);
            holder.checkBox.setChecked(false);
        } else {
            holder.checkBox.setVisibility(View.VISIBLE);
            if (callLogs.isAllSelected) {
                holder.checkBox.setChecked(true);
            } else {
                holder.checkBox.setChecked(false);
            }
        }

    }

    @Override
    public int getItemCount() {
        return callHistory.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv1, tv2, tv3;
        ImageView typeIcon;
        CheckBox checkBox;
        ImageView personBtn;
        CallLogs callLogs;

        MyViewHolder(View view, CallLogs activity) {
            super(view);
            tv1 = (TextView) view.findViewById(R.id.call_history_item_tv1);
            tv2 = (TextView) view.findViewById(R.id.call_history_item_tv2);
            tv3 = (TextView) view.findViewById(R.id.call_history_item_tv3);
            typeIcon = (ImageView) view.findViewById(R.id.call_history_type_icon);
            checkBox = (CheckBox) view.findViewById(R.id.call_history_item_cb);
            personBtn = (ImageView) view.findViewById(R.id.call_history_item_person_btn);
            this.callLogs = activity;

            view.setOnLongClickListener(callLogs);

            view.setOnLongClickListener(activity);
            view.setOnClickListener(v -> {
                if(callLogs.isInActionMode) {
                    CheckBox cb = v.findViewById(R.id.call_history_item_cb);
                    activity.prepareSelection(cb, getAdapterPosition());
                } else {

                }
            });
        }
    }

    public void filterList(List<Call> filteredList) {
        callHistory = filteredList;
        notifyDataSetChanged();
    }

    public void updateAdapter(List<Call> list) {
        for(Call c : list) {
            callHistory.remove(c);
        }
        notifyDataSetChanged();
    }
}

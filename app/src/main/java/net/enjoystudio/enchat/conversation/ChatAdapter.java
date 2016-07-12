package net.enjoystudio.enchat.conversation;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.enjoystudio.enchat.C;
import net.enjoystudio.enchat.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rizki on 11/07/2016.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private ArrayList<HashMap<String,String>> chatList;
    private Context context;
    private SharedPreferences sp;
    public ChatAdapter (ArrayList<HashMap<String,String>> chatList){
        this.chatList = chatList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.item_chat,parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        sp = context.getSharedPreferences(C.SESSION,Context.MODE_PRIVATE);
        int sizeInDP = 40;
        int marginInDp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, sizeInDP, context.getResources()
                        .getDisplayMetrics());
        HashMap<String,String> chatItem = chatList.get(position);
        String sender = chatItem.get(C.SENDER_ID);
        String myId = sp.getString(C.USER_ID,"0");
        String content = chatItem.get(C.CONTENT);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if(sender.equals(myId)){
            lp.gravity = Gravity.RIGHT;
            lp.leftMargin = marginInDp;
            holder.container.setLayoutParams(lp);
            holder.container.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.backgroud_right));
        }
        else {
            lp.gravity = Gravity.LEFT;
            lp.rightMargin = marginInDp;
            holder.container.setLayoutParams(lp);
            holder.container.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.background_left));
        }
        holder.chatText.setText(content);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout container;
        TextView chatText;
        public ViewHolder(View itemView) {
            super(itemView);
            chatText = (TextView) itemView.findViewById(R.id.chat);
            container = (LinearLayout) itemView.findViewById(R.id.holder);
        }
    }
}

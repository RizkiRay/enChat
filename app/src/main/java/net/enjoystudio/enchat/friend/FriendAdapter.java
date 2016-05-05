package net.enjoystudio.enchat.friend;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.enjoystudio.enchat.C;
import net.enjoystudio.enchat.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rizki on 5/4/2016.
 */
public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    private ArrayList<HashMap<String, String>> dataList;
    private Context context;

    public FriendAdapter(ArrayList<HashMap<String, String>> dataList) {
        this.dataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.item_conversation,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final HashMap<String,String> data = dataList.get(position);
        Picasso.with(context).load(Integer.parseInt(data.get(C.PHOTO))).centerCrop().fit()
                .into(holder.photo);
        holder.name.setText(data.get(C.NAME));
        holder.status.setText(data.get(C.STATUS));
        holder.photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ProfileActivity.class);
                i.putExtra(C.PHOTO, data.get(C.PHOTO));
                i.putExtra(C.NAME,data.get(C.NAME));
                i.putExtra(C.STATUS,data.get(C.STATUS));
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView photo;
        private final TextView name;
        private final TextView status;
        private final RelativeLayout placeholder;

        public ViewHolder(View itemView) {
            super(itemView);
            photo = (ImageView) itemView.findViewById(R.id.conv_image);
            name = (TextView) itemView.findViewById(R.id.txt_name);
            status = (TextView) itemView.findViewById(R.id.txt_lastchat);
            placeholder = (RelativeLayout) itemView.findViewById(R.id.placeholder);
            TextView date = (TextView) itemView.findViewById(R.id.date);
            date.setVisibility(View.GONE);
        }
    }
}

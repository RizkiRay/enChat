package net.enjoystudio.enchat.friend;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        HashMap<String,String> data = dataList.get(position);
        Picasso.with(context).load(Integer.parseInt(data.get(C.PHOTO))).centerCrop().fit()
                .into(holder.photo);
        holder.name.setText(data.get(C.NAME));
        holder.status.setText(data.get(C.STATUS));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView photo;
        private final TextView name;
        private final TextView status;

        public ViewHolder(View itemView) {
            super(itemView);
            photo = (ImageView) itemView.findViewById(R.id.conv_image);
            name = (TextView) itemView.findViewById(R.id.txt_name);
            status = (TextView) itemView.findViewById(R.id.txt_lastchat);
            TextView date = (TextView) itemView.findViewById(R.id.date);
            date.setVisibility(View.GONE);
        }
    }
}

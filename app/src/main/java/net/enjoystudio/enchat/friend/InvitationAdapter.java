package net.enjoystudio.enchat.friend;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import net.enjoystudio.enchat.C;
import net.enjoystudio.enchat.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rizki on 29/06/2016.
 */
public class InvitationAdapter extends RecyclerView.Adapter<InvitationAdapter.ViewHolder> {
    private ArrayList<HashMap<String, String>> arrayList;
    private Context context;
    private SharedPreferences sp;

    public InvitationAdapter(ArrayList<HashMap<String, String>> arrayList) {
        this.arrayList = arrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.item_conversation, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        sp = context.getSharedPreferences(C.SESSION,Context.MODE_PRIVATE);
        HashMap<String,String> data = arrayList.get(position);
        final String id1 = data.get(C.USER_ID);
        final String nama = data.get(C.NAME);
        holder.name.setText(nama);
        holder.status.setText(data.get(C.STATUS));
        Picasso.with(context).load(C.API_IMAGE + C.PROFILE_PICTURE + "/" + data.get(C.PHOTO))
                .fit().centerInside().into(holder.pict);
        holder.placeholder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(nama + " wants to be your friend")
                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.i("CEK","user1 = "+ id1 + "user2 = " + sp.getString(C.USER_ID, ""));
                                StringRequest sr = new StringRequest(Request.Method.GET
                                        , C.API_ACCEPT_FRIENDS + "user_1=" + id1 + "&user_2=" + sp.getString(C.USER_ID, ""),
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                Log.i("CEK",response);
                                                if(response.equals("s")){
                                                    arrayList.remove(position);
                                                    notifyItemRemoved(position);
                                                    if(arrayList.size()==0){
                                                        ((InviteActivity)context).finish();
                                                    }
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(context, "Check your connection", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                Volley.newRequestQueue(context).add(sr);
                            }
                        })
                        .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                StringRequest sr = new StringRequest(Request.Method.GET
                                    , C.API_REJECT_FRIENDS + "user_1=" + id1 + "&user_2=" + sp.getString(C.USER_ID, ""),
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Log.i("CEK",response);
                                            if(response.equals("s")){
                                                arrayList.remove(position);
                                                notifyItemRemoved(position);
                                                if(arrayList.size()==0){
                                                    ((InviteActivity)context).finish();
                                                }
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(context, "Check your connection", Toast.LENGTH_SHORT).show();
                                }
                            });
                                Volley.newRequestQueue(context).add(sr);
                            }
                        })
                        .setNeutralButton("Cancel", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final RelativeLayout placeholder;
        private final TextView status;
        private final TextView name;
        private final ImageView pict;

        public ViewHolder(View itemView) {
            super(itemView);
            pict = (ImageView) itemView.findViewById(R.id.conv_image);
            name = (TextView) itemView.findViewById(R.id.txt_name);
            status = (TextView) itemView.findViewById(R.id.txt_lastchat);
            placeholder = (RelativeLayout) itemView.findViewById(R.id.placeholder);
            TextView date = (TextView) itemView.findViewById(R.id.date);
            date.setVisibility(View.GONE);
        }
    }
}

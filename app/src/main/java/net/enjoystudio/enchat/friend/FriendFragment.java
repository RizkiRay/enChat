package net.enjoystudio.enchat.friend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import net.enjoystudio.enchat.C;
import net.enjoystudio.enchat.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by rizki on 5/4/2016.
 */
public class FriendFragment extends Fragment {
    private RecyclerView recyclerView;
    private ArrayList<HashMap<String,String>> dataList, iv, dataAdd;
    private SharedPreferences sp;
    private ProgressBar progressBar;
    private RelativeLayout rl;
    private TextView txtInvite, emptyMessage;
//    private Button  btnDetails;
    private int invitation=0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_friend,container,false);
        progressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerview);
        rl = (RelativeLayout) v.findViewById(R.id.invite);
        emptyMessage = (TextView) v.findViewById(R.id.empty);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        sp = getActivity().getSharedPreferences(C.SESSION, Context.MODE_PRIVATE);
        txtInvite = (TextView) v.findViewById(R.id.txt_invite);
        dataList = new ArrayList<>();
        iv = new ArrayList<>();
        dataAdd = new ArrayList<>();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Friends");
        setHasOptionsMenu(true);
        txtInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("CEK",iv.toString());
                InviteActivity.datalist = iv;
                startActivity(new Intent(getActivity(),InviteActivity.class));
            }
        });
        emptyMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoAddFriend();
            }
        });
        return v;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.base_app, menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        getFriendList();
    }

    private void getFriendList(){
        dataList.clear();
        iv.clear();
        progressBar.setVisibility(View.VISIBLE);
        JsonArrayRequest jar = new JsonArrayRequest(Request.Method.POST, C.API_GET_FRIENDS
                + sp.getString(C.USER_ID, "0"), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                progressBar.setVisibility(View.GONE);
                for (int i = 0; i<response.length();i++){
                    HashMap<String,String> data = new HashMap<>();
                    try {
                        JSONObject jobj = response.getJSONObject(i);
                        JSONObject user1 = jobj.getJSONObject(C.USER_1);
                        JSONObject user2 = jobj.getJSONObject(C.USER_2);
                        int status = jobj.getInt(C.STATUS);
                        int user1Id = user1.getInt(C.USER_ID);
                        int user2Id = user2.getInt(C.USER_ID);
                        if(status == 1){
                            //ini data yang nge add
                            if(sp.getString(C.USER_ID,"0").equals(user2Id+"")){
                                invitation+=1;
                                HashMap<String,String> dataIv = new HashMap<>();
                                dataIv.put(C.PHOTO, user1.getString(C.PROFILE_PICTURE));
                                dataIv.put(C.USER_ID, user1.getString(C.USER_ID));
                                dataIv.put(C.NAME, user1.getString(C.NAME));
                                dataIv.put(C.STATUS, user1.getString(C.STATUS));
                                dataIv.put(C.PHONE, user1.getString(C.PHONE));
                                iv.add(dataIv);
                            }
                            else { //ini data yang kita yang add
                                HashMap<String,String> dataIv = new HashMap<>();
                                dataIv.put(C.PHOTO, user2.getString(C.PROFILE_PICTURE));
                                dataIv.put(C.USER_ID, user2.getString(C.USER_ID));
                                dataIv.put(C.NAME, user2.getString(C.NAME));
                                dataIv.put(C.STATUS, user2.getString(C.STATUS));
                                dataIv.put(C.PHONE, user2.getString(C.PHONE));
                                dataAdd.add(dataIv);
                            }
                        }
                        else if(status == 2) {
                            //ini kalau posisi kita sebagai user 1
                            if (sp.getString(C.USER_ID, "0").equals(user1Id + "")) {
                                data.put(C.PHOTO, user2.getString(C.PROFILE_PICTURE));
                                data.put(C.NAME, user2.getString(C.NAME));
                                data.put(C.USER_ID, user2.getString(C.USER_ID));
                                data.put(C.STATUS, user2.getString(C.STATUS));
                                data.put(C.PHONE, user2.getString(C.PHONE));
                            }
                            //ini kalau posisi kita sebagai user 2
                            else if (sp.getString(C.USER_ID, "0").equals(user2Id + "")) {
                                data.put(C.PHOTO, user1.getString(C.PROFILE_PICTURE));
                                data.put(C.NAME, user1.getString(C.NAME));
                                data.put(C.USER_ID, user1.getString(C.USER_ID));
                                data.put(C.STATUS, user1.getString(C.STATUS));
                                data.put(C.PHONE, user1.getString(C.PHONE));
                            }

                            dataList.add(data);
                        }
                    } catch (JSONException e) {
                        Log.i("CEKFRIEND", e.toString());
                    }
                }
                if(invitation>0){
                    emptyMessage.setVisibility(View.GONE);
                    rl.setVisibility(View.VISIBLE);
                    txtInvite.setText(invitation + " people want to be your friend");
                    invitation = 0;
                }
                else {
                    rl.setVisibility(View.GONE);
                    if (dataList.size()==0){
                        emptyMessage.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                    else {
                        emptyMessage.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                }
                Collections.sort(dataList,new Comparator<HashMap<String,String>>() {
                            public int compare(HashMap<String, String> mapping1, HashMap<String, String> mapping2) {
                                return mapping1.get(C.NAME).compareTo(mapping2.get(C.NAME));
                            }
                        });
                 FriendAdapter adapter = new FriendAdapter(dataList);
                 recyclerView.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Log.i("CEKFRIEND", error.toString());
            }
        });
        jar.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getActivity()).add(jar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_add){
            gotoAddFriend();
        }
        return true;
    }
    private void gotoAddFriend(){
        AddFriend.dataList = dataList;
        AddFriend.dataAdd = dataAdd;
        AddFriend.dataConfirm = iv;
        startActivity(new Intent(getActivity(),AddFriend.class));
    }
}

package net.enjoystudio.enchat.conversation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.enjoystudio.enchat.C;
import net.enjoystudio.enchat.R;
import net.enjoystudio.enchat.friend.FriendFragment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rizki on 5/4/2016.
 */
public class ConversationFragment extends Fragment {
    private RecyclerView recylerView;
    private ArrayList<HashMap<String,String>> dataList;
    private ConversationAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_conversation,container,false);
        recylerView = (RecyclerView) v.findViewById(R.id.recyclerview);
        recylerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        TextView emptyMessage = (TextView) v.findViewById(R.id.empty);
        emptyMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container,new FriendFragment()).commit();
            }
        });
        getConv();
        if(dataList==null){
            dataList = new ArrayList<>();
            emptyMessage.setVisibility(View.VISIBLE);
            recylerView.setVisibility(View.GONE);
        }
        else {
            emptyMessage.setVisibility(View.GONE);
            recylerView.setVisibility(View.VISIBLE);
        }
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Conversations");
//        HashMap<String,String> data = new HashMap<>();
//        data.put(C.PHOTO,Integer.toString(R.drawable.john));
//        data.put(C.NAME,"John");
//        data.put(C.CHAT,"Hi how are you ? ");
//        data.put(C.DATE, "21:30");
//        dataList.add(data);
//        data = new HashMap<>();
//        data.put(C.PHOTO,Integer.toString(R.drawable.maryjane));
//        data.put(C.NAME,"Mary Jane");
//        data.put(C.CHAT,"Hi brotheerr ");
//        data.put(C.DATE, "21:35");
//        dataList.add(data);
        adapter = new ConversationAdapter(dataList);
        recylerView.setAdapter(adapter);
        setHasOptionsMenu(true);
        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(receiver, new IntentFilter(C.UPDATE_CONVERSATION));
        return v;
    }
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getConv();
            adapter = new ConversationAdapter(dataList);
            recylerView.setAdapter(adapter);
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
    }

    private void getConv(){
        Gson gson = new Gson();
        SharedPreferences sp = getActivity().getSharedPreferences(C.SESSION, Context.MODE_PRIVATE);
        Type type = new TypeToken<ArrayList<HashMap<String,String>>>(){}.getType();
        dataList = gson.fromJson(sp.getString(C.CONVERSATION,""),type);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.base_app,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_add){
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                    new FriendFragment()).commit();
        }
        return true;
    }
}

package net.enjoystudio.enchat.friend;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.enjoystudio.enchat.C;
import net.enjoystudio.enchat.R;
import net.enjoystudio.enchat.conversation.ConversationAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rizki on 5/4/2016.
 */
public class FriendFragment extends Fragment {
    private RecyclerView recyclerView;
    private ArrayList<HashMap<String,String>> dataList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_friend,container,false);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dataList = new ArrayList<>();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Friends");
        HashMap<String,String> data = new HashMap<>();
        data.put(C.PHOTO,Integer.toString(R.drawable.john));
        data.put(C.NAME,"John");
        data.put(C.STATUS,"Yeahhh !!!");
        dataList.add(data);
        data = new HashMap<>();
        data.put(C.PHOTO,Integer.toString(R.drawable.maryjane));
        data.put(C.NAME,"Mary Jane");
        data.put(C.STATUS,"Good Morning !!!");
        dataList.add(data);
        data = new HashMap<>();
        data.put(C.PHOTO,Integer.toString(R.drawable.zach));
        data.put(C.NAME,"Zach King");
        data.put(C.STATUS,"Let's do the magic");
        dataList.add(data);
        data = new HashMap<>();
        data.put(C.PHOTO,Integer.toString(R.drawable.harry));
        data.put(C.NAME,"Harry Potter");
        data.put(C.STATUS,"White Magic");
        dataList.add(data);
        data = new HashMap<>();
        data.put(C.PHOTO,Integer.toString(R.drawable.hermione));
        data.put(C.NAME,"Hermione");
        data.put(C.STATUS,"Hi i'm Hermione");
        dataList.add(data);
        data = new HashMap<>();
        data.put(C.PHOTO,Integer.toString(R.drawable.ron));
        data.put(C.NAME,"Ron Weasley");
        data.put(C.STATUS,"i'm Ron");
        dataList.add(data);
        FriendAdapter adapter = new FriendAdapter(dataList);
        recyclerView.setAdapter(adapter);
        return v;
    }
}

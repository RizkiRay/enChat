package net.enjoystudio.enchat.conversation;

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

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rizki on 5/4/2016.
 */
public class ConversationFragment extends Fragment {
    private RecyclerView recylerView;
    private ArrayList<HashMap<String,String>> dataList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_conversation,container,false);
        recylerView = (RecyclerView) v.findViewById(R.id.recyclerview);
        recylerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        dataList = new ArrayList<>();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Conversations");
        HashMap<String,String> data = new HashMap<>();
        data.put(C.PHOTO,Integer.toString(R.drawable.john));
        data.put(C.NAME,"John");
        data.put(C.CHAT,"Hi how are you ? ");
        data.put(C.DATE, "21:30");
        dataList.add(data);
        data = new HashMap<>();
        data.put(C.PHOTO,Integer.toString(R.drawable.maryjane));
        data.put(C.NAME,"Mary Jane");
        data.put(C.CHAT,"Hi brotheerr ");
        data.put(C.DATE, "21:35");
        dataList.add(data);
        Log.i("CEK","Jalan");
        ConversationAdapter adapter = new ConversationAdapter(dataList);
        recylerView.setAdapter(adapter);
        return v;
    }
}

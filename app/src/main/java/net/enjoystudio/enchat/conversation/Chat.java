package net.enjoystudio.enchat.conversation;

import android.content.SharedPreferences;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import net.enjoystudio.enchat.C;
import net.enjoystudio.enchat.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Chat extends AppCompatActivity {

    private EditText editChat;
    private TextView textName;
    private TextView textStatus;
    private String id, myId;
    private SharedPreferences sp;
    private ArrayList<HashMap<String, String>> chatList;
    private RecyclerView chatHolder;
    private ImageView buttonSend;
    private ChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        String name = getIntent().getStringExtra(C.NAME);
        String status = getIntent().getStringExtra(C.STATUS);
        sp = getSharedPreferences(C.SESSION, MODE_PRIVATE);
        id = getIntent().getStringExtra(C.USER_ID);
        myId = sp.getString(C.USER_ID, "0");
        chatList = new ArrayList<>();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        buttonSend = (ImageView) findViewById(R.id.btn_send);
        editChat = (EditText) findViewById(R.id.edit_chat);
        textName = (TextView) findViewById(R.id.text_name);
        textStatus = (TextView) findViewById(R.id.text_status);
        chatHolder = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        chatHolder.setLayoutManager(manager);
        adapter = new ChatAdapter(chatList);
        chatHolder.setAdapter(adapter);
        chatHolder.scrollToPosition(chatList.size() - 1);
        textName.setText(name);
        textStatus.setText(status);
        editChat.setSingleLine(true);
        editChat.setMaxLines(5);
        editChat.setHorizontallyScrolling(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getMessage();
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });
        editChat.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEND)
                    send();
                return true;
            }
        });
    }

    private void send() {
        if (!editChat.getText().toString().equals("")) {
            String konten = editChat.getText().toString();
            HashMap<String, String> chat = new HashMap<>();
            chat.put(C.SENDER_ID, myId);
            chat.put(C.RECEIVER_ID, id);
            chat.put(C.CONTENT, konten);
            chat.put(C.CREATED_AT, "");
            chatList.add(chat);
            adapter.notifyItemInserted(chatList.size() - 1);
            chatHolder.scrollToPosition(chatList.size() - 1);
            editChat.setText("");
        }
    }

    private void getMessage() {
        StringRequest sr = new StringRequest(Request.Method.POST, C.API_GET_MESSAGE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    if (response.trim().equals("k")) {

                    } else {
                        JSONArray list = new JSONArray(response);
                        for (int i = 0; i < list.length(); i++) {
                            HashMap<String, String> chat = new HashMap<>();
                            JSONObject data = list.getJSONObject(i);
                            chat.put(C.SENDER_ID, data.getString(C.SENDER_ID));
                            chat.put(C.RECEIVER_ID, data.getString(C.RECEIVER_ID));
                            chat.put(C.CONTENT, data.getString(C.CONTENT));
                            chat.put(C.CREATED_AT, data.getString(C.CREATED_AT));
                            chatList.add(chat);
                            adapter.notifyItemInserted(chatList.size() - 1);
                        }
                    }
                } catch (JSONException e) {

                    Log.i("CEK-CHAT-JSON", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("CEK-CHAT-VOLLEY", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put(C.USER_1, myId);
                params.put(C.USER_2, id);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(sr);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}

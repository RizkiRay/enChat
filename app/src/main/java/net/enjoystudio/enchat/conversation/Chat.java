package net.enjoystudio.enchat.conversation;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.enjoystudio.enchat.AES;
import net.enjoystudio.enchat.BaseAppActivity;
import net.enjoystudio.enchat.BuildConfig;
import net.enjoystudio.enchat.C;
import net.enjoystudio.enchat.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Chat extends AppCompatActivity {

    private EditText editChat;
    private TextView textName;
    private TextView textStatus;
    private String id, myId, cID;
    private SharedPreferences sp;
    private ArrayList<HashMap<String, String>> chatList;
    private RecyclerView chatHolder;
    private ImageView buttonSend;
    private ChatAdapter adapter;
    boolean isGenerated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        String name = getIntent().getStringExtra(C.NAME);
        String status = getIntent().getStringExtra(C.STATUS);
        sp = getSharedPreferences(C.SESSION, MODE_PRIVATE);
        id = getIntent().getStringExtra(C.USER_ID);
        Log.i("CEKSP", C.CONVERSATION + id);
        Log.i("CEKSP", sp.getString(C.CONVERSATION + id,""));
        if (!sp.getString(C.CONVERSATION + id,"").equals("")){
            isGenerated = true;
            cID = sp.getString(C.CONVERSATION + id, "");
            Log.i("CEKKEYtrue", cID);
        }
        else {
            isGenerated = false;
            cID = AES.randomString();
            Log.i("CEKKEYfalse", cID);
        }
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
        LocalBroadcastManager.getInstance(this).
                registerReceiver(broadCastReceiver, new IntentFilter(C.UPDATE_MESSAGE));
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
                if (actionId == EditorInfo.IME_ACTION_SEND)
                    send();
                return true;
            }
        });
    }

    private BroadcastReceiver broadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            HashMap<String, String> chat = new HashMap<>();
            chat.put(C.SENDER_ID, id);
            chat.put(C.RECEIVER_ID, myId);
            chat.put(C.CONTENT, intent.getExtras().getString(C.CONTENT));
            chatList.add(chat);
            adapter.notifyItemInserted(chatList.size() - 1);
            chatHolder.scrollToPosition(chatList.size() - 1);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        sp.edit().putString(C.ISACTIVE, "1;" + id).apply();
    }

    @Override
    protected void onStop() {
        super.onStop();
        sp.edit().putString(C.ISACTIVE, "0;" + id).apply();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadCastReceiver);
    }

    private void send() {
        if (!editChat.getText().toString().equals("")) {
            String konten = editChat.getText().toString();
            HashMap<String, String> chat = new HashMap<>();
            chat.put(C.SENDER_ID, myId);
            chat.put(C.RECEIVER_ID, id);
            chat.put(C.CONTENT, konten);
            chatList.add(chat);
            adapter.notifyItemInserted(chatList.size() - 1);
            chatHolder.scrollToPosition(chatList.size() - 1);
            editChat.setText("");
            sendMessage(konten);
        }

    }

    private void sendMessage(final String message) {

        StringRequest sr = new StringRequest(Request.Method.POST, C.API_SEND_MESSAGE + BuildConfig.KEY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("CEK", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("CEK", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String chipher = null;
                try {
                    chipher = AES.encryptAES(message, cID);
                    Log.i("CEK", chipher);
                    Log.i("CEK", AES.decryptAES(chipher, cID));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                HashMap<String, String> params = new HashMap<>();
                if (isGenerated) params.put(C.ID,"0");
                else {
                    params.put(C.ID, cID);
                    sp.edit().putString(C.CONVERSATION + id,cID).apply();
                }
                params.put(C.SENDER_ID, myId);
                params.put(C.RECEIVER_ID, id);
                params.put(C.CONTENT, chipher);
                return params;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(sr);
    }

    private void getMessage() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("loading...");
        pd.show();
        StringRequest sr = new StringRequest(Request.Method.POST, C.API_GET_MESSAGE + BuildConfig.KEY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("CEK",response);
                pd.dismiss();
                try {
//                    if (chatList.size()==0){
//                        isGenerated = false;
//                        cID = AES.randomString();
//                        Log.i("CEKKEY 0",cID);
//                    }
//                    else {
//                        isGenerated = true;
//                        cID = getID();
//                        Log.i("CEKKEY !0", cID);
//                    }
                    if (!response.trim().equals("k")) {
                        JSONArray list = new JSONArray(response);
                        for (int i = 0; i < list.length(); i++) {
                            HashMap<String, String> chat = new HashMap<>();
                            JSONObject data = list.getJSONObject(i);
                            chat.put(C.SENDER_ID, data.getString(C.SENDER_ID));
                            chat.put(C.RECEIVER_ID, data.getString(C.RECEIVER_ID));
                            chat.put(C.CONTENT, AES.decryptAES(data.getString(C.CONTENT), cID));
                            chat.put(C.CREATED_AT, data.getString(C.CREATED_AT));
                            chatList.add(chat);
                            adapter.notifyItemInserted(chatList.size() - 1);
                        }
                    }
                } catch (JSONException e) {

                    Log.i("CEK-CHAT-JSON", e.toString());
                }
                catch (Exception e){

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
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
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Gson gson = new Gson();
        ArrayList<HashMap<String, String>> cList;
        Type type = new TypeToken<ArrayList<HashMap<String, String>>>() {
        }.getType();
        cList = gson.fromJson(sp.getString(C.CONVERSATION, ""), type);
        if (cList == null) cList = new ArrayList<>();
        if (chatList.size() > 0) {
            int pos = getPos(getIntent().getExtras().getString(C.USER_ID));
            Log.i("CEK", pos + "");
            HashMap<String, String> conv = new HashMap<>();
            conv.put(C.ID, cID);
            conv.put(C.USER_ID, getIntent().getExtras().getString(C.USER_ID));
            conv.put(C.PHOTO, getIntent().getExtras().getString(C.PHOTO));
            conv.put(C.NAME, getIntent().getExtras().getString(C.NAME));
            conv.put(C.STATUS, getIntent().getExtras().getString(C.STATUS));
            conv.put(C.CHAT, chatList.get(chatList.size() - 1).get(C.CONTENT));
            if (pos != -1) {
                cList.remove(pos);
                cList.add(pos, conv);
                Log.i("CEK", chatList.size() + "");
            } else cList.add(conv);
            sp.edit().putString(C.CONVERSATION, gson.toJson(cList)).apply();
        }
        finish();
        startActivity(new Intent(this, BaseAppActivity.class));
    }

    private int getPos(String id) {
        Gson gson = new Gson();
        SharedPreferences sp = getSharedPreferences(C.SESSION, MODE_PRIVATE);
        String data = sp.getString(C.CONVERSATION, "");
        Type type = new TypeToken<ArrayList<HashMap<String, String>>>() {
        }.getType();
        ArrayList<HashMap<String, String>> cList = gson.fromJson(data, type);
        if (cList == null) cList = new ArrayList<>();
        for (int i = 0; i < cList.size(); i++) {
            HashMap<String, String> conv = cList.get(i);
            if (conv.get(C.USER_ID).equals(id)) {
                return i;
            }
        }
        return -1;
    }

//    private String getID() {
//        int pos = getPos(getIntent().getExtras().getString(C.USER_ID));
//        Gson gson = new Gson();
//        SharedPreferences sp = getSharedPreferences(C.SESSION, MODE_PRIVATE);
//        String data = sp.getString(C.CONVERSATION, "");
//        Type type = new TypeToken<ArrayList<HashMap<String, String>>>() {
//        }.getType();
//        ArrayList<HashMap<String, String>> cList = gson.fromJson(data, type);
//        HashMap<String, String> c = cList.get(pos);
//        return c.get(C.ID);
//    }
}

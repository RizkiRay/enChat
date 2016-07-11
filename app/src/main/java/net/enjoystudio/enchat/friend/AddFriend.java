package net.enjoystudio.enchat.friend;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import net.enjoystudio.enchat.C;
import net.enjoystudio.enchat.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddFriend extends AppCompatActivity {

    private Button addFriend;
    private ImageView search;
    private EditText editPhone;
    private TextView txtMessage;
    private CardView container;
    private ProgressBar pb;
    private CircleImageView pp;
    private TextView name;
    private TextView phone;
    private SharedPreferences sp;
    private boolean isFriend, isAdd, isConfirm, isMe;
    private String id;
    public static ArrayList<HashMap<String,String>> dataList, dataConfirm, dataAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Add Friend");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        search = (ImageView) findViewById(R.id.btn_search);
        editPhone = (EditText) findViewById(R.id.editNumber);
        txtMessage = (TextView) findViewById(R.id.mes);
        container = (CardView) findViewById(R.id.container);
        pb = (ProgressBar) findViewById(R.id.pb);
        pp = (CircleImageView) findViewById(R.id.picture);
        name = (TextView) findViewById(R.id.txt_name);
        phone = (TextView) findViewById(R.id.txt_phone);
        sp = getSharedPreferences(C.SESSION,MODE_PRIVATE);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = editPhone.getText().toString().trim();
                for (int i = 0; i < dataList.size(); i++) {
                    HashMap<String,String> data = dataList.get(i);
                    if(phoneNumber.equals(data.get(C.PHONE).trim())){
                        isFriend = true;
                        break;
                    }
                }
                for (int i = 0; i < dataAdd.size(); i++) {
                    HashMap<String,String> data = dataAdd.get(i);
                    if(phoneNumber.equals(data.get(C.PHONE).trim())){
                        isAdd = true;
                        break;
                    }
                }
                for (int i = 0; i < dataConfirm.size(); i++) {
                    HashMap<String,String> data = dataConfirm.get(i);
                    Log.i("CEK",phoneNumber + " = " + data.get(C.PHONE).trim());
                    if(phoneNumber.equals(data.get(C.PHONE).trim())){
                        isConfirm = true;
                        break;
                    }
                }
                if(phoneNumber.equals(sp.getString(C.PHONE,"0").trim())){
                    isMe = true;
                }
                getFriend(phoneNumber);
            }
        });

        addFriend = (Button) findViewById(R.id.btn_add);
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddFriend.this);
                builder.setMessage("Add " + name.getText() + " as friend ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addFriend();
                    }
                });
                builder.setNegativeButton("No", null);
                builder.create().show();
            }
        });
    }

    private void getFriend(final String strphone){
        Log.i("CEK",strphone);
        pb.setVisibility(View.VISIBLE);
        StringRequest sr = new StringRequest(Request.Method.GET, C.API_FIND_FRIENDS + strphone,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("k")){
                            container.setVisibility(View.GONE);
                            txtMessage.setVisibility(View.VISIBLE);
                            pb.setVisibility(View.GONE);
                        }
                        else {
                            pb.setVisibility(View.GONE);
                            container.setVisibility(View.VISIBLE);
                            txtMessage.setVisibility(View.GONE);
                            try {
                                JSONObject jobj = new JSONObject(response);
                                id = jobj.getString(C.USER_ID);
                                Picasso.with(AddFriend.this).load(C.API_IMAGE + C.PROFILE_PICTURE
                                        + "/" + jobj.getString(C.PROFILE_PICTURE))
                                        .fit().centerInside().into(pp);
                                name.setText(jobj.getString(C.NAME));
                                phone.setText(jobj.getString(C.PHONE));
                                if (isFriend){
                                    addFriend.setVisibility(View.VISIBLE);
                                    addFriend.setText("Already Your Friend");
                                    addFriend.setBackgroundColor(getResources().getColor(R.color.colorBackground));
                                    addFriend.setEnabled(false);
                                }
                                else if(isConfirm){
                                    addFriend.setVisibility(View.VISIBLE);
                                    addFriend.setBackgroundColor(getResources().getColor(R.color.colorBackground));
                                    addFriend.setText("Waiting Your Confirm");
                                    addFriend.setEnabled(false);
                                }
                                else if(isAdd){
                                    addFriend.setVisibility(View.VISIBLE);
                                    addFriend.setBackgroundColor(getResources().getColor(R.color.colorBackground));
                                    addFriend.setText("Waiting for confirmation");
                                    addFriend.setEnabled(false);
                                }
                                else if (isMe){
                                    addFriend.setVisibility(View.GONE);
                                }
                                else {
                                    addFriend.setVisibility(View.VISIBLE);
                                    addFriend.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                    addFriend.setText("Add Friend");
                                    addFriend.setEnabled(true);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            isFriend = false;
                            isAdd = false;
                            isConfirm = false;
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddFriend.this, "Check your network", Toast.LENGTH_SHORT).show();
                pb.setVisibility(View.GONE);
            }
        });
        Volley.newRequestQueue(this).add(sr);
    }
    private void addFriend(){
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("adding friend...");
        pd.show();
        StringRequest sr = new StringRequest(Request.Method.POST, C.API_ADD_FRIENDS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("s")){
                    Toast.makeText(AddFriend.this, "operation succesful", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    Toast.makeText(AddFriend.this, "operation failed", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddFriend.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<>();
                params.put(C.USER_1,sp.getString(C.USER_ID,"0"));
                params.put(C.USER_2, id);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(sr);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }
        return true;
    }
}

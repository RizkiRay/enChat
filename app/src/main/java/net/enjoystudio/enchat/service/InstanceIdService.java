package net.enjoystudio.enchat.service;

import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import net.enjoystudio.enchat.C;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rizki on 12/07/2016.
 */
public class InstanceIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        final String token = FirebaseInstanceId.getInstance().getToken();
        final SharedPreferences sp = getSharedPreferences(C.SESSION, MODE_PRIVATE);
        StringRequest sr = new StringRequest(Request.Method.POST, C.API_UPDATE_TOKEN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<>();
                params.put(C.TOKEN, token);
                params.put(C.USER_ID, sp.getString(C.USER_ID,"0"));
                Log.i("CEK",sp.getString(C.USER_ID,"0"));
                return params;
            }
        };
        Volley.newRequestQueue(this).add(sr);
    }
}

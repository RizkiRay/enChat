package net.enjoystudio.enchat.account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import net.enjoystudio.enchat.BaseAppActivity;
import net.enjoystudio.enchat.BuildConfig;
import net.enjoystudio.enchat.C;
import net.enjoystudio.enchat.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,
        TextView.OnEditorActionListener {

    private EditText editPass;
    private EditText editPhone;
    private TextInputLayout tilPass;
    private TextInputLayout tilPhone;
    private SharedPreferences sp;
    ProgressDialog pd;

    private Boolean checkPhoneInput() {
        String target = editPhone.getText().toString().trim();
        if (target.equals("")) {
            tilPhone.setError("can't be empty");
            return true;
//        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()) {
//            tilPhone.setError("not a valid email");
//            return true;
        } else {
            tilPhone.setError(null);
            tilPhone.setErrorEnabled(false);
            return false;
        }
    }

    private Boolean checkPassInput() {
        if (editPass.getText().toString().equals("")) {
            tilPass.setError("can't be empty");
            return true;
        } else if (editPass.getText().toString().length() < 8) {
            tilPass.setError("at least 8 characters");
            return true;
        } else {
            tilPass.setError(null);
            tilPass.setErrorEnabled(false);
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button login = (Button) findViewById(R.id.btn_login);
        TextView regist = (TextView) findViewById(R.id.btn_regist);
        editPhone = (EditText) findViewById(R.id.phone);
        editPass = (EditText) findViewById(R.id.password);
        tilPhone = (TextInputLayout) findViewById(R.id.til_phone);
        tilPass = (TextInputLayout) findViewById(R.id.til_password);

        editPhone.setOnEditorActionListener(this);
        editPass.setOnEditorActionListener(this);
        login.setOnClickListener(this);
        regist.setOnClickListener(this);

        sp = getSharedPreferences(C.SESSION, MODE_PRIVATE);
        pd = new ProgressDialog(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_login:
                boolean isPhoneInCorrect = checkPhoneInput();
                boolean isPassInCorrect = checkPassInput();
                boolean isCanContinue = !(isPhoneInCorrect || isPassInCorrect);
                if (isCanContinue) {
                pd.setMessage("Logging in...");
                pd.show();
                StringRequest sr = new StringRequest(Request.Method.POST, C.API_LOGIN + BuildConfig.KEY,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                pd.dismiss();
                                if (response.trim().equals("g")) {
                                    Toast.makeText(LoginActivity.this,
                                            "phone and password is not match",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    try {
                                        JSONObject jobj = new JSONObject(response);
                                        sp.edit().putString(C.USER_ID, jobj.getString(C.USER_ID)).apply();
                                        sp.edit().putString(C.NAME, jobj.getString(C.NAME)).apply();
                                        sp.edit().putString(C.STATUS, jobj.getString(C.STATUS)).apply();
                                        sp.edit().putString(C.PROFILE_PICTURE, jobj.getString(C.PROFILE_PICTURE)).apply();
                                        sp.edit().putString(C.PHONE, jobj.getString(C.PHONE)).apply();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    startActivity(new Intent(LoginActivity.this, BaseAppActivity.class));
                                    finish();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("CEK", error.toString());
                        pd.dismiss();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> params = new HashMap<>();
                        params.put(C.PHONE, editPhone.getText().toString().trim());
                        params.put(C.PASSWORD, editPass.getText().toString().trim());
                        return params;
                    }
                };
                Volley.newRequestQueue(this).add(sr);
                }
                //startActivity(new Intent(this,BaseAppActivity.class));
                break;
            case R.id.btn_regist:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        int id = v.getId();
        if (id == R.id.phone) {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                return checkPhoneInput();
            }
        }
        if (id == R.id.password) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                return checkPassInput();
            }
        }
        return false;
    }
}

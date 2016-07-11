package net.enjoystudio.enchat;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener,
        TextView.OnEditorActionListener {

    private EditText editPhone;
    private TextInputLayout tilPhone;
    private EditText editPass;
    private TextInputLayout tilPass;
    private Button submit;
    private EditText editPassConf;
    private TextInputLayout tilPassConf;
    private SharedPreferences sp;
    private ProgressDialog pd;


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

    private Boolean checkPassConf() {
        String pass = editPass.getText().toString().trim();
        String conf = editPassConf.getText().toString().trim();

        if (!conf.equals(pass)) {
            tilPassConf.setError("password is not match");
            return true;
        } else {
            tilPassConf.setError(null);
            tilPassConf.setErrorEnabled(false);
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        submit = (Button) findViewById(R.id.btn_submit);
        editPhone = (EditText) findViewById(R.id.phone);
        editPass = (EditText) findViewById(R.id.password);
        editPassConf = (EditText) findViewById(R.id.password_confirm);
        tilPhone = (TextInputLayout) findViewById(R.id.til_phone);
        tilPass = (TextInputLayout) findViewById(R.id.til_password);
        tilPassConf = (TextInputLayout) findViewById(R.id.til_password_confirm);

        sp = getSharedPreferences(C.SESSION, MODE_PRIVATE);
        pd = new ProgressDialog(this);

        editPhone.setOnEditorActionListener(this);
        editPass.setOnEditorActionListener(this);
        editPassConf.setOnEditorActionListener(this);

        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_submit:
                boolean isPhoneInCorrect = checkPhoneInput();
                boolean isPassInCorrect = checkPassInput();
                boolean isPassNotMatch = checkPassConf();

                boolean isCanContinue = !(isPhoneInCorrect||isPassInCorrect||isPassNotMatch);
                if (isCanContinue) {
                    pd.setMessage("registering your phone number");
                    pd.show();
                    StringRequest sr = new StringRequest(Request.Method.POST, C.API_REGISTER,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    pd.dismiss();
                                    if(response.trim().toString().equals("g")){
                                        Toast.makeText(RegisterActivity.this, "Phone number already registered", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        try {
                                            JSONObject jobj = new JSONObject(response);
                                            sp.edit().putString(C.USER_ID, jobj.getString(C.USER_ID)).commit();
                                            startActivity(new Intent(RegisterActivity.this, YourData.class));
                                            finish();

                                        } catch (JSONException e) {
                                            Log.i("CEK", e.toString());
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pd.dismiss();
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            HashMap<String,String> params = new HashMap<>();
                            params.put(C.PHONE, editPhone.getText().toString().trim());
                            params.put(C.PASSWORD, editPass.getText().toString().trim());
                            return params;
                        }
                    };
                    Volley.newRequestQueue(this).add(sr);
                }
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        int id = v.getId();
        if (id == R.id.phone) {
            if (actionId == EditorInfo.IME_ACTION_NEXT)
                return checkPhoneInput();
        }
        if (id == R.id.password) {
            return checkPassInput();
        }
        if (id == R.id.password_confirm){
            return checkPassConf();
        }
        return false;
    }
}

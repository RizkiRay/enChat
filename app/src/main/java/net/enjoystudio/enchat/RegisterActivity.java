package net.enjoystudio.enchat;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener,
        TextView.OnEditorActionListener {

    private EditText editPhone;
    private TextInputLayout tilPhone;
    private EditText editPass;
    private TextInputLayout tilPass;
    private Button submit;
    private EditText editPassConf;
    private TextInputLayout tilPassConf;


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
                    Toast.makeText(RegisterActivity.this, "Betull", Toast.LENGTH_SHORT).show();
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

package net.enjoystudio.enchat;

import android.content.Intent;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,
        TextView.OnEditorActionListener {

    private Button login;
    private TextView regist;
    private EditText editPass;
    private EditText editEmail;
    private TextInputLayout tilPass;
    private TextInputLayout tilEmail;

    private Boolean checkEmailInput() {
        String target = editEmail.getText().toString().trim();
        if (target.equals("")) {
            tilEmail.setError("can't be empty");
            return true;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()) {
            tilEmail.setError("not a valid email");
            return true;
        } else {
            tilEmail.setError(null);
            tilEmail.setErrorEnabled(false);
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
        login = (Button) findViewById(R.id.btn_login);
        regist = (TextView) findViewById(R.id.btn_regist);
        editEmail = (EditText) findViewById(R.id.email);
        editPass = (EditText) findViewById(R.id.password);
        tilEmail = (TextInputLayout) findViewById(R.id.til_email);
        tilPass = (TextInputLayout) findViewById(R.id.til_password);

        editEmail.setOnEditorActionListener(this);
        editPass.setOnEditorActionListener(this);
        login.setOnClickListener(this);
        regist.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_login:
                boolean isEmailInCorrect = checkEmailInput();
                boolean isPassInCorrect = checkPassInput();
                boolean isCanContinue = !(isEmailInCorrect||isPassInCorrect);
                if(isCanContinue)
                    Toast.makeText(LoginActivity.this, "Benar", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_regist:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        int id = v.getId();
        if (id == R.id.email) {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                return checkEmailInput();
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

package net.enjoystudio.enchat.conversation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import net.enjoystudio.enchat.C;
import net.enjoystudio.enchat.R;

public class Chat extends AppCompatActivity {

    private EditText editChat;
    private TextView textName;
    private TextView textStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        String name = getIntent().getStringExtra(C.NAME);
        String status = getIntent().getStringExtra(C.STATUS);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        editChat = (EditText) findViewById(R.id.edit_chat);
        textName = (TextView) findViewById(R.id.text_name);
        textStatus = (TextView) findViewById(R.id.text_status);
        textName.setText(name);
        textStatus.setText(status);
        editChat.setSingleLine(true);
        editChat.setMaxLines(5);
        editChat.setHorizontallyScrolling(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}

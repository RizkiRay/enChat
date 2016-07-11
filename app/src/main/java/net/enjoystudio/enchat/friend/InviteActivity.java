package net.enjoystudio.enchat.friend;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ProgressBar;

import net.enjoystudio.enchat.R;
import net.enjoystudio.enchat.friend.InvitationAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class InviteActivity extends AppCompatActivity {

    private RecyclerView rv;
    public static ArrayList<HashMap<String,String>> datalist;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Invitation");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        rv = (RecyclerView) findViewById(R.id.recyclerview);

        rv.setLayoutManager(new LinearLayoutManager(this));
        InvitationAdapter adapter = new InvitationAdapter(datalist);
        rv.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            finish();
        return true;
    }
}

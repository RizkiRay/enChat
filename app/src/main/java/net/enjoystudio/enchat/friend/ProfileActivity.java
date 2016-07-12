package net.enjoystudio.enchat.friend;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.enjoystudio.enchat.C;
import net.enjoystudio.enchat.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView picture;
    private TextView txtName;
    private TextView txtStatus;
    private TextView txtPhone;
//    private EditText editStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        String photo = getIntent().getStringExtra(C.PHOTO);
        String name = getIntent().getStringExtra(C.NAME);
        String status = getIntent().getStringExtra(C.STATUS);
        String phone = getIntent().getStringExtra(C.PHONE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Profile");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        picture = (CircleImageView) findViewById(R.id.picture);
        txtName = (TextView) findViewById(R.id.txt_name);
        txtPhone = (TextView) findViewById(R.id.txt_phone);
//        editStatus = (EditText) findViewById(R.id.edit_status);
        txtStatus = (TextView) findViewById(R.id.txt_status);
        Picasso.with(this).load(C.API_IMAGE + C.PROFILE_PICTURE + "/" + photo)
                .centerInside().fit().into(picture);
        txtName.setText(name);
        txtStatus.setText(status);
        Log.i("CEK",phone);
        txtPhone.setText(phone);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }
}

package net.enjoystudio.enchat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import net.enjoystudio.enchat.conversation.ConversationFragment;
import net.enjoystudio.enchat.friend.FriendFragment;

import de.hdodenhof.circleimageview.CircleImageView;

public class BaseAppActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView name, status;
    private SharedPreferences sp;
    private CircleImageView profPict;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_app);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sp = getSharedPreferences(C.SESSION, MODE_PRIVATE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        FirebaseMessaging.getInstance().subscribeToTopic("enChat");
        FirebaseInstanceId.getInstance().getToken();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View v = navigationView.getHeaderView(0);
        profPict = (CircleImageView) v.findViewById(R.id.profile_pict);
        name = (TextView)v.findViewById(R.id.name);
        status = (TextView) v.findViewById(R.id.status);

        Picasso.with(this).load(C.API_IMAGE + C.PROFILE_PICTURE + "/" + sp.getString(C.PROFILE_PICTURE, "0"))
                .fit().centerInside().into(profPict);
        name.setText(sp.getString(C.NAME,"KOSONG"));
        status.setText(sp.getString(C.STATUS,"KOSONG"));
        navigationView.setNavigationItemSelectedListener(this);
        if(savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new ConversationFragment())
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_conversation) {
            ft.replace(R.id.container,new ConversationFragment()).commit();
        } else if (id == R.id.nav_friends) {
            ft.replace(R.id.container,new FriendFragment()).commit();
        }  else if (id == R.id.nav_logout) {
            AlertDialog.Builder builder= new AlertDialog.Builder(this);
            builder.setMessage("Logout ?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sp.edit().clear().commit();
                            startActivity(new Intent(BaseAppActivity.this, LoginActivity.class));
                            finish();
                        }
                    })
                    .setNegativeButton("No", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (id == R.id.nav_about) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

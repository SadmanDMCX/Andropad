package android.notepad.app.dmcx.notepadandroid.Activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.notepad.app.dmcx.notepadandroid.Activities.Variables.Vars;
import android.notepad.app.dmcx.notepadandroid.BroadcastReciver.NetworkBroadcastReciver;
import android.notepad.app.dmcx.notepadandroid.Fragments.Main.AboutFragment;
import android.notepad.app.dmcx.notepadandroid.Fragments.Main.ConverterFragment;
import android.notepad.app.dmcx.notepadandroid.Fragments.Main.NotesFragment;
import android.notepad.app.dmcx.notepadandroid.Fragments.Main.TodosFragment;
import android.notepad.app.dmcx.notepadandroid.Fragments.Process.UserProfileFragment;
import android.notepad.app.dmcx.notepadandroid.R;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static MainActivity instance;

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    private TextView nameUserNavbarProfileTV;
    private TextView emailUserNavbarProfileTV;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    private NetworkBroadcastReciver networkBroadcastReciver;

    public static void reload() {
        instance.loadUserDetail();
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawerLayout);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        networkBroadcastReciver = new NetworkBroadcastReciver();
    }

    private void initToolbar() {
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        View navHeaderView = navigationView.getHeaderView(0);
        nameUserNavbarProfileTV = navHeaderView.findViewById(R.id.nameUserNavbarProfileTV);
        emailUserNavbarProfileTV = navHeaderView.findViewById(R.id.emailUserNavbarProfileTV);
    }

    private void loadUserDetail() {
        mFirestore.collection("users").document(mAuth.getCurrentUser().getUid()).collection("profile").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                DocumentSnapshot data = task.getResult().getDocuments().get(0);
                nameUserNavbarProfileTV.setText(data.get("name").toString());
                emailUserNavbarProfileTV.setText(mAuth.getCurrentUser().getEmail());
            }
        });
    }

    private void loadNotesFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .replace(R.id.fragment_container, new NotesFragment(), NotesFragment.TAG).commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;

        init();
        initToolbar();

        loadUserDetail();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawer(Gravity.START);

                final MenuItem theItem = item;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        switch (theItem.getItemId()) {
                            case R.id.notesNavItem:
                                getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                                    .replace(R.id.fragment_container, new NotesFragment(), NotesFragment.TAG).commit();
                                break;
                            case R.id.todosNavItem:
                                getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                                    .replace(R.id.fragment_container, new TodosFragment(), TodosFragment.TAG).commit();
                                break;
                            case R.id.converterNavItem:
                                getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                                    .replace(R.id.fragment_container, new ConverterFragment(), ConverterFragment.TAG).commit();
                                break;
                            case R.id.aboutMeNavItem:
                                getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                                    .replace(R.id.fragment_container, new AboutFragment(), AboutFragment.TAG).commit();
                                break;
                        }
                    }
                }, 300);

                return false;
            }
        });

        loadNotesFragment();
    }

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        registerReceiver(networkBroadcastReciver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(networkBroadcastReciver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profileNavItem: {
                Intent profileIntent = new Intent(MainActivity.instance, ProcessActivity.class);
                profileIntent.putExtra(Vars.Process, Vars.ProcessType.UserProfile);
                profileIntent.putExtra(Vars.Title, "Profile");
                startActivity(profileIntent);
                break;
            }
            case R.id.logoutNavItem: {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();

                Intent logoutIntent = new Intent(MainActivity.instance, AuthActivity.class);
                startActivity(logoutIntent);
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Fragment about = instance.getSupportFragmentManager().findFragmentByTag(AboutFragment.TAG);
        Fragment todo = instance.getSupportFragmentManager().findFragmentByTag(TodosFragment.TAG);
        Fragment converter = instance.getSupportFragmentManager().findFragmentByTag(ConverterFragment.TAG);

        if (about != null && about.isVisible()) {
            loadNotesFragment();
            return;
        } else if (todo != null && todo.isVisible()) {
            loadNotesFragment();
            return;
        } else if (converter != null && converter.isVisible()) {
            loadNotesFragment();
            return;
        }

        super.onBackPressed();
        finish();
    }
}

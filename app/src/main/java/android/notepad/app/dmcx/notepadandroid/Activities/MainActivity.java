package android.notepad.app.dmcx.notepadandroid.Activities;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.notepad.app.dmcx.notepadandroid.Activities.Variables.Vars;
import android.notepad.app.dmcx.notepadandroid.Fragments.Main.AboutFragment;
import android.notepad.app.dmcx.notepadandroid.Fragments.Main.NotesFragment;
import android.notepad.app.dmcx.notepadandroid.Fragments.Main.TodosFragment;
import android.notepad.app.dmcx.notepadandroid.R;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    public static MainActivity instance;

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    private TextView nameUserNavbarProfileTV;
    private TextView emailUserNavbarProfileTV;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    public static void reload() {
        instance.loadUserDetail();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;

        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawerLayout);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        View navHeaderView = navigationView.getHeaderView(0);
        nameUserNavbarProfileTV = navHeaderView.findViewById(R.id.nameUserNavbarProfileTV);
        emailUserNavbarProfileTV = navHeaderView.findViewById(R.id.emailUserNavbarProfileTV);

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

        getSupportFragmentManager()
            .beginTransaction()
            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
            .replace(R.id.fragment_container, new NotesFragment(), NotesFragment.TAG).commit();
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
        super.onBackPressed();
        finish();
    }
}

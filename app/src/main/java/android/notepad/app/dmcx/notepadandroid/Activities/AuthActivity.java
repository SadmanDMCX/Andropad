package android.notepad.app.dmcx.notepadandroid.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.notepad.app.dmcx.notepadandroid.BroadcastReciver.NetworkBroadcastReciver;
import android.notepad.app.dmcx.notepadandroid.Fragments.Auth.SignInFragment;
import android.notepad.app.dmcx.notepadandroid.Fragments.Auth.SignUpFragment;
import android.notepad.app.dmcx.notepadandroid.R;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    public static AuthActivity instance;

    private FirebaseAuth mAuth;

    private NetworkBroadcastReciver networkBroadcastReciver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        instance = this;

        mAuth = FirebaseAuth.getInstance();

        networkBroadcastReciver = new NetworkBroadcastReciver();

        getSupportFragmentManager()
            .beginTransaction()
            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
            .replace(R.id.fragment_container, new SignInFragment(), SignInFragment.TAG).commit();

    }

    @Override
    public void onStart() {
        super.onStart();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        registerReceiver(networkBroadcastReciver, intentFilter);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(networkBroadcastReciver);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentByTag(SignUpFragment.TAG) != null &&
                getSupportFragmentManager().findFragmentByTag(SignUpFragment.TAG).isVisible()) {
            getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .replace(R.id.fragment_container, new SignInFragment(), SignInFragment.TAG).commit();
        } else if (getSupportFragmentManager().findFragmentByTag(SignInFragment.TAG) != null &&
                getSupportFragmentManager().findFragmentByTag(SignInFragment.TAG).isVisible()) {
            super.onBackPressed();
            finish();
        }
    }
}

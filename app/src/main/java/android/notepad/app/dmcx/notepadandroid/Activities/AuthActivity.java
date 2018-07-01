package android.notepad.app.dmcx.notepadandroid.Activities;

import android.content.Intent;
import android.notepad.app.dmcx.notepadandroid.Fragments.Auth.SignInFragment;
import android.notepad.app.dmcx.notepadandroid.Fragments.Auth.SignUpFragment;
import android.notepad.app.dmcx.notepadandroid.R;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthActivity extends AppCompatActivity {

    public static AppCompatActivity instance;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        instance = this;

        mAuth = FirebaseAuth.getInstance();

        getSupportFragmentManager()
            .beginTransaction()
            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
            .replace(R.id.fragment_container, new SignInFragment(), SignInFragment.TAG).commit();

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
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

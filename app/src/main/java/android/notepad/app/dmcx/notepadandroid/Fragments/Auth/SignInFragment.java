package android.notepad.app.dmcx.notepadandroid.Fragments.Auth;

import android.app.AlertDialog;
import android.content.Intent;
import android.notepad.app.dmcx.notepadandroid.Activities.AuthActivity;
import android.notepad.app.dmcx.notepadandroid.Activities.MainActivity;
import android.notepad.app.dmcx.notepadandroid.R;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import dmax.dialog.SpotsDialog;

public class SignInFragment extends Fragment {

    public static final String TAG = "SIGN IN FRAGMENT";

    private EditText emailET;
    private EditText passwordET;
    private Button signInBTN;
    private Button signUpBTN;

    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        emailET = view.findViewById(R.id.emailET);
        passwordET = view.findViewById(R.id.passwordET);
        signInBTN = view.findViewById(R.id.signInBTN);
        signUpBTN = view.findViewById(R.id.saveBTN);

        mAuth = FirebaseAuth.getInstance();

        signInBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailET.getText().toString();
                String password = passwordET.getText().toString();

                if (email.equals("") || password.equals("")) {
                    Toast.makeText(AuthActivity.instance, "Fill all the fields.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(AuthActivity.instance, "Password must be 6 characters.", Toast.LENGTH_SHORT).show();
                    return;
                }

                final AlertDialog spotDialog = new SpotsDialog(AuthActivity.instance, "Please wait...");
                spotDialog.show();

                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(AuthActivity.instance, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            spotDialog.dismiss();

                            if (task.isSuccessful()) {
                                Intent intent = new Intent(AuthActivity.instance, MainActivity.class);
                                AuthActivity.instance.startActivity(intent);
                                AuthActivity.instance.finish();
                            } else {
                                Toast.makeText(AuthActivity.instance, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            }
        });

        signUpBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthActivity.instance.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .replace(R.id.fragment_container, new SignUpFragment(), SignUpFragment.TAG).commit();

            }
        });

        return view;
    }

}

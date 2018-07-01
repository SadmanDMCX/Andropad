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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class SignUpFragment extends Fragment {

    public static final String TAG = "SIGN UP FRAGMENT";

    private EditText nameET;
    private EditText emailET;
    private EditText passwordET;
    private Button signUpBTN;
    private Button signInBTN;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        nameET = view.findViewById(R.id.nameET);
        emailET = view.findViewById(R.id.emailET);
        passwordET = view.findViewById(R.id.passwordET);
        signUpBTN = view.findViewById(R.id.saveBTN);
        signInBTN = view.findViewById(R.id.singInBTN);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        signUpBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = nameET.getText().toString();
                final String email = emailET.getText().toString();
                final String password = passwordET.getText().toString();

                if (name.equals("") || email.equals("") || password.equals("")) {
                    Toast.makeText(AuthActivity.instance, "Fill all the fields.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(AuthActivity.instance, "Password must be 6 characters.", Toast.LENGTH_SHORT).show();
                    return;
                }

                final AlertDialog spotDialog = new SpotsDialog(AuthActivity.instance, "Please wait...");
                spotDialog.show();

                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(AuthActivity.instance, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                spotDialog.dismiss();

                                FirebaseUser user = mAuth.getCurrentUser();

                                if (user != null) {
                                    Map<String, String> userMap = new HashMap<>();
                                    userMap.put("name", name);

                                    mFirestore.collection("users").document(user.getUid()).collection("profile").add(userMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            if (task.isSuccessful()) {
                                                Intent intent = new Intent(AuthActivity.instance, MainActivity.class);
                                                AuthActivity.instance.startActivity(intent);
                                                AuthActivity.instance.finish();
                                            } else {
                                                Toast.makeText(AuthActivity.instance, "Some issue found. " + task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }

                            } else {
                                Toast.makeText(AuthActivity.instance, "Authentication failed. " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            }
        });

        signInBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthActivity.instance.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .replace(R.id.fragment_container, new SignInFragment(), SignInFragment.TAG).commit();
            }
        });

        return view;
    }
}

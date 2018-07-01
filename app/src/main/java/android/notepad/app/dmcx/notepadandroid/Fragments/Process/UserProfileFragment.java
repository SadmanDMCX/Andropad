package android.notepad.app.dmcx.notepadandroid.Fragments.Process;

import android.app.AlertDialog;
import android.content.Intent;
import android.notepad.app.dmcx.notepadandroid.Activities.AuthActivity;
import android.notepad.app.dmcx.notepadandroid.Activities.MainActivity;
import android.notepad.app.dmcx.notepadandroid.Activities.ProcessActivity;
import android.notepad.app.dmcx.notepadandroid.R;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.PreferenceChangeEvent;

import dmax.dialog.SpotsDialog;

public class UserProfileFragment extends Fragment {

    public static final String TAG = "USER PROFILE FRAGMENT";

    private TextView nameET;
    private TextView emailET;
    private TextView passwordET;
    private TextView oldPasswordET;
    private Button saveBTN;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore mFirestore;

    private String userId;
    private String prevEmail;
    private String prevPass;

    private void updateEP() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();

        Intent logoutIntent = new Intent(ProcessActivity.instance, AuthActivity.class);
        startActivity(logoutIntent);

        MainActivity.instance.finish();
        ProcessActivity.instance.finish();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        nameET = view.findViewById(R.id.nameET);
        emailET = view.findViewById(R.id.emailET);
        passwordET = view.findViewById(R.id.passwordET);
        oldPasswordET = view.findViewById(R.id.oldPasswordET);
        saveBTN = view.findViewById(R.id.saveBTN);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mFirestore = FirebaseFirestore.getInstance();

        final AlertDialog spotDialog = new SpotsDialog(ProcessActivity.instance, "Loading...");
        spotDialog.show();

        mFirestore.collection("users").document(mAuth.getCurrentUser().getUid()).collection("profile").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                spotDialog.dismiss();

                DocumentSnapshot data = task.getResult().getDocuments().get(0);
                nameET.setText(data.get("name").toString());
                emailET.setText(mUser.getEmail());

                userId = data.getId();
            }
        });

        saveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog spotDialog = new SpotsDialog(ProcessActivity.instance, "Loading...");
                spotDialog.show();

                boolean isNameNotGiven = false;
                boolean isEmailNotGiven = false;

                final String name = nameET.getText().toString();
                final String email = emailET.getText().toString();
                final String pass = passwordET.getText().toString();

                prevEmail = mUser.getEmail();
                prevPass = oldPasswordET.getText().toString();

                if (name.equals("") && email.equals("")) {
                    spotDialog.dismiss();
                    Toast.makeText(ProcessActivity.instance, "Need credentials to proceed!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (prevPass.equals("")) {
                    spotDialog.dismiss();
                    Toast.makeText(ProcessActivity.instance, "Old password needed!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!name.equals("")) {
                    Map<String, String> userMap = new HashMap<>();
                    userMap.put("name", name);

                    mFirestore.collection("users").document(mUser.getUid()).collection("profile").document(userId).set(userMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    MainActivity.reload();
                                }else {
                                    Toast.makeText(ProcessActivity.instance, "User update failed!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                } else {
                    isNameNotGiven = true;
                }

                if (!email.equals("")) {
                    AuthCredential authCredential = EmailAuthProvider.getCredential(prevEmail, prevPass);
                    mUser.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mUser.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            if (!pass.equals("")) {
                                                mUser.updatePassword(pass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        spotDialog.dismiss();

                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(ProcessActivity.instance, "User updated!", Toast.LENGTH_SHORT).show();
                                                            updateEP();
                                                        } else {
                                                            Toast.makeText(ProcessActivity.instance, "User not updated!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            } else {
                                                spotDialog.dismiss();
                                                updateEP();
                                            }
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(ProcessActivity.instance, "User credentials not matched.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    isEmailNotGiven = true;
                }

                if (isEmailNotGiven) {
                    Toast.makeText(ProcessActivity.instance, "Email remain same.", Toast.LENGTH_SHORT).show();
                }

                if (isNameNotGiven) {
                    Toast.makeText(ProcessActivity.instance, "Name remain same.", Toast.LENGTH_SHORT).show();
                }

                spotDialog.dismiss();
            }
        });


        return view;
    }
}

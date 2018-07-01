package android.notepad.app.dmcx.notepadandroid.Fragments.Process;

import android.notepad.app.dmcx.notepadandroid.Fragments.Main.NotesFragment;
import android.notepad.app.dmcx.notepadandroid.Activities.ProcessActivity;
import android.notepad.app.dmcx.notepadandroid.R;
import android.notepad.app.dmcx.notepadandroid.Activities.Variables.Vars;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NoteCreateFragment extends Fragment {

    public static final String TAG = "NOTE CREATE";

    private EditText noteContentET;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.process_fragment_create_note, container, false);

        noteContentET = view.findViewById(R.id.noteContentET);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        return view;
    }

    public void callback() {
        String title = getArguments().getString(Vars.Title);
        String content = noteContentET.getText().toString();

        Map<String, Object> noteMap = new HashMap<>();
        noteMap.put("title", title);
        noteMap.put("content", content);
        noteMap.put("time", System.currentTimeMillis());

        FirebaseUser user = mAuth.getCurrentUser();
        mFirestore.collection("users").document(user.getUid()).collection("notes").add(noteMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ProcessActivity.instance, "Note saved.", Toast.LENGTH_SHORT).show();
                    NotesFragment.reload();
                } else {
                    Toast.makeText(ProcessActivity.instance, "Note not saved.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

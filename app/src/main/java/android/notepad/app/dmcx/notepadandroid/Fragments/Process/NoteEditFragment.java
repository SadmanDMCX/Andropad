package android.notepad.app.dmcx.notepadandroid.Fragments.Process;

import android.notepad.app.dmcx.notepadandroid.Fragments.Main.Notes.NoteModel;
import android.notepad.app.dmcx.notepadandroid.Fragments.Main.NotesFragment;
import android.notepad.app.dmcx.notepadandroid.Activities.ProcessActivity;
import android.notepad.app.dmcx.notepadandroid.Fragments.Process.NoteEdit.TitleBottomSheetFragment;
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
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NoteEditFragment extends Fragment implements TitleBottomSheetFragment.TitleBottomSheetInterface {

    public static final String TAG = "NOTE EDIT";
    public static final String TITLE_BOTTOM_SHEET_FRAGMENT = "Title Bottom Fragment";
    public static NoteEditFragment instance;

    private EditText noteContentET;
    private ImageButton triggerUpIB;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore mFirestore;

    private String id;
    private String title;
    private String prevTitle;
    private String content;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.process_fragment_edit_note, container, false);

        instance = this;

        noteContentET = view.findViewById(R.id.noteContentET);
        triggerUpIB = view.findViewById(R.id.triggerUpIB);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mFirestore = FirebaseFirestore.getInstance();

        NoteModel note = getArguments().getParcelable(Vars.Content);
        id = note.getId();
        title = note.getTitle();
        prevTitle = note.getTitle();
        content = note.getContent();

        noteContentET.setText(content);

        triggerUpIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TitleBottomSheetFragment titleBottomSheetFragment = new TitleBottomSheetFragment();
                titleBottomSheetFragment.show(getChildFragmentManager(), TITLE_BOTTOM_SHEET_FRAGMENT);
                titleBottomSheetFragment.setTitle(title);
            }
        });

        return view;
    }

    public void callback() {
        String newContent = noteContentET.getText().toString();

        if (!newContent.equals(content) || !title.equals(prevTitle)) {
            Map<String, Object> noteMap = new HashMap<>();
            noteMap.put("title", title);
            noteMap.put("content", newContent);
            noteMap.put("time", System.currentTimeMillis());

            mFirestore.collection("users").document(mUser.getUid()).collection("notes").document(id).set(noteMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProcessActivity.instance, "Note Updated", Toast.LENGTH_SHORT).show();
                        NotesFragment.instance.reload();
                    } else {
                        Toast.makeText(ProcessActivity.instance, "Note update failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(ProcessActivity.instance, "Nothing to save!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void triggerOnClickListerer(String title) {
        ProcessActivity.instance.getSupportActionBar().setTitle(title);
        this.title = title;
    }
}

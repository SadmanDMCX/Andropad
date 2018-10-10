package android.notepad.app.dmcx.notepadandroid.Fragments.Main;

import android.app.AlertDialog;
import android.content.Intent;
import android.notepad.app.dmcx.notepadandroid.Fragments.Main.Notes.NotesRecylerViewAdapter;
import android.notepad.app.dmcx.notepadandroid.Fragments.Main.Notes.NoteModel;
import android.notepad.app.dmcx.notepadandroid.Activities.MainActivity;
import android.notepad.app.dmcx.notepadandroid.Activities.ProcessActivity;
import android.notepad.app.dmcx.notepadandroid.R;
import android.notepad.app.dmcx.notepadandroid.Activities.Variables.Vars;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class NotesFragment extends Fragment {

    public static final String TAG = "NOTE FRAGEMENT";
    public static NotesFragment instance;

    private TextView noDataFoundTV;
    private EditText searchNoteET;
    private RecyclerView noteGridRV;
    private Button createNewNoteBTN;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser mUser;

    private NotesRecylerViewAdapter notesRecylerViewAdapter;

    public static void reload() {
        instance.loadRecyclerView();
    }

    private List<NoteModel> loadData() {
        final List<NoteModel> noteList = new ArrayList<>();

        final AlertDialog spotDialog = new SpotsDialog(MainActivity.instance, "Data Retriving...");
        spotDialog.show();

        mFirestore.collection("users").document(mUser.getUid()).collection("notes").orderBy("time", Query.Direction.DESCENDING)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        spotDialog.dismiss();
                        
                        if (!documentSnapshots.isEmpty()) {
                            searchNoteET.setEnabled(true);
                            noteGridRV.setVisibility(View.VISIBLE);
                            noDataFoundTV.setVisibility(View.GONE);

                            List<DocumentSnapshot> notes = documentSnapshots.getDocuments();
                            for(DocumentSnapshot note : notes) {
                                NoteModel model = new NoteModel(note.getId(), note.get("title").toString(), note.get("content").toString(), note.get("time").toString());
                                noteList.add(model);
                            }

                            notesRecylerViewAdapter.notifyDataSetChanged();
                        } else {
                            searchNoteET.setEnabled(false);
                            noteGridRV.setVisibility(View.GONE);
                            noDataFoundTV.setVisibility(View.VISIBLE);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        spotDialog.dismiss();
                        Log.d(Vars.AppTag, "Note Retrive Failed: " + e.getMessage());
                    }
                });

        return noteList;
    }

    private void loadRecyclerView() {
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        noteGridRV.setLayoutManager(staggeredGridLayoutManager);
        notesRecylerViewAdapter = new NotesRecylerViewAdapter(loadData());
        noteGridRV.setAdapter(notesRecylerViewAdapter);
    }

    private void loadRecyclerView(List<NoteModel> list) {
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        noteGridRV.setLayoutManager(staggeredGridLayoutManager);
        notesRecylerViewAdapter = new NotesRecylerViewAdapter(list);
        noteGridRV.setAdapter(notesRecylerViewAdapter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, container, false);

        instance = this;

        noDataFoundTV = view.findViewById(R.id.noDataFoundTV);
        searchNoteET = view.findViewById(R.id.searchNoteET);
        noteGridRV = view.findViewById(R.id.noteGridRV);
        createNewNoteBTN = view.findViewById(R.id.createNewNoteBTN);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mFirestore = FirebaseFirestore.getInstance();

        noteGridRV.setHasFixedSize(true);
        loadRecyclerView();

        createNewNoteBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Vars.IsOnline) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.instance);
                    View dialogView = LayoutInflater.from(MainActivity.instance).inflate(R.layout.dialog_create_note, null);
                    builder.setView(dialogView);
                    final AlertDialog dialog = builder.create();
                    dialog.show();

                    final EditText noteTitleET = dialogView.findViewById(R.id.noteTitleET);
                    Button cancelBTN = dialogView.findViewById(R.id.cancelBTN);
                    Button confirmBTN = dialogView.findViewById(R.id.confirmBTN);

                    cancelBTN.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    confirmBTN.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();

                            String title = noteTitleET.getText().toString();
                            if (title.equals("")) {
                                Toast.makeText(MainActivity.instance, "Need a title of the note.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Intent intent = new Intent(MainActivity.instance, ProcessActivity.class);
                            intent.putExtra(Vars.Process, Vars.ProcessType.NoteCreate);
                            intent.putExtra(Vars.Title, title);
                            MainActivity.instance.startActivity(intent);
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.instance, "Please connect to internet.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        searchNoteET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (Vars.IsOnline) {
                    String search = charSequence.toString();
                    if (!search.equals("")) {
                        mFirestore.collection("users").document(mUser.getUid()).collection("notes").orderBy("title").startAt(search).endAt(search+"\uf8ff").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                List<DocumentSnapshot> snapshots = task.getResult().getDocuments();
                                List<NoteModel> notes = new ArrayList<>();

                                for (DocumentSnapshot snapshot : snapshots) {
                                    notes.add(new NoteModel(snapshot.getId(), snapshot.get("title").toString(), snapshot.get("content").toString(), snapshot.get("time").toString()));
                                }

                                loadRecyclerView(notes);
                            }
                        });

                        return;
                    }

                    Toast.makeText(MainActivity.instance, "Reloading. Please wait.", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadRecyclerView();
                        }
                    }, 500);
                } else {
                    Toast.makeText(MainActivity.instance, "Please connect to internet.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }
}

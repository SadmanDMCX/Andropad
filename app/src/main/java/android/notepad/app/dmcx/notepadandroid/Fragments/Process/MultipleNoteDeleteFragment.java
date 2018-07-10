package android.notepad.app.dmcx.notepadandroid.Fragments.Process;

import android.app.AlertDialog;
import android.content.Intent;
import android.notepad.app.dmcx.notepadandroid.Activities.MainActivity;
import android.notepad.app.dmcx.notepadandroid.Activities.ProcessActivity;
import android.notepad.app.dmcx.notepadandroid.Activities.Variables.Vars;
import android.notepad.app.dmcx.notepadandroid.Fragments.Main.Notes.NoteModel;
import android.notepad.app.dmcx.notepadandroid.Fragments.Main.Notes.NotesRecylerViewAdapter;
import android.notepad.app.dmcx.notepadandroid.Fragments.Main.NotesFragment;
import android.notepad.app.dmcx.notepadandroid.Fragments.Main.TodosFragment;
import android.notepad.app.dmcx.notepadandroid.Fragments.Process.MultipleNoteDelete.MulptipleNoteDeleteRecyclerViewAdapter;
import android.notepad.app.dmcx.notepadandroid.R;
import android.os.AsyncTask;
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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.PropertyResourceBundle;
import java.util.concurrent.ExecutionException;

import dmax.dialog.SpotsDialog;

public class MultipleNoteDeleteFragment extends Fragment {

    public static final String TAG = "MULTIPLE NOTE FRAGEMENT";
    public static MultipleNoteDeleteFragment instance;

    private RecyclerView noteGridRV;
    private Button deleteItemsBTN;
    private EditText searchNoteET;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser mUser;

    private MulptipleNoteDeleteRecyclerViewAdapter mulptipleNoteDeleteRecyclerViewAdapter;

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
                    List<DocumentSnapshot> notes = documentSnapshots.getDocuments();
                    for(DocumentSnapshot note : notes) {
                        NoteModel model = new NoteModel(note.getId(), note.get("title").toString(), note.get("content").toString(), note.get("time").toString());
                        noteList.add(model);
                    }

                    mulptipleNoteDeleteRecyclerViewAdapter.notifyDataSetChanged();
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

        String selectedId = Objects.requireNonNull(getArguments().get(Vars.Id)).toString();
//        String position = Objects.requireNonNull(getArguments().get(Vars.Position)).toString();
        mulptipleNoteDeleteRecyclerViewAdapter = new MulptipleNoteDeleteRecyclerViewAdapter(loadData(), selectedId);
        noteGridRV.setAdapter(mulptipleNoteDeleteRecyclerViewAdapter);
    }

    private void loadRecyclerView(List<NoteModel>  notes) {
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        noteGridRV.setLayoutManager(staggeredGridLayoutManager);

        String selectedId = Objects.requireNonNull(getArguments().get(Vars.Id)).toString();
        String position = Objects.requireNonNull(getArguments().get(Vars.Position)).toString();
        mulptipleNoteDeleteRecyclerViewAdapter = new MulptipleNoteDeleteRecyclerViewAdapter(notes, selectedId);
        noteGridRV.setAdapter(mulptipleNoteDeleteRecyclerViewAdapter);
        noteGridRV.scrollToPosition(Integer.valueOf(position));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.process_fragment_multiple_delete, container, false);

        instance = this;

        searchNoteET = view.findViewById(R.id.searchNoteET);
        noteGridRV = view.findViewById(R.id.noteGridRV);
        deleteItemsBTN = view.findViewById(R.id.deleteItemsBTN);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mFirestore = FirebaseFirestore.getInstance();

        noteGridRV.setHasFixedSize(true);
        loadRecyclerView();

        searchNoteET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
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
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        deleteItemsBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<String> ids = mulptipleNoteDeleteRecyclerViewAdapter.getIds();
                if (ids == null) {
                    Toast.makeText(ProcessActivity.instance, "No data selected.", Toast.LENGTH_LONG).show();
                    return;
                }

                DeleteMultipleData deleteMultipleData = new DeleteMultipleData();
                deleteMultipleData.execute(ids);
            }
        });

        return view;
    }


    private static class DeleteMultipleData extends AsyncTask<List<String>, Void, List<Boolean>> {

        private AlertDialog spotDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            spotDialog = new SpotsDialog(ProcessActivity.instance, "Data Deleting...");
            spotDialog.show();
        }

        @SafeVarargs
        @Override
        protected final List<Boolean> doInBackground(List<String>... strings) {
            final List<Boolean> isDeleted = new ArrayList<>();

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser mUser = mAuth.getCurrentUser();
            FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

            for (final String id : strings[0]) {
                assert mUser != null;
                mFirestore.collection("users").document(mUser.getUid()).collection("notes").document(id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            isDeleted.add(true);
                        } else {
                            isDeleted.add(false);
                        }
                    }
                });
            }

            return isDeleted;
        }

        @Override
        protected void onPostExecute(List<Boolean> isDeleted) {
            super.onPostExecute(isDeleted);

            boolean isDel = true;
            for (boolean isDelete : isDeleted) {
                if (!isDelete) {
                    isDel = false;
                }
            }

            if (isDel) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        spotDialog.dismiss();

                        Toast.makeText(ProcessActivity.instance, "Notes deleted.", Toast.LENGTH_SHORT).show();
                        NotesFragment.reload();
                        ProcessActivity.instance.finish();
                    }
                }, 2000);
            } else {
                spotDialog.dismiss();
                Toast.makeText(ProcessActivity.instance, "Some data not deleted.", Toast.LENGTH_SHORT).show();
                MultipleNoteDeleteFragment.reload();
            }
        }
    }

}

package android.notepad.app.dmcx.notepadandroid.Fragments.Main.Notes;

import android.content.Intent;
import android.notepad.app.dmcx.notepadandroid.Fragments.Main.NotesFragment;
import android.notepad.app.dmcx.notepadandroid.Activities.MainActivity;
import android.notepad.app.dmcx.notepadandroid.Activities.ProcessActivity;
import android.notepad.app.dmcx.notepadandroid.R;
import android.notepad.app.dmcx.notepadandroid.Activities.Variables.Vars;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class NotesRecylerViewAdapter extends RecyclerView.Adapter<NotesRecylerViewAdapter.NotesRecyclerViewHolder> {

    private List<NoteModel> notes;

    public NotesRecylerViewAdapter(List<NoteModel> notes) {
        this.notes = notes;
    }

    @Override
    public NotesRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_single_note, parent, false);
        return new NotesRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotesRecyclerViewHolder holder, int position) {
        holder.noteTitleTV.setText(notes.get(position).getTitle());
        holder.noteContentTV.setText(notes.get(position).getContent());

        final String id = notes.get(position).getId();
        final String title = notes.get(position).getTitle();
        final String content = notes.get(position).getContent();
        final String time = notes.get(position).getTime();
        final String itemPosition = String.valueOf(holder.getAdapterPosition());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.instance, ProcessActivity.class);
                intent.putExtra(Vars.Process, Vars.ProcessType.NoteEdit);
                intent.putExtra(Vars.Title, title);
                intent.putExtra(Vars.Content, new NoteModel(id, title, content, time));
                MainActivity.instance.startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(MainActivity.instance, ProcessActivity.class);
                intent.putExtra(Vars.Process, Vars.ProcessType.NoteMultipleDelete);
                intent.putExtra(Vars.Title, "Delete Notes");
                intent.putExtra(Vars.Id, id);
                intent.putExtra(Vars.Position, itemPosition);
                MainActivity.instance.startActivity(intent);
                return false;
            }
        });

        holder.noteDeleteIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

                assert user != null;
                mFirestore.collection("users").document(user.getUid()).collection("notes").document(id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.instance, "Note Deleted.", Toast.LENGTH_SHORT).show();
                            NotesFragment.reload();
                        } else {
                            Toast.makeText(MainActivity.instance, "Note delete failed. " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class NotesRecyclerViewHolder extends RecyclerView.ViewHolder {

        public TextView noteTitleTV;
        public TextView noteContentTV;
        public ImageButton noteDeleteIB;

        public NotesRecyclerViewHolder(View itemView) {
            super(itemView);

            noteTitleTV = itemView.findViewById(R.id.noteTitleTV);
            noteContentTV = itemView.findViewById(R.id.noteContentTV);
            noteDeleteIB = itemView.findViewById(R.id.noteDeleteIB);
        }
    }


}

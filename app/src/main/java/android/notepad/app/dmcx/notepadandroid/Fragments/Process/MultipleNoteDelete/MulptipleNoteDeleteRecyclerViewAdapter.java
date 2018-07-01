package android.notepad.app.dmcx.notepadandroid.Fragments.Process.MultipleNoteDelete;

import android.content.Intent;
import android.notepad.app.dmcx.notepadandroid.Fragments.Main.Notes.NoteModel;
import android.notepad.app.dmcx.notepadandroid.R;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MulptipleNoteDeleteRecyclerViewAdapter extends RecyclerView.Adapter<MulptipleNoteDeleteRecyclerViewAdapter.MulptipleNoteDeleteRecyclerViewHolder> {

    private List<NoteModel> notes;
    private List<String> ids;

    private String id;

    public MulptipleNoteDeleteRecyclerViewAdapter(List<NoteModel> notes, String id) {
        this.notes = notes;
        this.id = id;

        ids = new ArrayList<>();
        ids.add(id);
    }

    @Override
    public MulptipleNoteDeleteRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_single_multiple_delete, parent, false);
        return new MulptipleNoteDeleteRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MulptipleNoteDeleteRecyclerViewHolder holder, int position) {

        holder.noteTitleTV.setText(notes.get(position).getTitle());
        holder.noteContentTV.setText(notes.get(position).getContent());

        final String id = notes.get(position).getId();

        if (id.equals(this.id)) {
            holder.noteDeleteCB.setChecked(true);
        }

        holder.noteDeleteCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    ids.add(id);
                } else {
                    ids.remove(id);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class MulptipleNoteDeleteRecyclerViewHolder extends RecyclerView.ViewHolder {

        public TextView noteTitleTV;
        public TextView noteContentTV;
        public CheckBox noteDeleteCB;

        public MulptipleNoteDeleteRecyclerViewHolder(View itemView) {
            super(itemView);

            noteTitleTV = itemView.findViewById(R.id.noteTitleTV);
            noteContentTV = itemView.findViewById(R.id.noteContentTV);
            noteDeleteCB = itemView.findViewById(R.id.noteDeleteCB);
        }
    }

    public List<String> getIds() {
        if (ids.size() == 0)
            return null;
        return ids;
    }
}

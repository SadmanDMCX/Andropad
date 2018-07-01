package android.notepad.app.dmcx.notepadandroid.Activities;

import android.notepad.app.dmcx.notepadandroid.Fragments.Main.Notes.NoteModel;
import android.notepad.app.dmcx.notepadandroid.Fragments.Process.MultipleNoteDeleteFragment;
import android.notepad.app.dmcx.notepadandroid.Fragments.Process.NoteCreateFragment;
import android.notepad.app.dmcx.notepadandroid.Fragments.Process.NoteEditFragment;
import android.notepad.app.dmcx.notepadandroid.Fragments.Process.UserProfileFragment;
import android.notepad.app.dmcx.notepadandroid.R;
import android.notepad.app.dmcx.notepadandroid.Activities.Variables.Vars;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.Objects;

public class ProcessActivity extends AppCompatActivity {

    public static AppCompatActivity instance;

    private Toolbar toolbar;

    private NoteCreateFragment noteCreateFragment;
    private NoteEditFragment noteEditFragment;
    private MultipleNoteDeleteFragment multipleNoteDeleteFragment;
    private UserProfileFragment userProfileFragment;

    private String process;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);

        instance = this;

        toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        process = Objects.requireNonNull(getIntent().getExtras()).getString(Vars.Process);

        assert process != null;
        switch (process) {
            case Vars.ProcessType.NoteCreate: {
                String title = getIntent().getExtras().getString(Vars.Title);
                toolbar.setTitle(title);

                Bundle bundle = new Bundle();
                bundle.putString(Vars.Title, title);

                noteCreateFragment = new NoteCreateFragment();
                noteCreateFragment.setArguments(bundle);

                getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .replace(R.id.fragment_container, noteCreateFragment, NoteCreateFragment.TAG).commit();
                break;
            }
            case Vars.ProcessType.NoteEdit: {
                String title = getIntent().getExtras().getString(Vars.Title);
                toolbar.setTitle(title);

                NoteModel note = getIntent().getParcelableExtra(Vars.Content);

                Bundle bundle = new Bundle();
                bundle.putParcelable(Vars.Content, note);

                noteEditFragment = new NoteEditFragment();
                noteEditFragment.setArguments(bundle);

                getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .replace(R.id.fragment_container, noteEditFragment, NoteEditFragment.TAG).commit();
                break;
            }
            case Vars.ProcessType.NoteMultipleDelete: {
                String title = getIntent().getExtras().getString(Vars.Title);
                toolbar.setTitle(title);

                String id = getIntent().getExtras().getString(Vars.Id);

                Bundle bundle = new Bundle();
                bundle.putString(Vars.Id, id);

                multipleNoteDeleteFragment = new MultipleNoteDeleteFragment();
                multipleNoteDeleteFragment.setArguments(bundle);

                getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .replace(R.id.fragment_container, multipleNoteDeleteFragment, multipleNoteDeleteFragment.TAG).commit();
                break;
            }
            case Vars.ProcessType.UserProfile: {
                String title = getIntent().getExtras().getString(Vars.Title);
                toolbar.setTitle(title);

                userProfileFragment = new UserProfileFragment();

                getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .replace(R.id.fragment_container, userProfileFragment, userProfileFragment.TAG).commit();
                break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        switch (process) {
            case Vars.ProcessType.NoteCreate:
                noteCreateFragment.callback();
                break;
            case Vars.ProcessType.NoteEdit:
                noteEditFragment.callback();
                break;
        }
    }
}

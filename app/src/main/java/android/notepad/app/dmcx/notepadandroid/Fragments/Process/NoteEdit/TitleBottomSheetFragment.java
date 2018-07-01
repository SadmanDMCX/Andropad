package android.notepad.app.dmcx.notepadandroid.Fragments.Process.NoteEdit;

import android.notepad.app.dmcx.notepadandroid.Activities.ProcessActivity;
import android.notepad.app.dmcx.notepadandroid.Fragments.Process.NoteEditFragment;
import android.notepad.app.dmcx.notepadandroid.R;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class TitleBottomSheetFragment extends BottomSheetDialogFragment {

    private static TitleBottomSheetFragment instace;

    private TitleBottomSheetInterface titleBottomSheetInterface;

    private EditText titleET;
    private ImageButton triggerDownIB;

    private String title;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_dialog_title, container, false);

        instace = this;

        titleET = view.findViewById(R.id.titleET);
        triggerDownIB = view.findViewById(R.id.triggerDownIB);

        titleET.setText(title);

        triggerDownIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                instace.dismiss();

                titleBottomSheetInterface.triggerOnClickListerer(titleET.getText().toString());
            }
        });

        return view;
    }

    public interface TitleBottomSheetInterface {
        void triggerOnClickListerer(String title);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            titleBottomSheetInterface = (TitleBottomSheetInterface) NoteEditFragment.instance;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

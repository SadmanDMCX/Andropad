package android.notepad.app.dmcx.notepadandroid.Fragments.Main.Todos;

import android.app.AlertDialog;
import android.notepad.app.dmcx.notepadandroid.Activities.MainActivity;
import android.notepad.app.dmcx.notepadandroid.Fragments.Main.TodosFragment;
import android.notepad.app.dmcx.notepadandroid.R;
import android.notepad.app.dmcx.notepadandroid.Utility.Utils;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TodosRecyclerViewAdapter extends RecyclerView.Adapter<TodosRecyclerViewAdapter.TodosRecyclerViewHolder> {

    private List<TodoModel> todos;

    public TodosRecyclerViewAdapter(List<TodoModel> todos) {
        this.todos = todos;
    }

    @Override
    public TodosRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_single_todo, parent, false);
        return new TodosRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TodosRecyclerViewHolder holder, int position) {

        String formatedDateTime = Utils.getFormatedDateTimeFromLong(todos.get(position).getTime(), "MMM dd,yyyy - hh:mm a");

        holder.taskTV.setText(todos.get(position).getTask());
        holder.timeTV.setText(formatedDateTime);

        final String id = todos.get(position).getId();
        final String task = todos.get(position).getTask();
        final String time = todos.get(position).getTime();
        final String isCompletedStr = todos.get(position).getIsCompleted();
        final boolean isCompleted = isCompletedStr.equals("1");
        final int posi = position;

        if (isCompleted) {
            holder.isCompletedCB.setChecked(true);
        }

        final CheckBox tempCB = holder.isCompletedCB;

        holder.isCompletedCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isTaskCompleted) {
                if (tempCB.isPressed()) {
                    final Map<String, Object> todoMap = new HashMap<>();
                    todoMap.put("task", task);
                    todoMap.put("time", time);
                    if (isTaskCompleted) {
                        todoMap.put("isCompleted", "1");
                    } else {
                        todoMap.put("isCompleted", "0");
                    }

                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseUser mUser = mAuth.getCurrentUser();
                    FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
                    mFirestore.collection("users").document(mUser.getUid()).collection("todos").document(id).set(todoMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(MainActivity.instance, "Task Not Completed", Toast.LENGTH_SHORT).show();
                            } else {
                                TodoModel model = new TodoModel(id, todoMap.get("task").toString(), todoMap.get("time").toString(), todoMap.get("isCompleted").toString());
                                TodosFragment.updateTodoList(posi, model);
                            }
                        }
                    });
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.instance);
                View dialogView = LayoutInflater.from(MainActivity.instance).inflate(R.layout.dialog_edit_todo, null);
                builder.setView(dialogView);
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();

                final EditText todoTaskET = dialogView.findViewById(R.id.todoTaskET);
                final Button cancelBTN = dialogView.findViewById(R.id.cancelBTN);
                Button confirmBTN = dialogView.findViewById(R.id.confirmBTN);

                todoTaskET.setText(task);

                cancelBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                confirmBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();

                        Map<String, Object> todoMap = new HashMap<>();
                        todoMap.put("task", todoTaskET.getText().toString());
                        todoMap.put("time", System.currentTimeMillis());
                        todoMap.put("isCompleted", isCompletedStr);

                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        FirebaseUser mUser = mAuth.getCurrentUser();
                        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

                        mFirestore.collection("users").document(mUser.getUid()).collection("todos").document(id).set(todoMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    TodosFragment.reload();
                                } else {
                                    Toast.makeText(MainActivity.instance, "Task not saved.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return todos.size();
    }

    public class TodosRecyclerViewHolder extends RecyclerView.ViewHolder {

        public TextView taskTV;
        public TextView timeTV;
        public CheckBox isCompletedCB;

        public TodosRecyclerViewHolder(View itemView) {
            super(itemView);

            taskTV = itemView.findViewById(R.id.taskTV);
            timeTV = itemView.findViewById(R.id.timeTV);
            isCompletedCB = itemView.findViewById(R.id.isCompletedCB);
        }
    }

}

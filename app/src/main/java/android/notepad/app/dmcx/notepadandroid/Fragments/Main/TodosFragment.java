package android.notepad.app.dmcx.notepadandroid.Fragments.Main;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Movie;
import android.notepad.app.dmcx.notepadandroid.Activities.Variables.Vars;
import android.notepad.app.dmcx.notepadandroid.Fragments.Main.Todos.TodoModel;
import android.notepad.app.dmcx.notepadandroid.Fragments.Main.Todos.TodosRecyclerViewAdapter;
import android.notepad.app.dmcx.notepadandroid.Activities.MainActivity;
import android.notepad.app.dmcx.notepadandroid.R;
import android.notepad.app.dmcx.notepadandroid.Utility.Utils;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class TodosFragment extends Fragment {

    public static final String TAG = "TODO FRAGMENT";
    public static TodosFragment instance;

    private TextView noDataFoundTV;
    private RecyclerView todosRV;
    private FloatingActionButton addNewFAB;
    private CoordinatorLayout fragment_todo;
    private SwipeRefreshLayout swipeRefresh;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser mUser;

    private List<TodoModel> allTheTodos;
    private boolean isUndoCalled;

    private TodosRecyclerViewAdapter todosRecyclerViewAdapter;

    public static void reload() {
        instance.loadRecyclerView();
    }

    public static void updateTodoList(int posi, TodoModel todoModel) {
        instance.allTheTodos.set(posi, todoModel);
    }

    private List<TodoModel> loadData() {
        final List<TodoModel> todoList = new ArrayList<>();

        final AlertDialog spotDialog = new SpotsDialog(MainActivity.instance, "Data Retriving...");

        if (!swipeRefresh.isRefreshing()) {
            spotDialog.show();
        }

        mFirestore.collection("users").document(mUser.getUid()).collection("todos").orderBy("isCompleted", Query.Direction.ASCENDING)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        spotDialog.dismiss();

                        if (!documentSnapshots.isEmpty()) {
                            todosRV.setVisibility(View.VISIBLE);
                            noDataFoundTV.setVisibility(View.GONE);

                            List<DocumentSnapshot> todos = documentSnapshots.getDocuments();
                            for(DocumentSnapshot todo : todos) {
                                TodoModel model = new TodoModel(todo.getId(), todo.get("task").toString(), todo.get("time").toString(), todo.get("isCompleted").toString());
                                todoList.add(model);
                            }

                            todosRecyclerViewAdapter.notifyDataSetChanged();
                        } else {
                            noDataFoundTV.setVisibility(View.VISIBLE);
                            todosRV.setVisibility(View.GONE);
                        }

                        allTheTodos = todoList;
                        swipeRefresh.setRefreshing(false);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                spotDialog.dismiss();
                swipeRefresh.setRefreshing(false);
                allTheTodos = todoList;
                Log.d(Vars.AppTag, "Todo Retrive Failed: " + e.getMessage());
            }
        });

        return todoList;
    }

    private void loadRecyclerView() {
        todosRecyclerViewAdapter = new TodosRecyclerViewAdapter(loadData());
        todosRecyclerViewAdapter.notifyDataSetChanged();
        todosRV.setAdapter(todosRecyclerViewAdapter);
        todosRV.smoothScrollToPosition(allTheTodos.size());
    }

    private ItemTouchHelper.Callback itemTouchCallback() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT
        ) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                final int position = viewHolder.getAdapterPosition();
                final TodoModel theData = allTheTodos.get(position);

                final String removeId = allTheTodos.get(position).getId();
                allTheTodos.remove(position);
                todosRecyclerViewAdapter.notifyItemRemoved(position);
                isUndoCalled = false;

                Snackbar.make(fragment_todo, "Item deleted", Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.LTGRAY)
                        .setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                allTheTodos.add(position, theData);
                                todosRecyclerViewAdapter.notifyItemInserted(position);
                                todosRV.smoothScrollToPosition(position);
                                isUndoCalled = true;

                            }
                        })
                        .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                            @Override
                            public void onDismissed(Snackbar transientBottomBar, int event) {
                                super.onDismissed(transientBottomBar, event);
                                if (!isUndoCalled) {
                                    mFirestore.collection("users").document(mUser.getUid()).collection("todos").document(removeId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(MainActivity.instance, "Task Deleted.", Toast.LENGTH_SHORT).show();
                                                NotesFragment.reload();
                                            } else {
                                                Toast.makeText(MainActivity.instance, "Task delete failed. " + task.getException(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }

                                isUndoCalled = false;
                            }
                        }).show();
            }
        };

        return simpleCallback;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo, container, false);

        instance = this;

        noDataFoundTV = view.findViewById(R.id.noDataFoundTV);
        todosRV = view.findViewById(R.id.todosRV);
        addNewFAB = view.findViewById(R.id.addNewFAB);
        fragment_todo = view.findViewById(R.id.fragment_todo);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mFirestore = FirebaseFirestore.getInstance();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.instance);
        todosRV.setLayoutManager(linearLayoutManager);
        todosRV.setHasFixedSize(true);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reload();
            }
        });

        todosRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && addNewFAB.getVisibility() == View.VISIBLE) {
                    addNewFAB.hide();
                } else if (dy < 0 && addNewFAB.getVisibility() != View.VISIBLE) {
                    addNewFAB.show();
                }
            }
        });

        allTheTodos = new ArrayList<>();

        loadRecyclerView();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback());
        itemTouchHelper.attachToRecyclerView(todosRV);

        addNewFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Vars.IsOnline) {
                    Toast.makeText(MainActivity.instance, "Internet connection needed.", Toast.LENGTH_SHORT).show();
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.instance);
                @SuppressLint("InflateParams")
                View dialogView = LayoutInflater.from(MainActivity.instance).inflate(R.layout.dialog_create_todo, null);
                builder.setView(dialogView);
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();

                final EditText todoTaskET = dialogView.findViewById(R.id.todoTaskET);
                final Button cancelBTN = dialogView.findViewById(R.id.cancelBTN);
                Button confirmBTN = dialogView.findViewById(R.id.confirmBTN);

                cancelBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                confirmBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String task = todoTaskET.getText().toString();

                        if (!task.equals("")) {
                            alertDialog.dismiss();

                            Map<String, Object> todoMap = new HashMap<>();
                            todoMap.put("task", task);
                            todoMap.put("time", System.currentTimeMillis());
                            todoMap.put("isCompleted", "0");

                            mFirestore.collection("users").document(mUser.getUid()).collection("todos").add(todoMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.instance, "Task saved.", Toast.LENGTH_SHORT).show();
                                        TodosFragment.reload();
                                    } else {
                                        Toast.makeText(MainActivity.instance, "Task not saved.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(MainActivity.instance, "No task entered!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });

        return view;
    }
}

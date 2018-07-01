package android.notepad.app.dmcx.notepadandroid.Fragments.Main.Todos;

import android.os.Parcel;
import android.os.Parcelable;

public class TodoModel {

    private String id;
    private String task;
    private String time;
    private String isCompleted;

    public TodoModel(String id, String task, String time, String isCompleted) {
        this.id = id;
        this.task = task;
        this.time = time;
        this.isCompleted = isCompleted;
    }

    public String getId() {
        return id;
    }

    public String getTask() {
        return task;
    }

    public String getTime() {
        return time;
    }

    public String getIsCompleted() {
        return isCompleted;
    }
}

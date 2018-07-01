package android.notepad.app.dmcx.notepadandroid.Fragments.Main.Notes;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Struct;

public class NoteModel implements Parcelable {

    private String id;
    private String title;
    private String content;
    private String time;

    public NoteModel(String id, String title, String content, String time) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.time = time;
    }

    protected NoteModel(Parcel in) {
        id = in.readString();
        title = in.readString();
        content = in.readString();
        time = in.readString();
    }

    public static final Creator<NoteModel> CREATOR = new Creator<NoteModel>() {
        @Override
        public NoteModel createFromParcel(Parcel in) {
            return new NoteModel(in);
        }

        @Override
        public NoteModel[] newArray(int size) {
            return new NoteModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(content);
        parcel.writeString(time);
    }
}

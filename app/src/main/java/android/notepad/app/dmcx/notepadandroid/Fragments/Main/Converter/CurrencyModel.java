package android.notepad.app.dmcx.notepadandroid.Fragments.Main.Converter;

import com.google.gson.annotations.SerializedName;

public class CurrencyModel {

    @SerializedName("val")
    private String converted_value;

    public String getConverted_value() {
        return converted_value;
    }
}

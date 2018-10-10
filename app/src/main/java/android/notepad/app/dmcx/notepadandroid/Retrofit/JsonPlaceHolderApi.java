package android.notepad.app.dmcx.notepadandroid.Retrofit;

import android.notepad.app.dmcx.notepadandroid.Fragments.Main.Converter.CurrencyModel;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface JsonPlaceHolderApi {

    @GET("convert?compact=y")
    Call<JsonObject> getCurrency(@Query("q") String cvt);

}

package android.notepad.app.dmcx.notepadandroid.Fragments.Main.Converter;

import android.notepad.app.dmcx.notepadandroid.Activities.MainActivity;
import android.notepad.app.dmcx.notepadandroid.Activities.Variables.Vars;
import android.notepad.app.dmcx.notepadandroid.Interface.IActionCallback;
import android.notepad.app.dmcx.notepadandroid.Retrofit.JsonPlaceHolderApi;
import android.notepad.app.dmcx.notepadandroid.Retrofit.RetrofitClient;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ConverterController {

    public static void Currency(final String cvt, final IActionCallback callback) {

        Retrofit retrofit = RetrofitClient.getRetrofitClient(Vars.CurrencyConverterUrl);

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

//        Call<JsonObject> currencyModelCall = jsonPlaceHolderApi.getCurrency(cvt, "y");
        Call<JsonObject> currencyModelCall = jsonPlaceHolderApi.getCurrency(cvt);

        currencyModelCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject object = response.body();
                if (object != null) {
                    JsonObject value = object.getAsJsonObject(cvt);
                    if (value != null) {
                        String converted = value.get("val").getAsString();
                        callback.onCallback(Float.valueOf(converted));
                    } else {
                        callback.onCallback("Error! Conversion failed!");
                    }
                } else {
                    callback.onCallback("Error! Request failed!");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });

    }

}

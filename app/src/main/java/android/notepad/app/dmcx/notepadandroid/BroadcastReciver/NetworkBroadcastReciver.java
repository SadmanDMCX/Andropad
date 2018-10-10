package android.notepad.app.dmcx.notepadandroid.BroadcastReciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.notepad.app.dmcx.notepadandroid.Activities.MainActivity;
import android.notepad.app.dmcx.notepadandroid.Activities.Variables.Vars;
import android.notepad.app.dmcx.notepadandroid.Fragments.Main.TodosFragment;
import android.widget.Toast;

public class NetworkBroadcastReciver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String networkState = intent.getAction();

        assert networkState != null;
        if (networkState.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            boolean isOffline = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            Vars.IsOnline = !isOffline;
        }
    }

}

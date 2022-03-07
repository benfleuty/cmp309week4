package uk.ac.abertay.telephonysms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class CallHijacker extends BroadcastReceiver {

    public CallHijacker(){}

    @Override
    public void onReceive(Context context, Intent intent) {

        String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        Toast.makeText(context, "Outgoing: " + number, Toast.LENGTH_LONG).show();

        if(true) return;

        Toast.makeText(context,String.valueOf( intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")), Toast.LENGTH_SHORT).show();
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            String oldNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Log.i("DEBUG", "To: " + oldNumber);
            this.setResultData("0123456789"); // replace the number
        }
    }
}

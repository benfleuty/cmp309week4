package uk.ac.abertay.telephonysms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class CallReceiver extends BroadcastReceiver {

    final String TAG = "BroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            actionPhoneStateChanged(intent);
        } else if (action.equals(Intent.ACTION_NEW_OUTGOING_CALL)){
            actionNewOutgoingCall(intent);
        }
    }

    private void actionNewOutgoingCall(Intent intent) {
        String oldNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        Log.i(TAG, "To: " + oldNumber);
        setResultData("0123456789");
    }

    private void actionPhoneStateChanged(Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        final String TAG = "PhoneStateListener";
        Log.i(TAG,"Call state is now " + state + " on number " + intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER));
    }
}

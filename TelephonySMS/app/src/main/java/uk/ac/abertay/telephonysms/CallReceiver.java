package uk.ac.abertay.telephonysms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class CallReceiver extends BroadcastReceiver {

    final String TAG = "BroadcastReceiver";

    private static final String RINGING = TelephonyManager.EXTRA_STATE_RINGING;
    private static final String OFFHOOK = TelephonyManager.EXTRA_STATE_OFFHOOK;
    private static final String IDLE = TelephonyManager.EXTRA_STATE_IDLE;
    String lastState = "IDLE";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER) == null) return;

        if (action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            actionPhoneStateChanged(context, intent);
        }
    }

    private void actionNewOutgoingCall(Context context, Intent intent) {
        String oldNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        Log.i(TAG, "To: " + oldNumber);
        this.setResultData("0123456789");
    }

    private void actionPhoneStateChanged(Context context, Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        final String TAG = "PhoneStateListener";
        final String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

        // Incoming Call
        if (state.equals(RINGING) && lastState.equals(IDLE)) {
            String message = "Incoming call from " + number;
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            Log.i(TAG, message);
        }

        // Answer call
        else if (state.equals(OFFHOOK) && lastState.equals(RINGING)) {
            String message = "Answered call from " + number;
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            Log.i(TAG, message);
        }

        // Outgoing call
        else if (state.equals(OFFHOOK) && lastState.equals(IDLE)) {
            Log.i(TAG, "To: " + number);
           // this.setResultData("0123456789"); todo make this work
            Toast.makeText(context, "Outgoing call to " + number, Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Outgoing call to " + number);
        }

        // Hang up
        else if (state.equals(IDLE) && lastState.equals(OFFHOOK)) {
            String message = "Terminated call with " + number;
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            Log.i(TAG, message);
        }

        // Reject call
        else if (state.equals(IDLE) && lastState.equals(RINGING)){
            String message = "Rejected call with " + number;
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            Log.i(TAG, message);
        }

        lastState = state;

    }
}

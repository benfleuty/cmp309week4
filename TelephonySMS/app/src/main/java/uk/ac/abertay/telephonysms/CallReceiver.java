package uk.ac.abertay.telephonysms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class CallReceiver extends BroadcastReceiver {

    final String TAG = this.getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            if (phoneNumber == null) return;

            Toast.makeText(context, "State: " + state + " Number: " + phoneNumber, Toast.LENGTH_SHORT).show();
        }
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            String oldNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Log.e(TAG, "To: " + oldNumber);
            this.setResultData("0123456789");
        }
    }

    /*

    final String TAG = "BroadcastReceiver";

    private static final String RINGING = TelephonyManager.EXTRA_STATE_RINGING;
    private static final String OFFHOOK = TelephonyManager.EXTRA_STATE_OFFHOOK;
    private static final String IDLE = TelephonyManager.EXTRA_STATE_IDLE;
    String lastState = IDLE;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        // todo check if this is no longer needed
        if (intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER) == null) {
            Log.d("DEBUG","Rejecting call as phone number is null");
            return;
        }

        if (action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            actionPhoneStateChanged(context, intent);
        }
    }
    private void actionPhoneStateChanged(Context context, Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        final String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

        Log.i(TAG, "State is " + state + " and last state was " + lastState);

        String message = "error";

        // Incoming Call
        if (state.equals(RINGING) && lastState.equals(IDLE))
            message = "Incoming call from " + number;

        // Answer call
        else if (state.equals(OFFHOOK) && lastState.equals(RINGING))
            message = "Answered call from " + number;

        // Outgoing call
        else if (state.equals(OFFHOOK) && lastState.equals(IDLE))
            Log.i(TAG, "Outgoing call to " + number);

        // Hang up
        else if (state.equals(IDLE) && lastState.equals(OFFHOOK))
            message = "Terminated call with " + number;

        // Reject call
        else if (state.equals(IDLE) && lastState.equals(RINGING))
            message = "Rejected call with " + number;

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        Log.d(TAG,message);

        lastState = state;
    }
 */
}

package uk.ac.abertay.telephonysms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class IncomingSmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if(bundle != null)
        {
            Object[] pdus = (Object[])bundle.get("pdus");
            String format = (String)bundle.get("format");
            SmsMessage[] messages = new SmsMessage[pdus.length];

            for(int i = 0; i < pdus.length; i++){
                messages[i] = SmsMessage.createFromPdu((byte[])pdus[i], format);
                String message = messages[i].getMessageBody();
                String address = messages[i].getOriginatingAddress();

                Toast.makeText(context, "Received *"+message + "* FROM " + address, Toast.LENGTH_SHORT).show();
            }
        }
    }
}

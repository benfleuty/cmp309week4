package uk.ac.abertay.telephonysms;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SmsSender extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_sender);
        registerSmsSender();
        sendSms();
    }

    private void sendSms() {
        if (!checkSendSmsPermission()) return;

        String phoneNumber = ((EditText) findViewById(R.id.txtPhoneNumber)).getText().toString();
        String message  = ((EditText) findViewById(R.id.)).getText().toString();

        Intent sentIntent = new Intent("SENT_SMS_ACTION");
        PendingIntent sentPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 5, sentIntent, 0);
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage();
    }

    private void registerSmsSender() {
        // don't register the listener if not permitted to send
        if (!checkSendSmsPermission()) return;
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        successfulSmsMessage();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        unsuccessfulSmsMessage();
                        break;
                }
            }
        }, new IntentFilter("SENT_SMS_ACTION"));
    }

    private boolean checkSendSmsPermission() {
        if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        // permission not granted
        Toast.makeText(this, "You have not granted permission for this app to send SMS!", Toast.LENGTH_SHORT).show();
        // try ask for permission
        if (shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS)) {
            requestPermissions(new String[]{Manifest.permission.SEND_SMS}, MainActivity.REQUEST_CODE_RECEIVE_SMS);
            return false;
        }
        // otherwise the user must do so manually
        Toast.makeText(this, "To use this feature, you need to manually enable this in settings!", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    private void unsuccessfulSmsMessage() {
        Toast.makeText(this, "There was an error and your SMS could not be sent!", Toast.LENGTH_SHORT).show();
    }

    private void successfulSmsMessage() {
        Toast.makeText(this, "Your SMS was successfully sent!", Toast.LENGTH_SHORT).show();
    }


    public void btnSelectContact_onClick(View view) {
    }

    public void btnSendSms_onClick(View view) {
    }
}
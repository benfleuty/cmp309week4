package uk.ac.abertay.telephonysms;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SmsSender extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_sender);
        registerSmsSender();
    }

    private static final int GET_CONTACT_REQUEST = 1;

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

    private void sendSms() {
        if (!checkSendSmsPermission()) return;

        String phoneNumber = ((EditText) findViewById(R.id.textSmsRecipient)).getText().toString();
        String message = ((EditText) findViewById(R.id.textSmsMessage)).getText().toString();

        Intent sentIntent = new Intent("SENT_SMS_ACTION");
        PendingIntent sentPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 5, sentIntent, 0);
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber,null,message,sentPendingIntent,null);
    }

    public void btnSendSms_onClick(View view) {
        getAContact();
    }

    private void getAContact() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(intent, GET_CONTACT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;
        if (requestCode == GET_CONTACT_REQUEST && resultCode == RESULT_OK) {
            getContactNumber(data);
        }
    }

    private void getContactNumber(Intent intent) {
        // 1. Get data from Intent
        Uri contactUri = intent.getData();
        // 2. Create a search filter for all values we need by putting their types into an array.
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.NUMBER // we only use 1 here - the phone number
        };
        // 3. Run a query on the SQLite Database containing all contacts using the parameters from above
        // 	  ignore the last 3 nulls for now.
        Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);
        // 4. If the search returned a valid result in the cursor object, get the phone number.
        if (cursor != null && cursor.moveToFirst()) {
            // Since we will only have 1 entry for the phone number,
            // no need to loop through the entire table, just get the FIRST row.
            int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER); // find out what the index for the phone number column is
            String number = cursor.getString(numberIndex); // use this index to get the string value from the column in the current row
            /* Don't forget to change the "inputFieldID" to the id of your EditText field */
            ((EditText) findViewById(R.id.textSmsRecipient)).setText(number); // set the text of the input field to the phone number string
        }
    }
}
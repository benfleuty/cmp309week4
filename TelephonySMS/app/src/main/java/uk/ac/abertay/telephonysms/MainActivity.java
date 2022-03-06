package uk.ac.abertay.telephonysms;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_READ_PHONE_STATE = 1;
    private static final int REQUEST_CODE_READ_CALL_LOG = 2;
    private static final int REQUEST_CODE_RECEIVE_SMS = 3;
    private static final int REQUEST_CODE_READ_SMS = 4;
    private static final int REQUEST_CODE_SEND_SMS = 5;

    private final BroadcastReceiver MAIN_BROADCAST_RECEIVER = new CallReceiver();
    private final IncomingSmsReceiver MAIN_SMS_RECEIVER = new IncomingSmsReceiver();

    private final HashMap<String, Boolean> permissionAlreadyRequested = new HashMap<String, Boolean>() {{
        put("READ_PHONE_STATE", false);
        put("READ_CALL_LOG", false);
        put("RECEIVE_SMS", false);
        put("SEND_SMS", false);
        put("READ_SMS", false);
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceivers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceivers();
    }

    private void registerReceivers() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PHONE_STATE");
        filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        filter.addAction("android.intent.action.PROCESS_OUTGOING_CALLS");
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(MAIN_BROADCAST_RECEIVER, filter);

        filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(MAIN_SMS_RECEIVER, filter);
    }

    private void unregisterReceivers() {
        unregisterReceiver(MAIN_BROADCAST_RECEIVER);
        unregisterReceiver(MAIN_SMS_RECEIVER);
    }

    private void checkPermissions() {
        checkPhoneStatePermission();
        checkReadCallLogPermission();
        checkReceiveSmsPermission();
        checkReadSmsPermission();
        checkSendSmsPermission();
    }

    private void checkReceiveSmsPermission() {
        int checkResult = checkSelfPermission(Manifest.permission.RECEIVE_SMS);
        switch (checkResult) {
            case android.content.pm.PackageManager.PERMISSION_DENIED:
                permissionReceiveSmsDenied();
                break;
            case android.content.pm.PackageManager.PERMISSION_GRANTED:
                permissionReceiveSmsGranted();
                break;
        }
    }

    private void permissionReceiveSmsDenied() {
        if (permissionAlreadyRequested.getOrDefault("RECEIVE_SMS", true)) return;
        if (shouldShowRequestPermissionRationale(Manifest.permission.RECEIVE_SMS)) {
            permissionAlreadyRequested.put("RECEIVE_SMS",true);
            showExplanation("Permission Request", "We need this permission to display information about your network operator", Manifest.permission.RECEIVE_SMS, REQUEST_CODE_RECEIVE_SMS);
        } else {
            requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS}, REQUEST_CODE_RECEIVE_SMS);
        }

    }

    private void permissionReceiveSmsGranted() {
    }

    private void checkReadSmsPermission() {

        int checkResult = checkSelfPermission(Manifest.permission.RECEIVE_SMS);
        switch (checkResult) {
            case android.content.pm.PackageManager.PERMISSION_DENIED:
                permissionReadSmsDenied();
                break;
            case android.content.pm.PackageManager.PERMISSION_GRANTED:
                permissionReadSmsGranted();
                break;
        }
    }

    private void permissionReadSmsGranted() {
    }

    private void permissionReadSmsDenied() {
        if (permissionAlreadyRequested.getOrDefault("READ_SMS", true)) return;
        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_SMS)) {
            permissionAlreadyRequested.put("READ_SMS",true);
            showExplanation("Permission Request", "We need this permission to display information about your network operator", Manifest.permission.READ_PHONE_STATE, REQUEST_CODE_READ_PHONE_STATE);
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_SMS}, REQUEST_CODE_READ_SMS);
        }
    }

    private void checkSendSmsPermission() {
        int checkResult = checkSelfPermission(Manifest.permission.RECEIVE_SMS);
        switch (checkResult) {
            case android.content.pm.PackageManager.PERMISSION_DENIED:
                permissionSendSmsDenied();
                break;
            case android.content.pm.PackageManager.PERMISSION_GRANTED:
                permissionSendSmsGranted();
                break;
        }
    }

    private void permissionSendSmsGranted() {
    }

    private void permissionSendSmsDenied() {
        if (permissionAlreadyRequested.getOrDefault("SEND_SMS", true)) return;
        if (shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS)) {
            permissionAlreadyRequested.put("SEND_SMS",true);
            showExplanation("Permission Request", "We need this permission to send messages", Manifest.permission.SEND_SMS, REQUEST_CODE_SEND_SMS);
        } else {
            requestPermissions(new String[]{Manifest.permission.SEND_SMS}, REQUEST_CODE_SEND_SMS);
        }
    }

    private void checkPhoneStatePermission() {
        int checkResult = checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
        switch (checkResult) {
            case android.content.pm.PackageManager.PERMISSION_DENIED:
                permissionPhoneStateDenied();
                break;
            case android.content.pm.PackageManager.PERMISSION_GRANTED:
                permissionPhoneStateGranted();
                break;
        }
    }

    private void checkReadCallLogPermission() {
        int checkResult = checkSelfPermission(Manifest.permission.READ_CALL_LOG);
        switch (checkResult) {
            case android.content.pm.PackageManager.PERMISSION_DENIED:
                permissionReadCallLogDenied();
                break;
            case android.content.pm.PackageManager.PERMISSION_GRANTED:
                permissionReadCallLogGranted();
                break;
        }
    }

    private void permissionReadCallLogGranted() {
    }

    private void permissionReadCallLogDenied() {
        if (permissionAlreadyRequested.getOrDefault("READ_CALL_LOG", true)) return;
        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CALL_LOG)) {
            permissionAlreadyRequested.put("READ_CALL_LOG",true);
            showExplanation("Permission Request", "We need this permission to handle call information", Manifest.permission.READ_CALL_LOG, REQUEST_CODE_READ_CALL_LOG);
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_CALL_LOG}, REQUEST_CODE_READ_CALL_LOG);
        }
    }

    private void permissionPhoneStateDenied() {
        if (permissionAlreadyRequested.getOrDefault("READ_PHONE_STATE", true)) return;
        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {
            permissionAlreadyRequested.put("READ_PHONE_STATE",true);
            showExplanation("Permission Request", "We need this permission to display information about your network operator", Manifest.permission.READ_PHONE_STATE, REQUEST_CODE_READ_PHONE_STATE);
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE_READ_PHONE_STATE);
        }
    }

    // from https://stackoverflow.com/questions/35484767/activitycompat-requestpermissions-not-showing-dialog-box/35486162#35486162
    private void showExplanation(String title, String message, final String permission, final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermissions(new String[]{permission}, permissionRequestCode);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
                        dialogInterface.cancel();
                    }
                });
        builder.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_READ_PHONE_STATE:
                handlePhoneStatePermissionChanged(grantResults);
                break;
        }
    }

    private void handlePhoneStatePermissionChanged(int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            permissionPhoneStateGranted();
        } else {
            permissionPhoneStateDenied();
        }
    }

    private void permissionPhoneStateGranted() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
        boolean resultOK = checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;

        if (resultOK) {
            String operatorId = telephonyManager.getNetworkOperator();
            String countryCode = telephonyManager.getNetworkCountryIso();
            String networkName = telephonyManager.getNetworkOperatorName();
            String phoneNumber = null;
            try {
                phoneNumber = telephonyManager.getLine1Number();
            } catch (Exception e) {
                phoneNumber = "Unknown";
            }
            TextView textCC = findViewById(R.id.textCountryCode);
            TextView textOid = findViewById(R.id.textOperatorID);
            TextView textNetName = findViewById(R.id.textNetworkName);
            TextView textPhone = findViewById(R.id.textCurrentPhoneNumber);

            textCC.setText(countryCode);
            textOid.setText(operatorId);
            textNetName.setText(networkName);
            textPhone.setText(phoneNumber);
        }
    }
}